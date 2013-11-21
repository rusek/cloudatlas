package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataOutput;
import java.io.IOException;

public class IntegerValue extends SimpleValue implements Comparable<IntegerValue> {

	private static final long serialVersionUID = 1L;
	
	private long wrapped;
	
	public IntegerValue(long wrapped) {
		this.wrapped = wrapped;
	}
	
	public long getInteger() {
		return this.wrapped;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (wrapped ^ (wrapped >>> 32));
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
		IntegerValue other = (IntegerValue) obj;
		if (wrapped != other.wrapped)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return Long.toString(wrapped);
	}

	@Override
	public void compactWrite(DataOutput output) throws IOException {
		output.writeLong(this.wrapped);
	}

	@Override
	public SimpleType<IntegerValue> getType() {
		return SimpleType.INTEGER;
	}

	public int compareTo(IntegerValue o) {
		return Long.compare(wrapped, o.wrapped);
	}

}
