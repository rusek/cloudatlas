package pl.edu.mimuw.cloudatlas.islands;

public class TimerTube<O> extends Tube<TimerFeedbackEndpoint<O>, TimerEndpoint<O>> implements 
		TimerFeedbackEndpoint<O>, TimerEndpoint<O> {

	@Override
	public void schedule(final O object, final long delay) {
		getRightCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getRightEndpoint().schedule(object, delay);
			}
			
		});
	}

	@Override
	public void fire(final O object) {
		getLeftCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getLeftEndpoint().fire(object);
			}
			
		});
	}

	public static <O> TimerTube<O> entangle(TimerFeedbackIsland<O> feedbackIsland, TimerIsland timerIsland) {
		TimerTube<O> tube = new TimerTube<O>();
		tube.setLeft(feedbackIsland.getCarousel(), feedbackIsland.mountTimer(tube));
		tube.setRight(timerIsland.getCarousel(), timerIsland.mountTimerFeedback(tube));
		return tube;
	}
}
