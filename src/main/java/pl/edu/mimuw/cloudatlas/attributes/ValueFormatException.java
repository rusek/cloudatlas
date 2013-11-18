package pl.edu.mimuw.cloudatlas.attributes;

public class ValueFormatException extends Exception {

	private static final long serialVersionUID = 1L;

	public ValueFormatException() {
		super();
	}

	public ValueFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValueFormatException(String message) {
		super(message);
	}

	public ValueFormatException(Throwable cause) {
		super(cause);
	}

}
