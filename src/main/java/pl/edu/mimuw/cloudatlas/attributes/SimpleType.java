package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataInput;
import java.io.IOException;
import java.net.InetAddress;

public abstract class SimpleType<V extends SimpleValue> extends Type<V> {

	public static final SimpleType<BooleanValue> BOOLEAN = new SimpleType<BooleanValue>() {

		@Override
		public BooleanValue compactReadValue(DataInput input) throws IOException {
			return new BooleanValue(input.readBoolean());
		}
		
	};
	
	public static final SimpleType<IntegerValue> INTEGER = new SimpleType<IntegerValue>() {

		@Override
		public IntegerValue compactReadValue(DataInput input) throws IOException {
			return new IntegerValue(input.readLong());
		}
		
	};
	
	public static final SimpleType<DoubleValue> DOUBLE = new SimpleType<DoubleValue>() {

		@Override
		public DoubleValue compactReadValue(DataInput input) throws IOException {
			return new DoubleValue(input.readDouble());
		}
		
	};
	
	public static final SimpleType<StringValue> STRING = new SimpleType<StringValue>() {

		@Override
		public StringValue compactReadValue(DataInput input) throws IOException {
			return new StringValue(input.readUTF());
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
		
	};
	
	public static final SimpleType<TimeValue> TIME = new SimpleType<TimeValue>() {

		@Override
		public TimeValue compactReadValue(DataInput input) throws IOException {
			return new TimeValue(input.readLong());
		}
		
	};
	
	public static final SimpleType<DurationValue> DURATION = new SimpleType<DurationValue>() {

		@Override
		public DurationValue compactReadValue(DataInput input) throws IOException {
			return new DurationValue(input.readLong());
		}
		
	};
}
