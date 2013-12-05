package pl.edu.mimuw.cloudatlas.cli;

import pl.edu.mimuw.cloudatlas.attributes.ContactValue;
import pl.edu.mimuw.cloudatlas.attributes.Value;
import pl.edu.mimuw.cloudatlas.attributes.ValueFormatException;
import pl.edu.mimuw.cloudatlas.zones.Attribute;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;

public class Client {
	
	private static final Pattern LOCATOR_PATTERN = Pattern.compile("(?:([^:/]+)(?::(\\d+))?)?(/.+)?");

	private PrintWriter out = new PrintWriter(System.out);
	private CommandFacade commandFacade;
	private boolean shouldQuit = false;
	private boolean shellMode = false;
	
	private String registryHost = "localhost";
	private Integer registryPort = null;
	private String agentZoneName = null;
	
	private StatsCollector statsCollector = new StatsCollector();
	
	public Client(String locator) throws RemoteException, NotBoundException {
		parseLocator(locator);
		connect();
	}
	
	private void parseLocator(String locator) {
		String registryHost = this.registryHost;
		Integer registryPort = this.registryPort;
		String agentName = this.agentZoneName;
		
		Matcher matcher = LOCATOR_PATTERN.matcher(locator);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid locator: " + locator);
		}
		
		if (matcher.group(1) != null) {
			registryHost = matcher.group(1);
			agentName = null;
			registryPort = null;
		}

		if (matcher.group(2) != null) {
			registryPort = Integer.parseInt(matcher.group(2));
		}
		
		if (matcher.group(3) != null) {
			agentName = matcher.group(3);
		}
		
		this.registryHost = registryHost;
		this.registryPort = registryPort;
		this.agentZoneName = agentName;
	}
	
	private void connect() throws RemoteException, NotBoundException {
		Registry registry;
		
		if (registryPort == null) {
			registry = LocateRegistry.getRegistry(registryHost);
		} else {
			registry = LocateRegistry.getRegistry(registryHost, registryPort);
		}
		
		if (agentZoneName == null) {
			commandFacade = (CommandFacade) registry.lookup(CommandFacade.BIND_NAME);
		} else {
			commandFacade = (CommandFacade) registry.lookup(CommandFacade.BIND_NAME + ":" + agentZoneName);
		}
	}
	
	private void sendStats() throws RemoteException, IOException {
		List<Attribute> stats = statsCollector.collectStats();
		commandFacade.setMyAttributes(stats);
		
		if (shellMode) {
			for (Attribute stat : stats) {
				out.println(stat);
			}
		}
	}
	
	public void flush() {
		out.flush();
	}
	
	public void printCommonCommands() {
		out.println("  - getAttributeValue <zoneName> <attributeName>");
		out.println("    Returns attribute value of a given zone.");
		out.println("  - getMyGlobalName");
		out.println("    Returns agent zone name.");
		out.println("  - setFallbackContacts <host:port> [<host:port>] ...");
		out.println("    Sets fallback contacts.");
		out.println("  - showStats");
		out.println("    Collects statistics and prints values.");
		out.println("  - sendStats");
		out.println("    Collects statistics and sends them to the agent.");
		out.println("  - extinguish");
		out.println("    Remotely shuts down agent.");
		out.println("  - help");
		out.println("    This one should be obvious now.");
	}
	
	public void printShellHelp() {
		out.println("Available commands:");
		printCommonCommands();
		out.println("  - reconnect [host[:port]][/zoneName]");
		out.println("    Reestablishes connection to the agent");
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
		return new StringsCompleter("getAttributeValue", "getMyGlobalName", "extinguish", "reconnect", "showStats",
				"sendStats", "setFallbackContacts");
	}
	
	public void processCommonCommand(String name, List<String> args) throws Exception {
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
			commandFacade.extinguish();
			break;
		
		case "ping":
			out.println("pong");
			break;
			
		case "showStats":
			if (args.size() != 0) {
				throw new IllegalArgumentException("showStats command takes no arguments");
			}
			List<Attribute> stats = statsCollector.collectStats();
			for (Attribute stat : stats) {
				out.println(stat);
			}
			break;
			
		case "setFallbackContacts":
			if (args.size() == 0) {
				throw new IllegalArgumentException("setFallbackContacts takes at least one argument");
			}
			List<ContactValue> contacts = new ArrayList<ContactValue>();
			for (String arg : args) {
				try {
					contacts.add(ContactValue.parseContact(arg));
				} catch (ValueFormatException ex) {
					throw new IllegalArgumentException("Invalid contact: " + arg);
				}
			}
			commandFacade.setFallbackContacts(contacts);
			break;
		
		case "sendStats":
			if (args.size() != 0) {
				throw new IllegalArgumentException("showStats command takes no arguments");
			}
			sendStats();
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
	
	public void processShellCommand(String name, List<String> args) throws Exception {
		switch (name) {
		case "reconnect":
			if (args.size() > 1) {
				throw new IllegalArgumentException("reconnect takes at most 1 argument");
			}
			if (args.size() > 0) {
				parseLocator(args.get(0));
			}
			connect();
			break;
			
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
	
	public void processConsoleCommand(String name, List<String> args) throws Exception {
		switch (name) {
		case "--help":
			printConsoleHelp();
			break;
		
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
