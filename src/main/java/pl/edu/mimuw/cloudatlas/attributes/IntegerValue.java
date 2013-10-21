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
	public void compactWrite(DataOutput output) throws IOException {
		output.writeLong(this.wrapped);
	}

	@Override
	public SimpleType<IntegerValue> getType() {
		return SimpleType.INTEGER;
	}

}
