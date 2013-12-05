package pl.edu.mimuw.cloudatlas.islands;

// Wrapper for checked exceptions that should be propagated up to the worker thread
// (and then to MotherIsland.throwException)
public class IslandException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IslandException(Throwable cause) {
		super(cause);
	}

}
