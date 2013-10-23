package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataOutput;
import java.io.IOException;
import java.net.InetAddress;

public class ContactValue extends SimpleValue {

	private InetAddress wrapped;
	
	public ContactValue(InetAddress wrapped) {
		assert wrapped != null;
		
		this.wrapped = wrapped;
	}
	
	public InetAddress getContact() {
		return this.wrapped;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof ContactValue) {
			return this.wrapped.equals(((ContactValue) other).wrapped);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return this.wrapped.hashCode();
	}
	
	@Override
	public void compactWrite(DataOutput output) throws IOException {
		byte[] bytes = this.wrapped.getAddress();
		output.writeInt(bytes.length);
		output.write(bytes);
	}

	@Override
	public SimpleType<ContactValue> getType() {
		return SimpleType.CONTACT;
	}

}
