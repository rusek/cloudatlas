package pl.edu.mimuw.cloudatlas.zones;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class AttributeNames {
	
	private static final Pattern PATTERN = Pattern.compile("^&?[a-zA-Z_]\\w*$");
	
	private static final Set<String> BUILTIN_NAMES = new HashSet<String>();
	
	static {
		Collections.addAll(BUILTIN_NAMES, "level", "name", "owner", "timestamp", "contacts", "cardinality");
	}

	public static boolean isAttributeName(String name) {
		return PATTERN.matcher(name).matches();
	}
	
	public static boolean isRegularName(String name) {
		return isAttributeName(name) && name.charAt(0) != '&';
	}
	
	public static boolean isSpecialName(String name) {
		return isAttributeName(name) && name.charAt(0) == '&';
	}
	
	public static boolean isBuiltinName(String name) {
		return BUILTIN_NAMES.contains(name);
	}
}
