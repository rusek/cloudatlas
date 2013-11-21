package pl.edu.mimuw.cloudatlas.islands;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MotherIsland extends FatIsland {
	
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
		
		log.info("Waking up islands");
		state = State.RUNNING;
		numAwakenChildren = childEndpoints.size();
		for (ChildEndpoint childEndpoint : childEndpoints) {
			childEndpoint.wakeUp();
		}
	}
	
	private void tryInitiateStop() {
		assert state.compareTo(State.RUNNING) >= 0;
		
		if (state.equals(State.RUNNING)) {
			log.info("Initiating stop");
			
			state = State.STOPPING;
			for (ChildEndpoint childEndpoint : childEndpoints) {
				childEndpoint.goToBed();
			}
			tryCompleteStop();
		}
	}
	
	private void tryCompleteStop() {
		assert state.equals(State.STOPPING);
		
		if (numAwakenChildren == 0) {
			log.info("Stopped");
			Thread.currentThread().interrupt(); // TODO destroy self
		}
	}
	
	public MotherEndpoint mountChild(ChildEndpoint childEndpoint) {
		childEndpoints.add(childEndpoint);
		
		return new MotherEndpoint() {

			@Override
			public void stop() {
				tryInitiateStop();
			}

			@Override
			public void wentToBed() {
				numAwakenChildren--;
				tryCompleteStop();
			}};
	}
	
	private static enum State {
		VIRGIN, RUNNING, STOPPING, STOPPED;
	}
}
