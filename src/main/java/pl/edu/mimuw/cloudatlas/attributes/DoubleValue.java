package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataOutput;
import java.io.IOException;

public class DoubleValue extends SimpleValue {
	
	private double wrapped;
	
	public DoubleValue(double wrapped) {
		this.wrapped = wrapped;
	}
	
	double getDouble() {
		return this.wrapped;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof DoubleValue) {
			return this.wrapped == ((DoubleValue) other).wrapped;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new Double(wrapped).hashCode();
	}

	@Override
	public void compactWrite(DataOutput output) throws IOException {
		output.writeDouble(wrapped);
	}

	@Override
	public SimpleType<DoubleValue> getType() {
		return SimpleType.DOUBLE;
	}

}