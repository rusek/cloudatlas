package pl.edu.mimuw.cloudatlas.agent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.edu.mimuw.cloudatlas.islands.MotherIsland;
import pl.edu.mimuw.cloudatlas.islands.MotherTube;
import pl.edu.mimuw.cloudatlas.islands.PluggableIslandExecutor;
import pl.edu.mimuw.cloudatlas.islands.TimerIsland;
import pl.edu.mimuw.cloudatlas.islands.TimerIslandImpl;
import pl.edu.mimuw.cloudatlas.islands.TimerTube;


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
		
		try {
			motherIsland.runForever();
		} catch (InterruptedException e) {
			log.info("interrupted");
		}
		
		islandExecutor.destroy();
		log.info("closed!");
	}
}
