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

	public static <V extends SimpleValue> ListValue<V> of(SimpleType<V> itemType) {
		return new ListValue<V>(itemType);
	}
}
