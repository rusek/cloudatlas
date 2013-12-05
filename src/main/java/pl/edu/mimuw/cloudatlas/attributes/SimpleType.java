package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class SimpleType<V extends SimpleValue> extends Type<V> {

	private static final long serialVersionUID = 1L;
	
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

		private static final long serialVersionUID = 1L;

		@Override
		public BooleanValue compactReadValue(DataInput input) throws IOException {
			return new BooleanValue(input.readBoolean());
		}
		
		@Override
		public String toString() {
			return "boolean";
		}

		@Override
		public boolean isComparable() {
			return true;
		}
		
	};
	
	public static final SimpleType<IntegerValue> INTEGER = new SimpleType<IntegerValue>() {

		private static final long serialVersionUID = 1L;

		@Override
		public IntegerValue compactReadValue(DataInput input) throws IOException {
			return new IntegerValue(input.readLong());
		}
		
		@Override
		public String toString() {
			return "integer";
		}

		@Override
		public boolean isComparable() {
			return true;
		}
		
	};
	
	public static final SimpleType<DoubleValue> DOUBLE = new SimpleType<DoubleValue>() {

		private static final long serialVersionUID = 1L;

		@Override
		public DoubleValue compactReadValue(DataInput input) throws IOException {
			return new DoubleValue(input.readDouble());
		}
		
		@Override
		public String toString() {
			return "double";
		}

		@Override
		public boolean isComparable() {
			return true;
		}
		
	};
	
	public static final SimpleType<StringValue> STRING = new SimpleType<StringValue>() {

		private static final long serialVersionUID = 1L;

		@Override
		public StringValue compactReadValue(DataInput input) throws IOException {
			return new StringValue(input.readUTF());
		}
		
		@Override
		public String toString() {
			return "string";
		}

		@Override
		public boolean isComparable() {
			return true;
		}
		
	};
	
	public static final SimpleType<ContactValue> CONTACT = new SimpleType<ContactValue>() {

		private static final long serialVersionUID = 1L;

		@Override
		public ContactValue compactReadValue(DataInput input) throws IOException {
			String host = input.readUTF();
			int port = input.readUnsignedShort();
			return new ContactValue(host, port);
		}
		
		@Override
		public String toString() {
			return "contact";
		}
		
	};
	
	public static final SimpleType<TimeValue> TIME = new SimpleType<TimeValue>() {

		private static final long serialVersionUID = 1L;

		@Override
		public TimeValue compactReadValue(DataInput input) throws IOException {
			return new TimeValue(input.readLong());
		}
		
		@Override
		public String toString() {
			return "time";
		}

		@Override
		public boolean isComparable() {
			return true;
		}
		
	};
	
	public static final SimpleType<DurationValue> DURATION = new SimpleType<DurationValue>() {

		private static final long serialVersionUID = 1L;

		@Override
		public DurationValue compactReadValue(DataInput input) throws IOException {
			return new DurationValue(input.readLong());
		}
		
		@Override
		public String toString() {
			return "duration";
		}

		@Override
		public boolean isComparable() {
			return true;
		}
		
	};
}
