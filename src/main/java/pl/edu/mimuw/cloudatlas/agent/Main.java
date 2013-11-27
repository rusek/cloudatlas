package pl.edu.mimuw.cloudatlas.agent;

import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.edu.mimuw.cloudatlas.islands.MotherIsland;
import pl.edu.mimuw.cloudatlas.islands.MotherTube;
import pl.edu.mimuw.cloudatlas.islands.PluggableIslandExecutor;
import pl.edu.mimuw.cloudatlas.zones.ZoneNames;


public class Main {
	
	private static final Logger log = LogManager.getLogger();
	
	private Properties loadProperties() throws IOException {
		Properties properties = new Properties();
		properties.load(Main.class.getClassLoader().getResourceAsStream("agent.properties"));
		return properties;
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
		Properties properties = loadProperties();
		String zoneName = getZoneName(properties);
		
		MotherIsland motherIsland = new MotherIsland();
		PluggableIslandExecutor islandExecutor = new PluggableIslandExecutor(motherIsland);
		
		StateIsland stateIsland = new StateIsland(zoneName);
		islandExecutor.addIsland(stateIsland);
		
		CommandFacadeIsland commandFacadeIsland = new CommandFacadeIsland();
		islandExecutor.addIsland(commandFacadeIsland);
		
		MotherTube.entangle(commandFacadeIsland, motherIsland);
		StateTube.entangle(commandFacadeIsland, stateIsland);
		
		motherIsland.spinCarouselUntilInterrupted();
		
		islandExecutor.destroy();

		log.info("Exiting main()");
	}
	
	public static void main(String[] args) throws Exception {
		new Main().execute(args);
	}
}
