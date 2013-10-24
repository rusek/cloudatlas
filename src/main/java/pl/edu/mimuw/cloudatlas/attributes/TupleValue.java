package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class TupleValue extends Value {
	
	private final TupleType type;
	private List<SimpleValue> values;
	
	public TupleValue(TupleType type, List<SimpleValue> values) {
		assert type != null;
		assert values != null;
		
		this.type = type;
		this.values = values;
	}

	@Override
	public void compactWrite(DataOutput output) throws IOException {
		for (SimpleValue value : values) {
			value.compactWrite(output);
		}
	}

	@Override
	public Type<? extends Value> getType() {
		return type;
	}

}
