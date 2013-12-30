package pl.edu.mimuw.cloudatlas.agent;

import java.io.IOException;
import java.util.Properties;

public class GetHostname {

	public static void main(String[] args) throws IOException {
		Properties properties = args.length > 0 ? PropertyReader.loadProperties(args[0]) :
			PropertyReader.loadProperties(null);
		System.out.println(PropertyReader.getHost(properties));
	}
}
