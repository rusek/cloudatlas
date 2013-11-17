package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class CollectionType<V extends SimpleValue> extends Type<CollectionValue<V>> {
	
	protected final SimpleType<V> itemType;
	
	protected CollectionType(SimpleType<V> itemType) {
		assert itemType != null;
		
		this.itemType = itemType;
	}
	
	@Override
	public boolean isCollection() {
		return true;
	}
	
	public SimpleType<V> getItemType() {
		return itemType;
	}
	
	public static <V extends SimpleValue> CollectionType<V> of(SimpleType<V> itemType) {
		return new CollectionType<V>(itemType);
	}

	@Override
	public void compactWrite(DataOutput output) throws IOException {
		Types.compactWriteType(itemType, output);
	}

	@Override
	public CollectionValue<V> compactReadValue(DataInput input)
			throws IOException {
		throw new UnsupportedOperationException("compactReadValue() is not supported for CollectionType.");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((itemType == null) ? 0 : itemType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		CollectionType other = (CollectionType) obj;
		if (itemType == null) {
			if (other.itemType != null)
				return false;
		} else if (!itemType.equals(other.itemType))
			return false;
		return true;
	}
}

