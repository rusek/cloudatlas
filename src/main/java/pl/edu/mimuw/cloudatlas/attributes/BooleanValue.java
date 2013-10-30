package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataOutput;
import java.io.IOException;

public class BooleanValue extends SimpleValue {

	private boolean wrapped;
	
	public BooleanValue(boolean wrapped) {
		this.wrapped = wrapped;
	}
	
	public boolean getBoolean() {
		return this.wrapped;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof BooleanValue) {
			return this.wrapped == ((BooleanValue) other).wrapped;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return this.wrapped ? 1 : 0;
	}
	
	@Override
	public String toString() {
		return "BooleanValue [" + wrapped + "]";
	}
	
	public void compactWrite(DataOutput output) throws IOException {
		output.writeBoolean(wrapped);
	}

	@Override
	public Type<? extends Value> getType() {
		return SimpleType.BOOLEAN;
	}
}
