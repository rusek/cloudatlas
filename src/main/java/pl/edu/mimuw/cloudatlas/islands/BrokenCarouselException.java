package pl.edu.mimuw.cloudatlas.islands;

public class BrokenCarouselException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BrokenCarouselException(String message, Throwable cause) {
		super(message, cause);
	}

	public BrokenCarouselException(String message) {
		super(message);
	}

	public BrokenCarouselException(Throwable cause) {
		super(cause);
	}

}
