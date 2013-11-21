package pl.edu.mimuw.cloudatlas.islands;

public interface TimerIsland extends Island {
	
	public <O> TimerEndpoint<O> mountTimerFeedback(final TimerFeedbackEndpoint<O> feedbackEndpoint);
}
