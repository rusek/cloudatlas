package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataOutput;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationValue extends SimpleValue implements Comparable<DurationValue> {
	
	public final static Pattern PATTERN = Pattern.compile("^([+-])(0|[1-9]\\d*) (\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{3})$"); 
	
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
		long miliseconds = Math.abs(this.miliseconds);
		
		long seconds = miliseconds / 1000;
		miliseconds -= seconds * 1000;
		
		long minutes = seconds / 60;
		seconds -= minutes * 60;
		
		long hours = minutes / 60;
		minutes -= hours * 60;
		
		long days = hours / 24;
		hours -= days * 24;
		
		return String.format(
				"%c%d %02d:%02d:%02d.%03d",
				this.miliseconds >= 0 ? '+' : '-',
				days,
				hours,
				minutes,
				seconds,
				miliseconds
		);
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

	
	public static DurationValue parseDuration(String text) throws ValueFormatException {
		Matcher match = DurationValue.PATTERN.matcher(text);
		if (!match.matches()) {
			throw new ValueFormatException("Invalid duration string: " + text);
		}
		
		long sign = match.group(1).equals("+") ? 1 : -1;
		long days = Integer.parseInt(match.group(2));
		long hours = Integer.parseInt(match.group(3));
		long mins = Integer.parseInt(match.group(4));
		long secs = Integer.parseInt(match.group(5));
		long mils = Integer.parseInt(match.group(6));
		
		if (hours > 23 || mins > 59 || secs > 59) {
			throw new ValueFormatException("Invalid duration string: " + text);
		}
		
		return new DurationValue(sign * (((((days * 24) + hours) * 60 + mins) * 60 + secs) * 1000 + mils));	
	}
}
