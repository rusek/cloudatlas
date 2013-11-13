package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataOutput;
import java.io.IOException;

public class StringValue extends SimpleValue implements Comparable<StringValue> {
	
	private String wrapped;
	
	public StringValue(String wrapped) {
		assert wrapped != null;
		
		this.wrapped = wrapped;
	}
	
	public String getString() {
		return this.wrapped;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((wrapped == null) ? 0 : wrapped.hashCode());
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
		StringValue other = (StringValue) obj;
		if (wrapped == null) {
			if (other.wrapped != null)
				return false;
		} else if (!wrapped.equals(other.wrapped))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return wrapped;
	}

	@Override
	public void compactWrite(DataOutput output) throws IOException {
		output.writeUTF(this.wrapped);
	}

	@Override
	public SimpleType<StringValue> getType() {
		return SimpleType.STRING;
	}

	public int compareTo(StringValue o) {
		return this.wrapped.compareTo(o.wrapped);
	}

}
