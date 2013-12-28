package pl.edu.mimuw.cloudatlas.agent;

import java.util.Properties;

public class PropertyReader {
	
	private PropertyReader() {}
	
	public static String getHost(Properties properties) {
		return properties.getProperty("host", "localhost");
	}
	
	public static int getPort(Properties properties) {
		String portString = properties.getProperty("port");
		if (portString == null) {
			throw new IllegalArgumentException("Missing port property");
		}
		return Integer.parseInt(portString);
	}

}
