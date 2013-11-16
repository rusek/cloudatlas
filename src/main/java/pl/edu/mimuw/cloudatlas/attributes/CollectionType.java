package pl.edu.mimuw.cloudatlas.attributes;

public abstract class CollectionType<V> extends Type<CollectionValue> {
	
	@Override
	public boolean isCollection() {
		return true;
	}
}

