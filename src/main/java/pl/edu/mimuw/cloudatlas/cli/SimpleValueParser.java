package pl.edu.mimuw.cloudatlas.cli;

import java.text.ParseException;

import pl.edu.mimuw.cloudatlas.attributes.BooleanValue;
import pl.edu.mimuw.cloudatlas.attributes.ContactValue;
import pl.edu.mimuw.cloudatlas.attributes.DoubleValue;
import pl.edu.mimuw.cloudatlas.attributes.DurationValue;
import pl.edu.mimuw.cloudatlas.attributes.IntegerValue;
import pl.edu.mimuw.cloudatlas.attributes.SimpleType;
import pl.edu.mimuw.cloudatlas.attributes.SimpleValue;
import pl.edu.mimuw.cloudatlas.attributes.StringValue;
import pl.edu.mimuw.cloudatlas.attributes.TimeValue;
import pl.edu.mimuw.cloudatlas.attributes.ValueFormatException;

public abstract class SimpleValueParser<V extends SimpleValue> {

	public abstract V parseValue(String text) throws ValueFormatException;
	
	public abstract SimpleType<V> getType();
	
	public static SimpleValueParser<? extends SimpleValue> createParser(String simpleTypeName) {
		switch (simpleTypeName) {
		case "boolean":
			return new BooleanParser();
			
		case "integer":
			return new IntegerParser();
			
		case "double":
			return new DoubleParser();
			
		case "string":
			return new StringParser();
			
		case "time":
			return new TimeParser();
			
		case "duration":
			return new DurationParser();
			
		case "contact":
			return new ContactParser();
			
		default:
			throw new IllegalArgumentException("Invalid simple type: " + simpleTypeName);
		}
	}
	
	private static class BooleanParser extends SimpleValueParser<BooleanValue> {

		@Override
		public BooleanValue parseValue(String text) throws ValueFormatException {
			switch (text) {
			case "true":
				return new BooleanValue(true);
				
			case "false":
				return new BooleanValue(false);
				
			default:
				throw new ValueFormatException("Invalid boolean string: " + text);
			}
		}

		@Override
		public SimpleType<BooleanValue> getType() {
			return SimpleType.BOOLEAN;
		}
		
	}
	
	private static class IntegerParser extends SimpleValueParser<IntegerValue> {

		@Override
		public IntegerValue parseValue(String text) throws ValueFormatException {
			try {
				return new IntegerValue(Long.parseLong(text));
			} catch (NumberFormatException e) {
				throw new ValueFormatException("Invalid integer string: " + text);
			}
		}

		@Override
		public SimpleType<IntegerValue> getType() {
			return SimpleType.INTEGER;
		}
		
	}
	
	private static class DoubleParser extends SimpleValueParser<DoubleValue> {

		@Override
		public DoubleValue parseValue(String text) throws ValueFormatException {
			try {
				return new DoubleValue(Double.parseDouble(text));
			} catch (NumberFormatException e) {
				throw new ValueFormatException("Invalid double string: " + text);
			}
		}

		@Override
		public SimpleType<DoubleValue> getType() {
			return SimpleType.DOUBLE;
		}
		
	}
	
	private static class StringParser extends SimpleValueParser<StringValue> {

		@Override
		public StringValue parseValue(String text) throws ValueFormatException {
			return new StringValue(text);
		}

		@Override
		public SimpleType<StringValue> getType() {
			return SimpleType.STRING;
		}
		
	}
	
	private static class ContactParser extends SimpleValueParser<ContactValue> {

		@Override
		public ContactValue parseValue(String text) throws ValueFormatException {
			return ContactValue.parseContact(text);
		}

		@Override
		public SimpleType<ContactValue> getType() {
			return SimpleType.CONTACT;
		}
		
	}
	
	private static class TimeParser extends SimpleValueParser<TimeValue> {

		@Override
		public TimeValue parseValue(String text) throws ValueFormatException {
			try {
				return new TimeValue(TimeValue.createDateFormat().parse(text).getTime());
			} catch (ParseException e) {
				throw new ValueFormatException("Invalid date string: " + text);
			}
		}

		@Override
		public SimpleType<TimeValue> getType() {
			return SimpleType.TIME;
		}
		
	}
	
	private static class DurationParser extends SimpleValueParser<DurationValue> {

		@Override
		public DurationValue parseValue(String text)
				throws ValueFormatException {
			return DurationValue.parseDuration(text);
		}

		@Override
		public SimpleType<DurationValue> getType() {
			return SimpleType.DURATION;
		}
		
	}
}
