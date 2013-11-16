package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ListType<V extends SimpleValue> extends CollectionType<ListValue<V>> {

	private final SimpleType<V> itemType;
	
	private ListType(SimpleType<V> itemType) {
		assert itemType != null;
		
		this.itemType = itemType;
	}

	public SimpleType<V> getItemType() {
		return itemType;
	}

	@Override
	public ListValue<V> compactReadValue(DataInput input) throws IOException {
		int length = input.readInt();
		if (length < 0) {
			throw new IOException("Negative length");
		}
		
		ListValue<V> list = ListValue.of(itemType);
		for (int i = 0; i < length; i++) {
			list.addItem(itemType.compactReadValue(input));
		}

		return list;
	}
	
	public static <V extends SimpleValue> ListType<V> of(SimpleType<V> itemType) {
		return new ListType<V>(itemType);
	}

	@Override
	public void compactWrite(DataOutput output) throws IOException {
		Types.compactWriteType(itemType, output);
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
		ListType other = (ListType) obj;
		if (itemType == null) {
			if (other.itemType != null)
				return false;
		} else if (!itemType.equals(other.itemType))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "ListType[" + itemType + "]";
	}
}
