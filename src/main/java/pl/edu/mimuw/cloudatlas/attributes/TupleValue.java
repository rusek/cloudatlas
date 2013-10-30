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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
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
		TupleValue other = (TupleValue) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

}
