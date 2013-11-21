package pl.edu.mimuw.cloudatlas.islands;

public interface TimerFeedbackIsland<O> extends Island {

	public TimerFeedbackEndpoint<O> mountTimer(TimerEndpoint<O> timerEndpoint);
}
