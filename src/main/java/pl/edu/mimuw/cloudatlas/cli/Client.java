package pl.edu.mimuw.cloudatlas.cli;

import pl.edu.mimuw.cloudatlas.attributes.Value;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

	public static void main(String[] args) {
		try {
			Registry registry = LocateRegistry.getRegistry(args[0]);
			CommandFacade stub = (CommandFacade) registry.lookup(CommandFacade.BIND_NAME);
			switch(args[1]) {
				case "getAttributeValue":
					Value v = stub.getAttributeValue(args[2], args[3]);
					System.out.println("Value of " + args[3] + " at " + args[2] + " is " + v.toString());
					break;
				case "shutdown":
					stub.shutdown();
					break;
				
			}
		} catch (Exception e) {
			System.err.println("Client exception:");
			e.printStackTrace();
		}
	}

}

// http://stackoverflow.com/questions/1051295/how-to-find-how-much-disk-space-is-left-using-java
// http://stackoverflow.com/questions/2062440/java-cpu-usage-monitoring
// http://stackoverflow.com/questions/12807797/java-get-available-memory
// http://stackoverflow.com/questions/5512378/how-to-get-ram-size-and-size-of-hard-disk-using-java
// TODO Swap
// http://stackoverflow.com/questions/54686/how-to-get-a-list-of-current-open-windows-process-with-java
// http://stackoverflow.com/questions/4759570/finding-number-of-cores-in-java
// kernel: uname -v
// users: who -q
// DNS: /etc/resolv.conf

