package pl.edu.mimuw.cloudatlas.islands;

import java.util.Timer;
import java.util.TimerTask;

public class TimerIslandImpl extends SynchronizedIsland implements TimerIsland {
	
	private final Timer timer = new Timer();
	
	public void destroy() {
		timer.cancel();
	}
	
	public <O> TimerEndpoint<O> mountTimerFeedback(final TimerFeedbackEndpoint<O> feedbackEndpoint) {
		return new TimerEndpoint<O>() {

			@Override
			public void schedule(final O object, long delay) {
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						feedbackEndpoint.fire(object);
					}
					
				}, delay);
			}
		};
	}
}
