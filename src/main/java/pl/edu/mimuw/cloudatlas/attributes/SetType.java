package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataInput;
import java.io.IOException;

public class SetType<V extends SimpleValue> extends Type<SetValue<V>> {
	
	private final SimpleType<V> itemType;
	
	private SetType(SimpleType<V> itemType) {
		assert itemType != null;
		
		this.itemType = itemType;
	}

	@Override
	public SetValue<V> compactReadValue(DataInput input) throws IOException {
		int length = input.readInt();
		if (length < 0) {
			throw new IOException("Negative length");
		}
		
		SetValue<V> set = SetValue.of(itemType);
		for (int i = 0; i < length; i++) {
			set.addItem(itemType.compactReadValue(input));
		}

		return set;
	}
	
	public static <V extends SimpleValue> SetType<V> of(SimpleType<V> itemType) {
		return new SetType<V>(itemType);
	}

}
