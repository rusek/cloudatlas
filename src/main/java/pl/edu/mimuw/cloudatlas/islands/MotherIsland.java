package pl.edu.mimuw.cloudatlas.islands;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MotherIsland extends ManualIsland {
	
	private static Logger log = LogManager.getLogger();
	
	private State state = State.VIRGIN;
	private List<ChildEndpoint> childEndpoints = new ArrayList<ChildEndpoint>();
	private int numAwakenChildren = 0;
	
	public MotherIsland() {
		getCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				wakeUpChildren();
			}
			
		});
	}
	
	public void throwException(final Exception ex) {
		// Someone was very naughty. Mommy is very angry and will crash the whole application now.
		
		ex.printStackTrace();
		System.exit(1);
	}
	
	private void wakeUpChildren() {
		assert state == State.VIRGIN;
		
		log.info("Igniting system.");
		
		state = State.IGNITED;
		numAwakenChildren = childEndpoints.size();
		for (ChildEndpoint childEndpoint : childEndpoints) {
			childEndpoint.wakeUp();
		}
	}
	
	private void tryInitiateExtinguishing() {
		assert state.compareTo(State.IGNITED) >= 0;
		
		if (state.equals(State.IGNITED)) {
			log.info("Entering extinguishing state.");
			
			state = State.EXTINGUISHING;
			for (ChildEndpoint childEndpoint : childEndpoints) {
				childEndpoint.goToBed();
			}
			tryCompleteExtinguishing();
		}
	}
	
	private void tryCompleteExtinguishing() {
		assert state.equals(State.EXTINGUISHING);
		
		if (numAwakenChildren == 0) {
			log.info("System extinguished.");
			
			Thread.currentThread().interrupt();
		}
	}
	
	public MotherEndpoint mountChild(ChildEndpoint childEndpoint) {
		childEndpoints.add(childEndpoint);
		
		return new MotherEndpoint() {

			@Override
			public void stop() {
				tryInitiateExtinguishing();
			}

			@Override
			public void wentToBed() {
				numAwakenChildren--;
				tryCompleteExtinguishing();
			}};
	}
	
	private static enum State {
		VIRGIN, IGNITED, EXTINGUISHING, EXTINGUISHED;
	}
}
