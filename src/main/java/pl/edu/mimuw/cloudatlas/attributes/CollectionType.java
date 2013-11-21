package pl.edu.mimuw.cloudatlas.attributes;

public abstract class CollectionType<V extends SimpleValue, C extends CollectionValue<V>> extends Type<C> {

	private static final long serialVersionUID = 1L;
	
	protected final SimpleType<V> itemType;
	
	protected CollectionType(SimpleType<V> itemType) {
		assert itemType != null;
		
		this.itemType = itemType;
	}
	
	public SimpleType<V> getItemType() {
		return itemType;
	}
}

