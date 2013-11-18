package pl.edu.mimuw.cloudatlas;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import pl.edu.mimuw.cloudatlas.attributes.BooleanValue;
import pl.edu.mimuw.cloudatlas.attributes.ContactValue;
import pl.edu.mimuw.cloudatlas.attributes.DoubleValue;
import pl.edu.mimuw.cloudatlas.attributes.DurationValue;
import pl.edu.mimuw.cloudatlas.attributes.IntegerValue;
import pl.edu.mimuw.cloudatlas.attributes.ListValue;
import pl.edu.mimuw.cloudatlas.attributes.SetValue;
import pl.edu.mimuw.cloudatlas.attributes.SimpleType;
import pl.edu.mimuw.cloudatlas.attributes.StringValue;
import pl.edu.mimuw.cloudatlas.attributes.TimeValue;
import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;
import pl.edu.mimuw.cloudatlas.attributes.ValueFormatException;
import pl.edu.mimuw.cloudatlas.query.Env;
import pl.edu.mimuw.cloudatlas.query.EvaluationException;
import pl.edu.mimuw.cloudatlas.query.Parsers;
import pl.edu.mimuw.cloudatlas.query.SelectStmt;
import pl.edu.mimuw.cloudatlas.query.SelectionResult;
import pl.edu.mimuw.cloudatlas.query.Stmt;
import pl.edu.mimuw.cloudatlas.zones.Attribute;
import pl.edu.mimuw.cloudatlas.zones.ZMI;
import pl.edu.mimuw.cloudatlas.zones.Zone;

public class TestInterpreter {
	
	static Zone rootZone;
	
	static void initializeZones() throws TestException {
		ZoneBuilder builder = new ZoneBuilder();
		rootZone = builder.addZone(null, 0, null, "/uw/violet07", "2012/11/09 20:10:17.342 CET", null, 0);
		Zone uwZone = builder.addZone(rootZone, 1, "uw", "/uw/violet07", "2012/11/09 20:8:13.123 CET", null, 0);
		Zone pjwstkZone = builder.addZone(rootZone, 1, "pjwstk", "/pjwstk/whatever01", "2012/11/09 20:8:13.123 CET", null, 0);
		builder.addZone(uwZone, 2, "violet07", "/uw/violet07", "2012/11/09 18:00:00.000", new String[] {"UW1A", "UW1B", "UW1C"}, 1,
			new String[] {"UW1"}, "2011/11/09 20:8:13.123", 0.9, 3, null, new String[] {"tola", "tosia"}, "+13 12:00:00.000");
		builder.addZone(uwZone, 2, "khaki31", "/uw/khaki31", "2012/11/09 20:03:00.000", new String[] {"UW2A"}, 1,
			new String[] {"UW2A"}, "2011/11/09 20:12:13.123", null, 3, false, new String[] {"agatka", "beatka", "celina"}, "-13 11:00:00.000");
		builder.addZone(uwZone, 2, "khaki13", "/uw/khaki13", "2012/11/09 21:03:00.000", new String[] {"UW3A", "UW3B"}, 1,
				new String[] {"UW3B"}, null, 0.1, null, true, null, null);
		Zone tmp = builder.addZone(pjwstkZone, 2, "whatever01", "/pjwstk/whatever01", "2012/11/09 21:12:00.000", new String[] {"PJ1"}, 1,
				new String[] {"PJ1"}, "2012/10/18 07:03:00.000", 0.1, 7);
		builder.addList(tmp.getZMI(), "php_modules", new String[] {"rewrite"});
		tmp = builder.addZone(pjwstkZone, 2, "whatever02", "/pjwstk/whatever02", "2012/11/09 21:13:00.000", new String[] {"PJ2"}, 1,
				new String[] {"PJ2"}, "2012/10/18 07:04:00.000", 0.4, 13);
		builder.addList(tmp.getZMI(), "php_modules", new String[] {"odbc"});
	}
	
	static Map<String, String> queries = new LinkedHashMap<String, String>();
	
	static void readQueries() throws TestException {
		try {
			parseQueries(IOUtils.toString(System.in, "UTF-8"));
		} catch (IOException e) {
			throw new TestException(e);
		}
	}
	
	static void parseQueries(String input) throws TestException {
		Pattern pattern = Pattern.compile(
				"^\\s*(&[a-zA-Z0-9_]+):((?:[^;\"]|\"(?:[^\\\\\"]|\\\\.)*\")*)",
				Pattern.DOTALL);
		do {
			Matcher matcher = pattern.matcher(input);
			if (!matcher.find() || (matcher.end() != input.length() && input.charAt(matcher.end()) != ';')) {
				throw new TestException("Cannot parse input");
			}
			queries.put(matcher.group(1), StringUtils.trim(matcher.group(2)));
			
			if (matcher.end() != input.length()) {
				input = input.substring(matcher.end() + 1); // "+1" to skip ";" 
			} else {
				return;
			}
		} while (true);
	}
	
	static void installQueries() throws TestException {
		installQueries(rootZone);
		installQueries(rootZone.getChild("uw"));
		installQueries(rootZone.getChild("pjwstk"));
	}
	
	static void installQueries(Zone zone) throws TestException {
		for (Map.Entry<String, String> entry : queries.entrySet()) {
			if (zone.getZMI().hasAttribute(entry.getKey())) {
				throw new TestException("Attribute already present: " + entry.getKey());
			}
			zone.getZMI().addAttribute(entry.getKey(), new StringValue(entry.getValue()));
		}
	}
	
	static void evaluateQueries() throws TestException {
		evaluateQueries(rootZone.getChild("uw"));
		evaluateQueries(rootZone.getChild("pjwstk"));
		evaluateQueries(rootZone);
	}

	static void evaluateQueries(Zone zone) throws TestException {
		List<String> queriesInZone = new ArrayList<String>();
		
		for (Attribute attribute : zone.getZMI().getAttributes()) {
			if (attribute.getName().charAt(0) == '&') {
				if (!attribute.getType().equals(SimpleType.STRING) || attribute.getValue() == null) {
					throw new TestException("Invalid attribute " + attribute.getName() + " in zone " +
							zone.getGlobalName());
				}
				queriesInZone.add(((StringValue) attribute.getValue()).getString());
			}
		}
		
		for (String queryInZone : queriesInZone) {
			evaluateQuery(zone, queryInZone);
		}
	}
	
	@SuppressWarnings("unchecked")
	static void evaluateQuery(Zone zone, String querySource) throws TestException {
		try {
			List<Stmt> stmts = Parsers.parseQuery(querySource);
			Env env;
			try {
				env = Env.createFromZMIs(zone.getChildZMIs());
			} catch (IllegalArgumentException ex) {
				throw new TestException("Cannot create env for query evaluation. " + ex.getMessage());
			}
			
			for (Stmt stmt : stmts) {
				if (stmt instanceof SelectStmt) {
					for (SelectionResult selectionResult : ((SelectStmt) stmt).evaluate(env)) {
						if (selectionResult.getName() == null) {
							throw new TestException("Unnamed sel_item in query: " + querySource);
						}
						zone.getZMI().setAttribute(
								selectionResult.getName(),
								(Type<Value>) selectionResult.getType(),
								selectionResult.getValue());
					}
				} else {
					throw new TestException("Unrecognized statement node: " + stmt);
				}
			}
		} catch (pl.edu.mimuw.cloudatlas.query.ParseException e) {
			throw new TestException(e);
		} catch (EvaluationException e) {
			System.out.println("Failed to execute query in zone " + zone.getGlobalName() + ": " + querySource);
			System.out.println("    " + e.getMessage());
			
			// Uncomment to print stack trace (obviously...)
			// e.printStackTrace();
		}
	}
	
	static void printZones() {
		printZoneRecursively(rootZone);
	}
	
	static void printZoneRecursively(Zone zone) {
		System.out.println(zone.getGlobalName());
		for (Attribute attribute : zone.getZMI().getAttributes()) {
			System.out.println("    " + attribute.getName() + " : " + attribute.getType() + " = " +
					attribute.getValue());
		}
		for (Zone child : zone.getChildren()) {
			printZoneRecursively(child);
		}
	}
	
	static void uninstallQueries() {
		uninstallQueries(rootZone);
		uninstallQueries(rootZone.getChild("uw"));
		uninstallQueries(rootZone.getChild("pjwstk"));
	}
	
	static void uninstallQueries(Zone zone) {
		for (String key : queries.keySet()) {
			zone.getZMI().removeAttribute(key);
		}
	}
	
	public static void main(String[] args) {
		try {
			initializeZones();
			readQueries();
			installQueries();
			evaluateQueries();
			printZones();
			uninstallQueries();
		} catch (TestException e) {
			e.printStackTrace();
		}
	}

	public static class TestException extends Exception {

		public TestException() {
			super();
		}

		public TestException(String message, Throwable cause) {
			super(message, cause);
		}

		public TestException(String message) {
			super(message);
		}

		public TestException(Throwable cause) {
			super(cause);
		}

		private static final long serialVersionUID = 1L;
		
	}


	private static class ZoneBuilder {
		private Map<String, ContactValue> contactValues = new HashMap<String, ContactValue>();
		private byte nextIPLastByte = 1;
		
		private ContactValue getContact(String name) throws TestException {
			ContactValue result = contactValues.get(name);
			if (result == null) {
				try {
					result = new ContactValue(InetAddress.getByAddress(name, new byte[]{10, 0, 0, nextIPLastByte++}));
				} catch (UnknownHostException e) {
					throw new TestException(e);
				}
				contactValues.put(name, result);
			}
			return result;
		}
		
		public Zone addZone(Zone parent, Integer level, String name, String owner, String time, String[] contacts, Integer cardinality) throws TestException {
			ZMI zmi = new ZMI();
			zmi.addAttribute("level", SimpleType.INTEGER, level != null ? new IntegerValue(level) : null);
			zmi.addAttribute("name", SimpleType.STRING, name != null ? new StringValue(name) : null);
			zmi.addAttribute("owner", SimpleType.STRING, owner != null ? new StringValue(owner) : null);
			addTime(zmi, "timestamp", time);
			addContacts(zmi, "contacts", contacts);
			zmi.addAttribute("cardinality", SimpleType.INTEGER, cardinality != null ? new IntegerValue(cardinality) : null);
			
			Zone zone;
			if(parent == null)
				zone = Zone.createRoot(zmi);
			else
				zone = parent.addChild(name, zmi);
			return zone;
		}
		
		public Zone addZone(Zone parent, Integer level, String name, String owner, String time, String[] contacts, Integer cardinality,
				String[] members, String creation, Double cpu_usage, Integer num_cores) throws TestException {
			Zone zone = addZone(parent, level, name, owner, time, contacts, cardinality);
			ZMI zmi = zone.getZMI();
			addContacts(zmi, "members", members);
			addTime(zmi, "creation", creation);
			zmi.addAttribute("cpu_usage", SimpleType.DOUBLE, cpu_usage != null ? new DoubleValue(cpu_usage) : null);
			zmi.addAttribute("num_cores", SimpleType.INTEGER, num_cores != null ? new IntegerValue(num_cores) : null);
			return zone;
		}
		
		public Zone addZone(Zone parent, Integer level, String name, String owner, String time, String[] contacts, Integer cardinality,
				String[] members, String creation, Double cpu_usage, Integer num_cores, Boolean has_ups, String[] some_names, String expiry) throws TestException {
			Zone zone = addZone(parent, level, name, owner, time, contacts, cardinality, members, creation, cpu_usage, num_cores);
			ZMI zmi = zone.getZMI();
			zmi.addAttribute("has_ups", SimpleType.BOOLEAN, has_ups != null ? new BooleanValue(has_ups) : null);
			addList(zmi, "some_names", some_names);
			addDuration(zmi, "expiry", expiry);
			return zone;
			
		}
		
		public void addDuration(ZMI zmi, String name, String dur) throws TestException {
			if(dur == null) {
				zmi.addAttribute(name, SimpleType.DURATION, null);
			}
			else {
				try {
					zmi.addAttribute(name, DurationValue.parseDuration(dur));
				} catch (ValueFormatException e) {
					throw new TestException(e);
				}
			}
		}
		
		public void addTime(ZMI zmi, String name, String time) throws TestException {
			Long res = null;
			if(time != null) {
				DateFormat df = (time.split(" ").length > 2) ? TimeValue.createDateFormat() : TimeValue.createNoZoneDateFormat();
				try {
					res = df.parse(time).getTime();
				} catch (ParseException e) {
					throw new TestException(e);
				}
			}
			zmi.addAttribute(name, SimpleType.TIME, res != null ? new TimeValue(res) : null);
		}
		
		public void addList(ZMI zmi, String name, String[] list) {
			ListValue<StringValue> listValues = ListValue.of(SimpleType.STRING);
			if(list != null)
				for(String s : list) {
					listValues.addItem(new StringValue(s));
				}
			zmi.addAttribute(name, listValues);
		}
		
		public void addContacts(ZMI zmi, String name, String[] list) throws TestException {
			SetValue<ContactValue> contactValues = SetValue.of(SimpleType.CONTACT);
			if(list != null) {
				for(String s : list) {
					contactValues.addItem(getContact(s));
				}
			}
			zmi.addAttribute(name, contactValues);
		}
	}
}
