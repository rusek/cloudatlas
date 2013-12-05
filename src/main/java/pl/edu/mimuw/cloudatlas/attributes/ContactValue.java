package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataOutput;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactValue extends SimpleValue {

	private static final long serialVersionUID = 1L;

	private final String host;
	private final int port;
	
	public ContactValue(String host, int port) {
		assert host != null;
		
		this.host = host;
		this.port = port;
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	@Override
	public String toString() {
		return host + ":" + port;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + port;
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
		ContactValue other = (ContactValue) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (port != other.port)
			return false;
		return true;
	}

	@Override
	public void compactWrite(DataOutput output) throws IOException {
		output.writeUTF(host);
		output.writeShort(port);
	}

	@Override
	public SimpleType<ContactValue> getType() {
		return SimpleType.CONTACT;
	}
	
	private static final Pattern PATTERN = Pattern.compile("([^:]+):(\\d+)");

	public static ContactValue parseContact(String text) throws ValueFormatException {
		Matcher matcher = PATTERN.matcher(text);
		if (!matcher.matches()) {
			throw new ValueFormatException("Invalid contact string: " + text);
		}
		int port = Integer.parseInt(matcher.group(2));
		if (port < 1 || port > 65535) {
			throw new ValueFormatException("Invalid contact string: " + text);
		}
		
		return new ContactValue(matcher.group(1), port);
	}
}
