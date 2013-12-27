package pl.edu.mimuw.cloudatlas.zones;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class ZoneNames {
	
	private static final Pattern GLOBAL_NAME_PATTERN = Pattern.compile("^(/|(/[a-zA-Z_]\\w*)+)$");
	private static final Pattern LOCAL_NAME_PATTERN = Pattern.compile("^[a-zA-Z_]\\w*$");

	private ZoneNames() {
	}
	
	public static boolean isGlobalName(String globalName) {
		return GLOBAL_NAME_PATTERN.matcher(globalName).matches();
	}
	
	public static String getParentName(String globalName) {
		if (globalName.equals("/")) {
			return null;
		} else {
			return globalName.substring(0, globalName.lastIndexOf('/'));
		}
	}
	
	public static boolean isAncestor(String globalName, String ancestorName) {
		if (globalName.equals("/")) {
			return false;
		} else if (ancestorName.equals("/")) {
			return true;
		} else {
			return StringUtils.startsWith(globalName, ancestorName + "/");
		}
	}
	
	public static boolean isAncestorOrSelf(String globalName, String ancestorName) {
		return globalName.equals(ancestorName) || isAncestor(globalName, ancestorName);
	}
	
	public static boolean isLocalName(String localName) {
		return LOCAL_NAME_PATTERN.matcher(localName).matches();
	}
	
	public static String[] splitGlobalName(String globalName) {
		return globalName.substring(1).split("/");
	}
	
	public static String getLocalName(String globalName) {
		int pos = globalName.lastIndexOf('/');
		if (pos == globalName.length() - 1) {
			return null;
		} else {
			return globalName.substring(pos + 1);
		}
	}
	
	public static String getCommonName(String firstName, String secondName) {
		String[] firstParts = splitGlobalName(firstName);
		String[] secondParts = splitGlobalName(secondName);
		int i = 0;
		while (i < firstParts.length && i < secondParts.length && firstParts[i].equals(secondParts[i])) {
			i++;
		}
		return "/" + StringUtils.join(Arrays.asList(firstParts).subList(0, i), '/');
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
