package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TupleType extends Type<TupleValue> implements Iterable<SimpleType<? extends SimpleValue>> {
	
	private final List<SimpleType<? extends SimpleValue>> itemTypes =
			new ArrayList<SimpleType<? extends SimpleValue>>();
	
	public TupleType(List<SimpleType<? extends SimpleValue> > itemTypes) {
		assert itemTypes != null;
		
		this.itemTypes.addAll(itemTypes);
	}

	@Override
	public TupleValue compactReadValue(DataInput input) throws IOException {
		List<SimpleValue> values = new ArrayList<SimpleValue>();
		for (SimpleType<? extends Value> itemType : itemTypes) {
			values.add(itemType.compactReadValue(input));
		}
		
		return new TupleValue(this, values);
	}

	@Override
	public void compactWrite(DataOutput output) throws IOException {
		output.writeInt(itemTypes.size());
		for (SimpleType<? extends Value> itemType : itemTypes) {
			Types.compactWriteType(itemType, output);
		}
	}

	public Iterator<SimpleType<? extends SimpleValue>> iterator() {
		return itemTypes.iterator();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((itemTypes == null) ? 0 : itemTypes.hashCode());
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
		TupleType other = (TupleType) obj;
		if (itemTypes == null) {
			if (other.itemTypes != null)
				return false;
		} else if (!itemTypes.equals(other.itemTypes))
			return false;
		return true;
	}

}
