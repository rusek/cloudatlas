package pl.edu.mimuw.cloudatlas.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

	private static final Pattern ARGS_PATTERN =
			Pattern.compile("(\\s+)|([^\\\\\"'\\s]+)|\\\\(.)|\"([^\"]*)\"|'([^']*)'");;
	
	/**
	 * Performs shell-like argument splitting.
	 */
	public static List<String> splitArgs(String line) {
		List<String> readyParts = new ArrayList<String>();
		String currentPart = null;
		Matcher matcher = ARGS_PATTERN.matcher(line);
		int offset = 0;
		while (matcher.find(offset)) {
			if (matcher.group(1) != null) { // whitespace
				if (currentPart != null) {
					readyParts.add(currentPart);
					currentPart = null;
				}
			} else {
				if (currentPart == null) {
					currentPart = "";
				}
				if (matcher.group(2) != null) { // regular text
					currentPart += matcher.group(2);
				} else if (matcher.group(3) != null) { // escape character
					currentPart += matcher.group(3);
				} else if (matcher.group(4) != null) { // double quotes
					currentPart += matcher.group(4);
				} else { // single quotes
					currentPart += matcher.group(5);
				}
			}
			
			offset = matcher.end();
		}
		if (currentPart != null) {
			readyParts.add(currentPart);
		}
		
		if (offset != line.length()) {
			String cause = "unknown error";
			switch (line.charAt(offset)) {
			case '"':
				cause = "missing closing double quote";
				break;
				
			case '\'':
				cause = "missing closing single quote";
				break;
				
			case '\\':
				cause = "missing escape character";
				break;
			}
			throw new IllegalArgumentException("Near character " + (offset + 1) + ": " + cause);
		}
		
		return readyParts;
	}
}
