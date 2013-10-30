package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListValue<V extends SimpleValue> extends Value {
	
	private final SimpleType<V> itemType;
	private final List<V> items = new ArrayList<V>();
	
	private ListValue(SimpleType<V> itemType) {
		this.itemType = itemType;
	}
	
	public List<V> getItems() {
		return items;
	}
	
	public void addItem(V item) {
		items.add(item);
	}

	@Override
	public void compactWrite(DataOutput output) throws IOException {
		output.writeInt(items.size());
		for (V item : items) {
			item.compactWrite(output);
		}
	}

	@Override
	public ListType<V> getType() {
		return ListType.of(itemType);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((itemType == null) ? 0 : itemType.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
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
		ListValue other = (ListValue) obj;
		if (itemType == null) {
			if (other.itemType != null)
				return false;
		} else if (!itemType.equals(other.itemType))
			return false;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return true;
	}

	public static <V extends SimpleValue> ListValue<V> of(SimpleType<V> itemType) {
		return new ListValue<V>(itemType);
	}
}
