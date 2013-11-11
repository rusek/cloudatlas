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
	public boolean equals(Object other) {
		if (other instanceof StringValue) {
			return this.wrapped.equals(((StringValue) other).wrapped);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return this.wrapped.hashCode();
	}
	
	@Override
	public String toString() {
		return "StringValue [" + wrapped + "]";
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
