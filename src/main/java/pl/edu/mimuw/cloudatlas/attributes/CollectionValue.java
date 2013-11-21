package pl.edu.mimuw.cloudatlas.attributes;

public abstract class CollectionValue<V extends SimpleValue> extends Value {

	private static final long serialVersionUID = 1L;
	
	public abstract int size();

}
