package pl.edu.mimuw.cloudatlas.attributes;

public abstract class CollectionType<V extends SimpleValue> extends Type<CollectionValue<V>> {
	
	protected final SimpleType<V> itemType;
	
	protected CollectionType(SimpleType<V> itemType) {
		assert itemType != null;
		
		this.itemType = itemType;
	}
	
	public SimpleType<V> getItemType() {
		return itemType;
	}
}

