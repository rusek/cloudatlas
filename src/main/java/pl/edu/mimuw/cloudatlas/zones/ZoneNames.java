package pl.edu.mimuw.cloudatlas.zones;

import java.util.regex.Pattern;

public class ZoneNames {
	
	private static final Pattern GLOBAL_NAME_PATTERN = Pattern.compile("^(/|(/[a-zA-Z_]\\w*)+)$");
	private static final Pattern LOCAL_NAME_PATTERN = Pattern.compile("^[a-zA-Z_]\\w*$");

	private ZoneNames() {
	}
	
	public static boolean isGlobalName(String globalName) {
		return GLOBAL_NAME_PATTERN.matcher(globalName) != null;
	}
	
	public static boolean isLocalName(String localName) {
		return LOCAL_NAME_PATTERN.matcher(localName) != null;
	}
	
	public static String getLocalName(String globalName) {
		int pos = globalName.lastIndexOf('/');
		if (pos == globalName.length() - 1) {
			return null;
		} else {
			return globalName.substring(pos + 1);
		}
	}

	public static int getLevel(String globalName) {
		assert isGlobalName(globalName);
		
		if (globalName.equals("/")) {
			return 0;
		} else {
			return globalName.length() - globalName.replaceAll("/", "").length();
		}
	}
}
