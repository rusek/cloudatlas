package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataOutput;
import java.io.IOException;

public class DurationValue extends SimpleValue {
	
	private long miliseconds;
	
	public DurationValue(long miliseconds) {
		this.miliseconds = miliseconds;
	}
	
	public long getTotalMiliseconds() {
		return this.miliseconds;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof DurationValue) {
			return this.miliseconds == ((DurationValue) other).miliseconds;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return (int) miliseconds;
	}

	@Override
	public void compactWrite(DataOutput output) throws IOException {
		output.writeLong(this.miliseconds);
		
	}

	@Override
	public SimpleType<DurationValue> getType() {
		return SimpleType.DURATION;
	}

}
