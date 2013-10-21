package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataInput;
import java.io.IOException;

public class ListType<V extends SimpleValue> extends Type<ListValue<V>> {
	
	private final SimpleType<V> itemType;
	
	private ListType(SimpleType<V> itemType) {
		this.itemType = itemType;
	}

	@Override
	public ListValue<V> compactReadValue(DataInput input) throws IOException {
		int length = input.readInt();
		ListValue<V> list = ListValue.of(itemType);
		for (int i = 0; i < length; i++) {
			list.addItem(itemType.compactReadValue(input));
		}

		return list;
	}
	
	public static <V extends SimpleValue> ListType<V> of(SimpleType<V> itemType) {
		return new ListType<V>(itemType);
	}

}
