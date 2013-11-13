package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataOutput;
import java.io.IOException;

public class TimeValue extends SimpleValue implements Comparable<TimeValue> {
	
	private long timestamp;
	
	public TimeValue(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public long getTimestamp() {
		return this.timestamp;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
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
		TimeValue other = (TimeValue) obj;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TimeValue [" + timestamp + "]";
	}

	@Override
	public void compactWrite(DataOutput output) throws IOException {
		output.writeLong(timestamp);
	}

	@Override
	public SimpleType<TimeValue> getType() {
		return SimpleType.TIME;
	}

	public int compareTo(TimeValue o) {
		return Long.compare(timestamp, o.timestamp);
	}

}
