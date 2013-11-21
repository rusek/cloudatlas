package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataOutput;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeValue extends SimpleValue implements Comparable<TimeValue> {

	private static final long serialVersionUID = 1L;
	
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
		return createDateFormat().format(new Date(timestamp));
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

	public static DateFormat createDateFormat() {
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS z");
		format.setTimeZone(TimeZone.getTimeZone("CET"));
		return format;
	}
	
	public static DateFormat createNoZoneDateFormat() {
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		format.setTimeZone(TimeZone.getTimeZone("CET"));
		return format;
	}
	
	public static TimeValue now() {
		return new TimeValue(new Date().getTime());
	}
}
