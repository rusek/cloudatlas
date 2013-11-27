package pl.edu.mimuw.cloudatlas.islands;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Mommy manages all the islands - she initiates system ignition and coordinates extinguishing. 
// You should never mess with mommy.
public class MotherIsland extends ManualIsland {
	
	private static Logger log = LogManager.getLogger();
	
	private State state = State.VIRGIN;
	private List<ChildEndpoint> childEndpoints = new ArrayList<ChildEndpoint>();
	private int numIgnitedChildren = 0;
	
	public MotherIsland() {
		getCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				ignite();
			}
			
		});
	}
	
	public void throwException(final Exception ex) {
		// Someone was very naughty. Mommy is very angry and will crash the whole application now.
		
		ex.printStackTrace();
		System.exit(1);
	}
	
	private void ignite() {
		assert state == State.VIRGIN;
		
		log.info("Igniting system.");
		
		state = State.IGNITED;
		numIgnitedChildren = childEndpoints.size();
		for (ChildEndpoint childEndpoint : childEndpoints) {
			childEndpoint.ignite();
		}
	}
	
	private void tryInitiateExtinguishing() {
		assert state.compareTo(State.IGNITED) >= 0;
		
		if (state.equals(State.IGNITED)) {
			log.info("Entering extinguishing state.");
			
			state = State.EXTINGUISHING;
			for (ChildEndpoint childEndpoint : childEndpoints) {
				childEndpoint.extinguish();
			}
			tryCompleteExtinguishing();
		}
	}
	
	private void tryCompleteExtinguishing() {
		assert state.equals(State.EXTINGUISHING);
		
		if (numIgnitedChildren == 0) {
			log.info("System extinguished.");
			
			Thread.currentThread().interrupt();
		}
	}
	
	public MotherEndpoint mountChild(ChildEndpoint childEndpoint) {
		childEndpoints.add(childEndpoint);
		
		return new MotherEndpoint() {

			@Override
			public void initiateExtinguishing() {
				tryInitiateExtinguishing();
			}

			@Override
			public void childExtinguished() {
				numIgnitedChildren--;
				tryCompleteExtinguishing();
			}};
	}
	
	private static enum State {
		VIRGIN, IGNITED, EXTINGUISHING, EXTINGUISHED;
	}
}
