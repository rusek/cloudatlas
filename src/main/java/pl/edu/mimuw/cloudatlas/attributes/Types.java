package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Types {
	
	private Types() {
	}
	
	public static void compactWriteType(Type<? extends Value> type, DataOutput output) throws IOException {
		output.writeByte(DISCRIMINATORS.get(type.getClass()));
		type.compactWrite(output);
	}
	
	public static Type<? extends Value> compactReadType(DataInput input) throws IOException {
		byte discriminator = input.readByte();
		TypeReader reader = READERS.get(discriminator);
		if (reader == null) {
			throw new IOException("Unregistered type");
		}
		
		return reader.compactReadType(input);
	}
	
	@SuppressWarnings("unchecked")
	private static SimpleType<? extends SimpleValue> compactReadSimpleType(DataInput input) throws IOException {
		Type<? extends Value> type = compactReadType(input);
		if (!(type instanceof SimpleType)) {
			throw new IOException("Expecting simple type");
		}
		
		return (SimpleType<? extends SimpleValue>) type;
	}
	
	private static interface TypeReader {
		Type<? extends Value> compactReadType(DataInput input) throws IOException;
	}
	
	private static final Map<Class<?>, Byte> DISCRIMINATORS =
			new HashMap<Class<?>, Byte>();
	private static final Map<Byte, TypeReader> READERS = 
			new HashMap<Byte, TypeReader>();
	
	private static void registerType(byte discriminator, Class<?> classType, TypeReader reader) {
		DISCRIMINATORS.put(classType, discriminator);
		READERS.put(discriminator, reader);
	}

	private static void registerSimpleType(byte discriminator, final SimpleType<? extends SimpleValue> simpleType) {
		registerType(discriminator, simpleType.getClass(), new TypeReader() {
			public Type<? extends Value> compactReadType(DataInput input) throws IOException {
				return simpleType;
			}
		});
	}
	
	{
		registerSimpleType((byte) 1, SimpleType.BOOLEAN);
		registerSimpleType((byte) 2, SimpleType.INTEGER);
		registerSimpleType((byte) 3, SimpleType.DOUBLE);
		registerSimpleType((byte) 4, SimpleType.STRING);
		registerSimpleType((byte) 5, SimpleType.TIME);
		registerSimpleType((byte) 6, SimpleType.DURATION);
		registerType((byte) 101, ListType.class, new TypeReader() {
			public Type<? extends Value> compactReadType(DataInput input) throws IOException {
				SimpleType<? extends SimpleValue> itemType = Types.compactReadSimpleType(input);
				return ListType.of(itemType);
			}
		});
		registerType((byte) 102, SetType.class, new TypeReader() {
			public Type<? extends Value> compactReadType(DataInput input) throws IOException {
				SimpleType<? extends SimpleValue> itemType = Types.compactReadSimpleType(input);
				return SetType.of(itemType);
			}
		});
	}
}
