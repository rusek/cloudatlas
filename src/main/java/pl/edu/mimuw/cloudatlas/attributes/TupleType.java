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

}
