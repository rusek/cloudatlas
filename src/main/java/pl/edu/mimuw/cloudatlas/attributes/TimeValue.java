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
	public boolean equals(Object other) {
		if (other instanceof TimeValue) {
			return this.timestamp == ((TimeValue) other).timestamp;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return (int) this.timestamp;
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
