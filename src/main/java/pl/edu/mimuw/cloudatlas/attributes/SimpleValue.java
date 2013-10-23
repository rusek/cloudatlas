package pl.edu.mimuw.cloudatlas.attributes;

// Collections may contain only simple values
public abstract class SimpleValue extends Value {

	@Override
	public abstract boolean equals(Object other);
	
	@Override
	public abstract int hashCode();
}
