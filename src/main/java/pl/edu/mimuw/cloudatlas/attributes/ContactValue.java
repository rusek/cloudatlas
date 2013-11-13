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
		ContactValue other = (ContactValue) obj;
		if (wrapped == null) {
			if (other.wrapped != null)
				return false;
		} else if (!wrapped.equals(other.wrapped))
			return false;
		return true;
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
