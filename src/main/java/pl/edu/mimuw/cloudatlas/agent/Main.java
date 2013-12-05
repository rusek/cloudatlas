package pl.edu.mimuw.cloudatlas.agent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	
	private Properties loadProperties() throws IOException {
		Properties properties = new Properties();
		properties.load(Main.class.getClassLoader().getResourceAsStream("agent.properties"));
		return properties;
	}
	
	private Properties loadProperties(String fileName) throws IOException {
		FileInputStream inputStream = new FileInputStream(fileName);
		try {
			Properties properties = new Properties();
			properties.load(inputStream);
			return properties;
		} finally {
			try {
				inputStream.close();
			} catch (IOException ex) {
				
			}
		}
	}
	
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
		Properties properties = args.length > 0 ? loadProperties(args[0]) : loadProperties();
		String zoneName = getZoneName(properties);
		
		MotherIsland motherIsland = new MotherIsland();
		PluggableIslandExecutor islandExecutor = new PluggableIslandExecutor(motherIsland);
		
		StateIsland stateIsland = new StateIsland(zoneName);
		islandExecutor.addIsland(stateIsland);
		
		CommandFacadeIsland commandFacadeIsland = new CommandFacadeIsland(zoneName);
		islandExecutor.addIsland(commandFacadeIsland);
		
		DatagramSocketIsland socketIsland = new DatagramSocketIsland(properties);
		islandExecutor.addIsland(socketIsland);
		
		TimerIslandImpl timerIsland = new TimerIslandImpl();
		
		MotherTube.entangle(commandFacadeIsland, motherIsland);
		MotherTube.entangle(socketIsland, motherIsland);
		StateTube.entangle(commandFacadeIsland, stateIsland);
		TimerTube.entangle(socketIsland, timerIsland);
		
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
