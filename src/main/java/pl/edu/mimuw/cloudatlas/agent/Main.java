package pl.edu.mimuw.cloudatlas.agent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.edu.mimuw.cloudatlas.islands.MotherIsland;
import pl.edu.mimuw.cloudatlas.islands.MotherTube;
import pl.edu.mimuw.cloudatlas.islands.PluggableIslandExecutor;


public class Main {
	
	private static final Logger log = LogManager.getLogger();
	
	public static void main(String[] args) {
		MotherIsland motherIsland = new MotherIsland();
		PluggableIslandExecutor islandExecutor = new PluggableIslandExecutor(motherIsland);
		
		StateIsland stateIsland = new StateIsland();
		islandExecutor.addIsland(stateIsland);
		
		CommandFacadeIsland commandFacadeIsland = new CommandFacadeIsland();
		islandExecutor.addIsland(commandFacadeIsland);
		
		MotherTube.entangle(commandFacadeIsland, motherIsland);
		StateTube.entangle(commandFacadeIsland, stateIsland);
		
		motherIsland.spinCarouselUntilInterrupted();
		
		islandExecutor.destroy();

		log.info("Exiting main()");
	}
}
