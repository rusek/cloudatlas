package pl.edu.mimuw.cloudatlas.agent;

import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.edu.mimuw.cloudatlas.islands.MotherIsland;
import pl.edu.mimuw.cloudatlas.islands.MotherTube;
import pl.edu.mimuw.cloudatlas.islands.PluggableIslandExecutor;
import pl.edu.mimuw.cloudatlas.islands.TimerIslandImpl;
import pl.edu.mimuw.cloudatlas.islands.TimerTube;
import pl.edu.mimuw.cloudatlas.zones.ZoneNames;


public class Main {
	
	private static final Logger log = LogManager.getLogger();
	
	private String getZoneName(Properties properties) {
		String zoneName = properties.getProperty("zoneName");
		if (zoneName == null) {
			throw new RuntimeException("Missing zoneName key");
		}
		if (!ZoneNames.isGlobalName(zoneName)) {
			throw new RuntimeException("Invalid zone name: " + zoneName);
		}
		return zoneName;
	}
	
	public void execute(String[] args) throws IOException {
		Properties properties = args.length > 0 ? PropertyReader.loadProperties(args[0]) :
			PropertyReader.loadProperties(null);
		String zoneName = getZoneName(properties);
		
		MotherIsland motherIsland = new MotherIsland();
		PluggableIslandExecutor islandExecutor = new PluggableIslandExecutor(motherIsland);
		
		StateIsland stateIsland = new StateIsland(zoneName, properties);
		islandExecutor.addIsland(stateIsland);
		
		CommandFacadeIsland commandFacadeIsland = new CommandFacadeIsland(zoneName);
		islandExecutor.addIsland(commandFacadeIsland);
		
		DatagramSocketIsland socketIsland = new DatagramSocketIsland(zoneName, properties);
		islandExecutor.addIsland(socketIsland);
		
		TimerIslandImpl timerIsland = new TimerIslandImpl();
		
		MotherTube.entangle(commandFacadeIsland, motherIsland);
		MotherTube.entangle(socketIsland, motherIsland);
		MotherTube.entangle(stateIsland, motherIsland);
		StateTube.entangle(commandFacadeIsland, stateIsland);
		TimerTube.entangle(socketIsland, timerIsland);
		TimerTube.entangle(stateIsland, timerIsland);
		GossipTube.entangle(socketIsland, stateIsland);
		
		motherIsland.spinCarouselUntilInterrupted();
		
		islandExecutor.destroy();
		socketIsland.destroy();
		timerIsland.destroy();

		log.info("Exiting main()");
	}
	
	public static void main(String[] args) throws Exception {
		new Main().execute(args);
	}
}
