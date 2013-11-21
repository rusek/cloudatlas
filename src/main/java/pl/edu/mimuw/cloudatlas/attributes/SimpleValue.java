package pl.edu.mimuw.cloudatlas.attributes;

// Collections may contain only simple values
public abstract class SimpleValue extends Value {

	private static final long serialVersionUID = 1L;

	@Override
	public SimpleValue deepCopy() {
		// Simple values are immutable
		return this;
	}
}
