package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataOutput;
import java.io.IOException;

public class DoubleValue extends SimpleValue implements Comparable<DoubleValue> {
	
	private double wrapped;
	
	public DoubleValue(double wrapped) {
		this.wrapped = wrapped;
	}
	
	double getDouble() {
		return this.wrapped;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(wrapped);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		DoubleValue other = (DoubleValue) obj;
		if (Double.doubleToLongBits(wrapped) != Double
				.doubleToLongBits(other.wrapped))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return Double.toString(wrapped);
	}

	@Override
	public void compactWrite(DataOutput output) throws IOException {
		output.writeDouble(wrapped);
	}

	@Override
	public SimpleType<DoubleValue> getType() {
		return SimpleType.DOUBLE;
	}

	public int compareTo(DoubleValue o) {
		return Double.compare(wrapped, o.wrapped);
	}

}
