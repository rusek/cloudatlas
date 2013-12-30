package pl.edu.mimuw.cloudatlas.cli;

import pl.edu.mimuw.cloudatlas.attributes.ContactValue;
import pl.edu.mimuw.cloudatlas.attributes.Value;
import pl.edu.mimuw.cloudatlas.attributes.ValueFormatException;
import pl.edu.mimuw.cloudatlas.zones.Attribute;

import java.io.PrintWriter;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;

public class Client {
	private PrintWriter out = new PrintWriter(System.out);
	private boolean shouldQuit = false;
	private boolean shellMode = false;
	
	private List<FacadeConnection> connections = new ArrayList<FacadeConnection>();
	
	private Map<String, Command> shellCommands = new LinkedHashMap<String, Command>();
	private Map<String, Command> consoleCommands = new LinkedHashMap<String, Command>();
	
	private void addCommonCommand(Command command) {
		shellCommands.put(command.getName(), command);
		consoleCommands.put(command.getName(), command);
	}
	
	private void addShellCommand(Command command) {
		shellCommands.put(command.getName(), command);
	}
	
	private void addConsoleCommand(Command command) {
		consoleCommands.put(command.getName(), command);
	}
	
	public Client() {
		addCommonCommand(new GetAttributeValueCommand());
		addCommonCommand(new SetMyAttributeValueCommand());
		addCommonCommand(new GetAttributesCommand());
		addCommonCommand(new GetMyGlobalNameCommand());
		addCommonCommand(new ListZoneNamesCommand());
		addCommonCommand(new SetFallbackContactsCommand());
		addCommonCommand(new ShowStatsCommand());
		addCommonCommand(new SendStatsCommand());
		addCommonCommand(new InstallQueryCommand());
		addCommonCommand(new InstallQueryAtCommand());
		addCommonCommand(new UninstallQueryCommand());
		addCommonCommand(new UninstallQueryAtCommand());
		addCommonCommand(new ExtinguishCommand());
		addShellCommand(new ReconnectCommand());
		addShellCommand(new QuitCommand());
		addConsoleCommand(new ShellCommand());
		addCommonCommand(new HelpCommand());
	}
	
	public void addConnection(String locator) throws RemoteException, NotBoundException {
		connections.add(new FacadeConnection(locator));
	}
	
	public void executeCommand(String name, List<String> args) {
		if (name.equals("")) {
			// No command supplied
			return;
		}
		
		Command command = (shellMode ? shellCommands : consoleCommands).get(name);
		if (command == null) {
			out.println("Unrecognized command: " + name);
		} else {
			try {
				command.execute(args);
			} catch (Exception ex) {
				command.printException(ex);
			}
		}
		
		out.flush();
	}
	
	public static void printUsage() {
		Client client = new Client();
		client.executeCommand("help", Collections.<String>emptyList());
	}
	
	public static void main(String[] args) throws Exception {
		List<String> locators = new ArrayList<String>();
		int offset = 0;;
		while(offset < args.length && args[offset].length() != 0 && args[offset].charAt(0) == '-') {
			switch (args[offset]) {
			case "--locator":
			case "-l":
				if (offset + 1 >= args.length) {
					System.out.println("Missing -l option value");
					return;
				}
				locators.add(args[offset + 1]);
				offset += 2;
				break;
			
			case "--help":
			case "-h":
				printUsage();
				return;
				
			default:
				System.out.println("Unrecognized option: " + args[offset]);
				return;
			}
			
		}
		if (offset == args.length) {
			System.out.println("Missing command name");
			return;
		}
		if (locators.isEmpty()) {
			locators.add("localhost");
		}
		
		Client client = new Client();
		for (String locator : locators) {
			client.addConnection(locator);
		}
		client.executeCommand(args[offset], Arrays.asList(args).subList(offset + 1, args.length));
	}
	
	private abstract class Command {
		public abstract String getName();
		
		public abstract void printHelp();
		
		public abstract void execute(List<String> args) throws Exception;
		
		public void println(Object o) {
			out.println(o);
		}
		
		public void printException(Exception ex) {
			if (ex instanceof IllegalArgumentException || ex instanceof IllegalStateException) {
				println(ex.getMessage());
			} else if (ex instanceof ServerException && ex.getCause() instanceof RemoteException) {
				println(ex.getCause().getMessage());
			} else {
				ex.printStackTrace(out);
			}
		}
	}
	
	private abstract class FacadeCommand extends Command {
		private String indent = "";

		@Override
		public void println(Object o) {
			out.println(indent + o);
		}
		
		public abstract void prepare(List<String> args) throws Exception;
		public abstract void executeOnFacade(CommandFacade facade) throws Exception;
		public void complete() throws Exception {}
		
		@Override
		public void execute(List<String> args) throws Exception {
			prepare(args);
			if (connections.isEmpty()) {
				throw new IllegalStateException("Not connected");
			} else if (connections.size() == 1) {
				executeOnFacade(connections.get(0).getFacade());
			} else {
				indent = "    ";
				for (FacadeConnection connection : connections) {
					try {
						out.println(connection.getLocator() + ":");
						executeOnFacade(connection.getFacade());
					} catch (Exception ex) {
						printException(ex);
					}
				}
				indent = "";
			}
			complete();
		}
	}
	
	private class SendStatsCommand extends FacadeCommand {
		private List<Attribute> stats;

		@Override
		public String getName() {
			return "sendStats";
		}

		@Override
		public void printHelp() {
			println("  - sendStats");
			println("    Collects statistics and sends them to the agent.");
		}

		@Override
		public void prepare(List<String> args) throws Exception {
			if (args.size() != 0) {
				throw new IllegalArgumentException("showStats command takes no arguments");
			}
			stats = new StatsCollector().collectStats();
		}

		@Override
		public void executeOnFacade(CommandFacade facade) throws Exception {
			facade.setMyAttributes(stats);
		}

		@Override
		public void complete() throws Exception {
			if (shellMode) {
				for (Attribute stat : stats) {
					println(stat);
				}
			}
		}
	}
	
	private class ShowStatsCommand extends Command {

		@Override
		public String getName() {
			return "showStats";
		}

		@Override
		public void printHelp() {
			println("  - showStats");
			println("    Collects statistics and prints values.");
		}

		@Override
		public void execute(List<String> args) throws Exception {
			if (args.size() != 0) {
				throw new IllegalArgumentException("showStats command takes no arguments");
			}
			List<Attribute> stats = new StatsCollector().collectStats();
			for (Attribute stat : stats) {
				out.println(stat);
			}
		}
		
	}
	
	private class SetFallbackContactsCommand extends FacadeCommand {
		private List<ContactValue> contacts;

		@Override
		public void prepare(List<String> args) throws Exception {
			if (args.size() == 0) {
				throw new IllegalArgumentException("setFallbackContacts takes at least one argument");
			}
			contacts = new ArrayList<ContactValue>();
			for (String arg : args) {
				try {
					contacts.add(ContactValue.parseContact(arg));
				} catch (ValueFormatException ex) {
					throw new IllegalArgumentException("Invalid contact: " + arg);
				}
			}
		}

		@Override
		public void executeOnFacade(CommandFacade facade) throws Exception {
			facade.setFallbackContacts(contacts);
			
		}

		@Override
		public String getName() {
			return "setFallbackContacts";
		}

		@Override
		public void printHelp() {
			println("  - setFallbackContacts <host:port> [<host:port>] ...");
			println("    Sets fallback contacts.");
		}
	}
	
	private class GetAttributeValueCommand extends FacadeCommand {
		private String zoneName;
		private String attrName;
		
		@Override
		public void prepare(List<String> args) throws Exception {
			if (args.size() != 2) {
				throw new IllegalArgumentException("getAttributeValue command takes exactly 2 arguments");
			}
			zoneName = args.get(0);
			attrName = args.get(1);
		}

		@Override
		public void executeOnFacade(CommandFacade facade) throws Exception {
			Value v = facade.getAttributeValue(zoneName, attrName);
			println(v);
		}

		@Override
		public String getName() {
			return "getAttributeValue";
		}

		@Override
		public void printHelp() {
			println("  - getAttributeValue <zoneName> <attributeName>");
			println("    Returns attribute value of a given zone.");
		}
		
	}
	
	private class SetMyAttributeValueCommand extends FacadeCommand {

		private Attribute attribute; 
		
		@Override
		public void prepare(List<String> args) throws Exception {
			if (args.size() < 2) {
				throw new IllegalArgumentException("setMyAttributeValue takes at least 2 arguments");
			}
			
			attribute = new Attribute(args.get(0), ValueParser.parseValue(args.subList(1, args.size())));
		}

		@Override
		public void executeOnFacade(CommandFacade facade) throws Exception {
			List<Attribute> attributes = new ArrayList<Attribute>();
			attributes.add(attribute);
			facade.setMyAttributes(attributes);
		}

		@Override
		public String getName() {
			return "setMyAttributeValue";
		}

		@Override
		public void printHelp() {
			//       ************************************************************************
			println("  - setMyAttributeValue <attributeName> <simpleType> <value>");
			println("    setMyAttributeValue <attributeName> list:<simpleType> <item1> ...");
			println("    setMyAttributeValue <attributeName> set:<simpleType> <item1> ...");
			println("    Sets value of a given attribute that belongs to agent's own zone.");
			println("    Possible simpleType values: boolean, integer, double, string, time,");
			println("    duration, contact.");
			
		}
		
	}
	
	private class GetAttributesCommand extends FacadeCommand {
		private String zoneName;
		
		@Override
		public void prepare(List<String> args) throws Exception {
			if (args.size() != 1) {
				throw new IllegalArgumentException("getAttributes command takes exactly 1 argument");
			}
			zoneName = args.get(0);
		}

		@Override
		public void executeOnFacade(CommandFacade facade) throws Exception {
			List<Attribute> attributes = facade.getAttributes(zoneName);
			Collections.sort(attributes, new Comparator<Attribute>() {

				@Override
				public int compare(Attribute arg0, Attribute arg1) {
					return arg0.getName().compareTo(arg1.getName());
				}
				
			});
			for (Attribute attribute : attributes) {
				println(attribute);
			}
		}

		@Override
		public String getName() {
			return "getAttributes";
		}

		@Override
		public void printHelp() {
			println("  - getAttributes");
			println("    Returns all attributes of a given zone");
		}
		
	}
	
	private class GetMyGlobalNameCommand extends FacadeCommand {

		@Override
		public void prepare(List<String> args) throws Exception {
			if (args.size() != 0) {
				throw new IllegalArgumentException("getMyGlobalName command takes no arguments");
			}
		}

		@Override
		public void executeOnFacade(CommandFacade facade) throws Exception {
			String globalName = facade.getMyGlobalName();
			println(globalName);
		}

		@Override
		public String getName() {
			return "getMyGlobalName";
		}

		@Override
		public void printHelp() {
			println("  - getMyGlobalName");
			println("    Returns agent zone name.");
		}
		
	}
	
	private class ListZoneNamesCommand extends FacadeCommand {

		@Override
		public void prepare(List<String> args) throws Exception {
			if (args.size() != 0) {
				throw new IllegalArgumentException("listZoneNames command takes no arguments");
			}
		}

		@Override
		public void executeOnFacade(CommandFacade facade) throws Exception {
			for (String zoneName : facade.getZoneNames()) {
				println(zoneName);
			}
		}

		@Override
		public String getName() {
			return "listZoneNames";
		}

		@Override
		public void printHelp() {
			println("  - listZoneNames");
			println("  - Lists global names of all zones known by the agent");
		}
		
	}
	
	private class ExtinguishCommand extends FacadeCommand {

		@Override
		public void prepare(List<String> args) throws Exception {
			if (args.size() != 0) {
				throw new IllegalArgumentException("extinguish command takes no arguments");
			}
		}

		@Override
		public void executeOnFacade(CommandFacade facade) throws Exception {
			facade.extinguish();
			
		}

		@Override
		public String getName() {
			return "extinguish";
		}

		@Override
		public void printHelp() {
			println("  - extinguish");
			println("    Remotely shuts down agent.");
		}
		
	}
	
	private class InstallQueryCommand extends FacadeCommand {
		
		private String attributeName;
		private String query;

		@Override
		public void prepare(List<String> args) throws Exception {
			if (args.size() != 2) {
				throw new IllegalArgumentException("installQuery command takes exactly 2 arguments");
			}
			attributeName = args.get(0);
			query = args.get(1);
		}

		@Override
		public void executeOnFacade(CommandFacade facade) throws Exception {
			facade.installQuery(attributeName, query);
			
		}

		@Override
		public String getName() {
			return "installQuery";
		}

		@Override
		public void printHelp() {
			println("  - installQuery <&attributeName> <query>");
			println("    Installs a given query in all agent zones.");
			
		}
	}

	private class InstallQueryAtCommand extends FacadeCommand {

		private String zoneName;
		private String attributeName;
		private String query;

		@Override
		public void prepare(List<String> args) throws Exception {
			if (args.size() != 3) {
				throw new IllegalArgumentException("installQuery command takes exactly 3 arguments");
			}
			zoneName = args.get(0);
			attributeName = args.get(1);
			query = args.get(2);
		}

		@Override
		public void executeOnFacade(CommandFacade facade) throws Exception {
			facade.installQueryAt(zoneName, attributeName, query);
			
		}

		@Override
		public String getName() {
			return "installQueryAt";
		}

		@Override
		public void printHelp() {
			println("  - installQueryAt <zoneName> <&attributeName> <query>");
			println("    Installs a given query in a given zone.");
			
		}
	}
	
	private class UninstallQueryCommand extends FacadeCommand {
		
		private String attributeName;

		@Override
		public void prepare(List<String> args) throws Exception {
			if (args.size() != 1) {
				throw new IllegalArgumentException("uninstallQuery command takes exactly 1 argument");
			}
			attributeName = args.get(0);
		}

		@Override
		public void executeOnFacade(CommandFacade facade) throws Exception {
			facade.uninstallQuery(attributeName);
			
		}

		@Override
		public String getName() {
			return "uninstallQuery";
		}

		@Override
		public void printHelp() {
			println("  - uninstallQuery <&attributeName>");
			println("    Uninstalls a given query from all agent zones.");
			
		}
	}

	private class UninstallQueryAtCommand extends FacadeCommand {

		private String zoneName;
		private String attributeName;

		@Override
		public void prepare(List<String> args) throws Exception {
			if (args.size() != 2) {
				throw new IllegalArgumentException("uninstallQueryAt command takes exactly 2 arguments");
			}
			zoneName = args.get(0);
			attributeName = args.get(1);
		}

		@Override
		public void executeOnFacade(CommandFacade facade) throws Exception {
			facade.uninstallQueryAt(zoneName, attributeName);
		}

		@Override
		public String getName() {
			return "uninstallQueryAt";
		}

		@Override
		public void printHelp() {
			println("  - uninstallQueryAt <zoneName> <&attributeName>");
			println("    Uninstalls a given query from a given zone.");
			
		}
	}
	
	private class QuitCommand extends Command {

		@Override
		public String getName() {
			return "quit";
		}

		@Override
		public void printHelp() {
			println("  - quit");
			println("    Exits shell.");
		}

		@Override
		public void execute(List<String> args) throws Exception {
			if (args.size() != 0) {
				throw new IllegalArgumentException("quit command takes no arguments");
			}
			shouldQuit = true;
		}
		
	}
	
	private class ReconnectCommand extends Command {

		@Override
		public String getName() {
			return "reconnect";
		}

		@Override
		public void printHelp() {
			println("  - reconnect [locator] [locator] ...");

			println("    If no argument is given, reestablishes connection to all current\n" +
			        "    agents. If one or more  locator arguments are given (see Locator\n" +
					"    format section for argument details), then connects to the specified\n" +
			        "    agents instead.");
		}

		@Override
		public void execute(List<String> args) throws Exception {
			if (args.size() > 0) {
				connections.clear();
				for (String arg : args) {
					connections.add(new FacadeConnection(arg));
				}
			} else {
				for (FacadeConnection connection : connections) {
					connection.reconnect();
				}
			}
		}
		
	}
	
	private class ShellCommand extends Command {

		@Override
		public String getName() {
			return "shell";
		}

		@Override
		public void printHelp() {
			println("  - shell");
			println("    Runs interactive shell.");
			
		}

		@Override
		public void execute(List<String> args) throws Exception {
			if (args.size() != 0) {
				throw new IllegalArgumentException("shell command takes no arguments");
			}
			
			shellMode = true;
			out.flush();
			ConsoleReader reader = new ConsoleReader();
			reader.addCompleter(createCompleter());
			reader.setPrompt(">: ");
			out = new PrintWriter(reader.getOutput());

			String line = null;
			while (!shouldQuit && (line = reader.readLine()) != null) {
				try {
					List<String> parts = Utils.splitArgs(line);
					if (!parts.isEmpty()) {
						executeCommand(parts.get(0), parts.subList(1, parts.size()));
					}
				} catch (Exception ex) {
					printException(ex);
				}
				out.flush();
			}
			if (line == null) {
				out.println();
				out.flush();
			}
		}
		
		public Completer createCompleter() {
			return new StringsCompleter(shellCommands.keySet());
		}
	}
	
	private class HelpCommand extends Command {

		@Override
		public String getName() {
			return "help";
		}

		@Override
		public void printHelp() {
			println("  - help");
			println("    This one should be obvious now.");
		}

		@Override
		public void execute(List<String> args) throws Exception {
			if (args.size() != 0) {
				throw new IllegalArgumentException("help command takes no arguments");
			}
			
			Collection<Command> currentCommands = (shellMode ? shellCommands : consoleCommands).values();
			
			if (!shellMode) {
				println("Usage: ./client.sh [-l locator] command commandArg1 commandArg2 ...");
				println("    -l");
				println("    Specify agent locator (see Locator format section for details).\n" +
						"    This option may be specified multiple times.");
				println("");
			}
			
			println("Available commands:");
			for (Command command : currentCommands) {
				command.printHelp();
			}
			
			println("");
			println("Locator format: [host[:port]][/zoneId]");
			println("    host:port part should point to RMI registry where the agent is\n" +
			        "    registered. If omitted, localhost is assumed. If no zoneId is\n" +
					"    provided, the client will try to connect to any registered agent.");
			println("    Examples: localhost, students/uw/khaki14, /uw/violet08");
		}
		
	}
	
	private static class FacadeConnection {
		private static final Pattern LOCATOR_PATTERN = Pattern.compile("(?:([^:/]+)(?::(\\d+))?)?(/.+)?");

		private String locator;
		private String registryHost = "localhost";
		private Integer registryPort = null;
		private String agentZoneName = null;
		
		private CommandFacade commandFacade;

		public FacadeConnection(String locator) throws RemoteException, NotBoundException {
			parseLocator(locator);
			reconnect();
		}
		
		public CommandFacade getFacade() {
			return commandFacade;
		}
		
		public String getLocator() {
			return locator;
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
			this.locator = locator;
		}
		
		public void reconnect() throws RemoteException, NotBoundException {
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

	}
}
