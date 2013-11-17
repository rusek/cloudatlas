package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SetType<V extends SimpleValue> extends CollectionType<V, SetValue<V>> {

	private SetType(SimpleType<V> itemType) {
		super(itemType);
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
		SetType other = (SetType) obj;
		if (itemType == null) {
			if (other.itemType != null)
				return false;
		} else if (!itemType.equals(other.itemType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SetType[" + itemType + "]";
	}
}
