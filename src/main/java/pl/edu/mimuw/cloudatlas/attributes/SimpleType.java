package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.InetAddress;

public abstract class SimpleType<V extends SimpleValue> extends Type<V> {
	
	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
	
	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}
	
	@Override
	public final void compactWrite(DataOutput output) throws IOException {
		// Simple types have no state which requires serializing
	}

	public static final SimpleType<BooleanValue> BOOLEAN = new SimpleType<BooleanValue>() {

		@Override
		public BooleanValue compactReadValue(DataInput input) throws IOException {
			return new BooleanValue(input.readBoolean());
		}
		
		@Override
		public String toString() {
			return "BooleanType";
		}
		
	};
	
	public static final SimpleType<IntegerValue> INTEGER = new SimpleType<IntegerValue>() {

		@Override
		public IntegerValue compactReadValue(DataInput input) throws IOException {
			return new IntegerValue(input.readLong());
		}
		
		@Override
		public String toString() {
			return "IntegerType";
		}
		
	};
	
	public static final SimpleType<DoubleValue> DOUBLE = new SimpleType<DoubleValue>() {

		@Override
		public DoubleValue compactReadValue(DataInput input) throws IOException {
			return new DoubleValue(input.readDouble());
		}
		
		@Override
		public String toString() {
			return "DoubleType";
		}
		
	};
	
	public static final SimpleType<StringValue> STRING = new SimpleType<StringValue>() {

		@Override
		public StringValue compactReadValue(DataInput input) throws IOException {
			return new StringValue(input.readUTF());
		}
		
		@Override
		public String toString() {
			return "StringType";
		}
		
	};
	
	public static final SimpleType<ContactValue> CONTACT = new SimpleType<ContactValue>() {

		@Override
		public ContactValue compactReadValue(DataInput input) throws IOException {
			int length = input.readInt();
			if (length < 0) {
				throw new IOException("Negative length");
			}
			byte[] bytes = new byte[length];
			input.readFully(bytes);
			return new ContactValue(InetAddress.getByAddress(bytes));
		}
		
		@Override
		public String toString() {
			return "ContactType";
		}
		
	};
	
	public static final SimpleType<TimeValue> TIME = new SimpleType<TimeValue>() {

		@Override
		public TimeValue compactReadValue(DataInput input) throws IOException {
			return new TimeValue(input.readLong());
		}
		
		@Override
		public String toString() {
			return "TimeType";
		}
		
	};
	
	public static final SimpleType<DurationValue> DURATION = new SimpleType<DurationValue>() {

		@Override
		public DurationValue compactReadValue(DataInput input) throws IOException {
			return new DurationValue(input.readLong());
		}
		
		@Override
		public String toString() {
			return "DurationType";
		}
		
	};
}
