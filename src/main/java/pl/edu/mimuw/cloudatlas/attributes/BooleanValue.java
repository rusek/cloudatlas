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
	
	public void compactWrite(DataOutput output) throws IOException {
		output.writeBoolean(wrapped);
	}

	@Override
	public Type<? extends Value> getType() {
		return SimpleType.BOOLEAN;
	}
}
