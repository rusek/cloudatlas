package pl.edu.mimuw.cloudatlas.islands;

public interface TimerFeedbackEndpoint<O> {
	
	public void fire(O object);

}
