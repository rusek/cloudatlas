package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SetValue<V extends SimpleValue> extends CollectionValue<V> {

	private static final long serialVersionUID = 1L;
	
	private final SimpleType<V> itemType;
	private final Set<V> items = new HashSet<V>();
	
	private SetValue(SimpleType<V> itemType) {
		this.itemType = itemType;
	}
	
	public Set<V> getItems() {
		return items;
	}
	
	public void addItem(V item) {
		items.add(item);
	}
	
	@Override
	public int size() {
		return items.size();
	}

	@Override
	public void compactWrite(DataOutput output) throws IOException {
		output.writeInt(items.size());
		for (V item : items) {
			item.compactWrite(output);
		}
	}

	@Override
	public SetType<V> getType() {
		return SetType.of(itemType);
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
		SetValue other = (SetValue) obj;
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
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{ ");
		Iterator<V> it = items.iterator();
		if (it.hasNext()) {
			builder.append(it.next());
			while (it.hasNext()) {
				builder.append(", ").append(it.next());
			}
		}
		builder.append(" }");
		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public SetValue<V> deepCopy() {
		SetValue<V> copy = new SetValue<V>(itemType);
		for (V item : items) {
			copy.addItem((V) item.deepCopy());
		}
		return copy;
	}

	public static <V extends SimpleValue> SetValue<V> of(SimpleType<V> itemType) {
		return new SetValue<V>(itemType);
	}
}
