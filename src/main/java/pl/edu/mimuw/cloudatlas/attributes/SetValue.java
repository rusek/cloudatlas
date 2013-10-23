package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SetValue<V extends SimpleValue> extends Value {
	
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

	public static <V extends SimpleValue> SetValue<V> of(SimpleType<V> itemType) {
		return new SetValue<V>(itemType);
	}
}
