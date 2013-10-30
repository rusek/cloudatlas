package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataOutput;
import java.io.IOException;

public class IntegerValue extends SimpleValue {
	
	private long wrapped;
	
	public IntegerValue(long wrapped) {
		this.wrapped = wrapped;
	}
	
	long getInteger() {
		return this.wrapped;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof IntegerValue) {
			return this.wrapped == ((IntegerValue) other).wrapped;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return (int) this.wrapped;
	}
	
	@Override
	public String toString() {
		return "IntegerValue [" + wrapped + "]";
	}

	@Override
	public void compactWrite(DataOutput output) throws IOException {
		output.writeLong(this.wrapped);
	}

	@Override
	public SimpleType<IntegerValue> getType() {
		return SimpleType.INTEGER;
	}

}
