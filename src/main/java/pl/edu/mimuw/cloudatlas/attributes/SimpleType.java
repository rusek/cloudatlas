package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataInput;
import java.io.IOException;

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
}
