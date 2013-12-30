package pl.edu.mimuw.cloudatlas.cli;

import java.util.List;

import pl.edu.mimuw.cloudatlas.attributes.ListValue;
import pl.edu.mimuw.cloudatlas.attributes.SetValue;
import pl.edu.mimuw.cloudatlas.attributes.SimpleValue;
import pl.edu.mimuw.cloudatlas.attributes.Value;
import pl.edu.mimuw.cloudatlas.attributes.ValueFormatException;

public class ValueParser {

	public static Value parseValue(List<String> args) {
		if (args.size() == 0) {
			throw new IllegalArgumentException("Missing value type");
		}
		String[] type = args.get(0).split(":", 2);
		try {
			if (type.length == 1) {
				if (args.size() != 2) {
					throw new IllegalArgumentException("Expecting single value string");
				}
				return SimpleValueParser.createParser(type[0]).parseValue(args.get(1));
			} else {
				SimpleValueParser<? extends SimpleValue> parser = SimpleValueParser.createParser(type[1]);
				List<String> parserArgs = args.subList(1, args.size());
				switch (type[0]) {
				case "list":
					return parseList(parser, parserArgs);
					
				case "set":
					return parseSet(parser, parserArgs);
					
				default:
					throw new IllegalArgumentException("Invalid collection type: " + type[0]);
				}
			}
		} catch (ValueFormatException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	private static <V extends SimpleValue> Value parseList(SimpleValueParser<V> parser, List<String> args)
			throws ValueFormatException {
		ListValue<V> result = ListValue.of(parser.getType());
		for (String arg : args) {
			result.addItem(parser.parseValue(arg));
		}
		return result;
	}
	
	private static <V extends SimpleValue> Value parseSet(SimpleValueParser<V> parser, List<String> args)
			throws ValueFormatException {
		SetValue<V> result = SetValue.of(parser.getType());
		for (String arg : args) {
			result.addItem(parser.parseValue(arg));
		}
		return result;
	}
}
