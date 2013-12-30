package pl.edu.mimuw.cloudatlas.agent;

import java.io.FileInputStream;
import java.io.IOException;
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

	public static Properties loadProperties(String path) throws IOException {
		Properties properties = new Properties();
		if (path == null) {
			properties.load(Main.class.getClassLoader().getResourceAsStream("agent.properties"));
		} else {
			FileInputStream inputStream = new FileInputStream(path);
			try {
				properties.load(inputStream);
			} finally {
				try {
					inputStream.close();
				} catch (IOException ex) {}
			}
		}
		return properties;
	}
}
