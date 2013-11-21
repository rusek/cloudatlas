package pl.edu.mimuw.cloudatlas.cli;

import pl.edu.mimuw.cloudatlas.attributes.Value;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;

public class Client {

	private PrintWriter out = new PrintWriter(System.out);
	private CommandFacade commandFacade;
	private boolean shouldQuit = false;
	private boolean shellMode = false;
	
	public Client(String registryHost) throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry(registryHost);
		commandFacade = (CommandFacade) registry.lookup(CommandFacade.BIND_NAME);
	}
	
	public void flush() {
		out.flush();
	}
	
	public void printCommonCommands() {
		out.println("  - getAttributeValue <zoneName> <attributeName>");
		out.println("    Returns attribute value of a given zone.");
		out.println("  - getMyGlobalName");
		out.println("    Returns agent zone name.");
		out.println("  - extinguish");
		out.println("    Remotely shuts down agent.");
		out.println("  - help");
		out.println("    This one should be obvious now.");
	}
	
	public void printShellHelp() {
		out.println("Available commands:");
		printCommonCommands();
		out.println("  - quit");
		out.println("    Exits shell.");
	}
	
	public void printConsoleHelp() {
		out.println("Available commands:");
		printCommonCommands();
		out.println("  - shell");
		out.println("    Runs interactive shell.");
	}
	
	private Completer createCompleter() {
		return new StringsCompleter("getAttributeValue", "getMyGlobalName", "extinguish");
	}
	
	public void processCommonCommand(String name, List<String> args) throws RemoteException {
		switch (name) {
		case "getAttributeValue":
			if (args.size() != 2) {
				throw new IllegalArgumentException("getAttributeValue command takes exactly 2 arguments");
			}
			Value v = commandFacade.getAttributeValue(args.get(0), args.get(1));
			out.println(v);
			break;
			
		case "getMyGlobalName":
			if (args.size() != 0) {
				throw new IllegalArgumentException("getAttributeValue command takes no arguments");
			}
			String globalName = commandFacade.getMyGlobalName();
			out.println(globalName);
			break;
		
		case "extinguish":
			if (args.size() != 0) {
				throw new IllegalArgumentException("extinguish command takes no arguments");
			}
			commandFacade.shutdown();
			break;
		
		case "ping":
			out.println("pong");
			break;
		
		case "":
		case "help":
			if (shellMode) {
				printShellHelp();
			} else {
				printConsoleHelp();
			}
			break;
			
		default:
			throw new IllegalArgumentException("Unrecognized command: " + name);
		}
	}
	
	public void processShellCommand(String name, List<String> args) throws RemoteException {
		switch (name) {
		case "quit":
			if (args.size() != 0) {
				throw new IllegalArgumentException("quit command takes no arguments");
			}
			shouldQuit = true;
			break;
			
		case "":
			// No command provided
			break;
			
		default:
			processCommonCommand(name, args);
		}
	}
	
	public void processConsoleCommand(String name, List<String> args) throws RemoteException, IOException {
		switch (name) {
		case "shell":
			if (args.size() != 0) {
				throw new IllegalArgumentException("shell command takes no arguments");
			}
			executeShell();
			
			break;
			
		default:
			processCommonCommand(name, args);
		}
	}
	
	public void executeShell() throws IOException {
		shellMode = true;
		out.flush();
		ConsoleReader reader = new ConsoleReader();
		reader.addCompleter(createCompleter());
		reader.setPrompt(">: ");
		out = new PrintWriter(reader.getOutput());

		String line;
		while (!shouldQuit && (line = reader.readLine()) != null) {
			String[] parts = StringUtils.trim(line).split("\\s+");
			try {
				processShellCommand(parts[0], Arrays.asList(parts).subList(1, parts.length));
			} catch (IllegalArgumentException ex) {
				out.println(ex.getMessage());
			} catch (Exception ex) {
				printException(ex);
			}
			out.flush();
		}
	}
	
	public void printException(Exception ex) {
		ex.printStackTrace(out);
	}
	
	public static void main(String[] args) {
		try {
			Client client = new Client(args[0]);
			try {
				client.processConsoleCommand(args[1], Arrays.asList(args).subList(2, args.length));
			} finally {
				client.flush();
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
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

