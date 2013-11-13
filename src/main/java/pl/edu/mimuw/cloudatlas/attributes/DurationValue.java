package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataOutput;
import java.io.IOException;

public class DurationValue extends SimpleValue implements Comparable<DurationValue> {
	
	private long miliseconds;
	
	public DurationValue(long miliseconds) {
		this.miliseconds = miliseconds;
	}
	
	public long getTotalMiliseconds() {
		return this.miliseconds;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (miliseconds ^ (miliseconds >>> 32));
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
		DurationValue other = (DurationValue) obj;
		if (miliseconds != other.miliseconds)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DurationValue [" + miliseconds + "]";
	}

	@Override
	public void compactWrite(DataOutput output) throws IOException {
		output.writeLong(this.miliseconds);
		
	}

	@Override
	public SimpleType<DurationValue> getType() {
		return SimpleType.DURATION;
	}

	public int compareTo(DurationValue o) {
		return Long.compare(miliseconds, o.miliseconds);
	}

}
