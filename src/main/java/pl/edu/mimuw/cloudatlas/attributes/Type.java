package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;

public abstract class Type<V extends Value> {
	
	public abstract void compactWrite(DataOutput output) throws IOException;
	
	public abstract V compactReadValue(DataInput input) throws IOException;
	
	public boolean isComparable() {
		return false;
	}

	public abstract int hashCode();
	public abstract boolean equals(Object obj);
	
	public boolean equalsAllValueTypes(Collection<? extends Value> values) {
		for (Value value : values) {
			if (value != null && !equals(value.getType())) {
				return false;
			}
		}
		return true;
	}
}
