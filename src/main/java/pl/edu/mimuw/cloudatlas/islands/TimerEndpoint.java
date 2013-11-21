package pl.edu.mimuw.cloudatlas.islands;

public interface TimerEndpoint<O> {
	
	public void schedule(O object, long delay);
}
