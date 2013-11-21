package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataOutput;
import java.io.IOException;

public class BooleanValue extends SimpleValue implements Comparable<BooleanValue> {

	private static final long serialVersionUID = 1L;
	
	private boolean wrapped;
	
	public BooleanValue(boolean wrapped) {
		this.wrapped = wrapped;
	}
	
	public boolean getBoolean() {
		return this.wrapped;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (wrapped ? 1231 : 1237);
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
		BooleanValue other = (BooleanValue) obj;
		if (wrapped != other.wrapped)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return wrapped ? "true" : "false";
	}
	
	public void compactWrite(DataOutput output) throws IOException {
		output.writeBoolean(wrapped);
	}

	@Override
	public Type<? extends Value> getType() {
		return SimpleType.BOOLEAN;
	}

	public int compareTo(BooleanValue o) {
		return Boolean.compare(wrapped, o.wrapped);
	}
}
