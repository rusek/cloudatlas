package pl.edu.mimuw.cloudatlas;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import pl.edu.mimuw.cloudatlas.query.Parsers;
import pl.edu.mimuw.cloudatlas.query.SelectStmt;
import pl.edu.mimuw.cloudatlas.zones.ZMI;

public class TestInterpreter {
	
	private static ZMIs initializeEnv() throws UnknownHostException, ParseException {
		ZMIs zmis = new ZMIs();
		zmis.addZMI(null, 0, null, "/uw/violet07", "2012/11/09 20:10:17.342 CET", null, 0);
		ZMI uw = zmis.addZMI(zmis.getRoot(), 1, "uw", "/uw/violet07", "2012/11/09 20:8:13.123 CET", null, 0);
		ZMI pjwstk = zmis.addZMI(zmis.getRoot(), 1, "pjwstk", "/pjwstk/whatever01", "2012/11/09 20:8:13.123 CET", null, 0);
		zmis.addZMI(uw, 2, "violet07", "/uw/violet07", "2012/11/09 18:00:00.000", new String[] {"uw.edu.pl", "biol.uw.edu.pl", "mimuw.edu.pl"}, 1,
			new String[] {"uw.edu.pl"}, "2011/11/09 20:8:13.123", 0.9, 3, null, new String[] {"tola", "tosia"}, "+13 12:00:00.000");
		zmis.addZMI(uw, 2, "khaki31", "/uw/khaki31", "2012/11/09 20:03:00.000", new String[] {"students.mimuw.edu.pl"}, 1,
			new String[] {"students.mimuw.edu.pl"}, "2011/11/09 20:12:13.123", null, 3, false, new String[] {"agatka", "beatka", "celina"}, "-13 11:00:00.000");
		zmis.addZMI(uw, 2, "khaki13", "/uw/khaki13", "2012/11/09 21:03:00.000", new String[] {"duch.mimuw.edu.pl", "wazniak.mimuw.edu.pl"}, 1,
				new String[] {"wazniak.mimuw.edu.pl"}, null, 0.1, null, true, null, null);
		ZMI tmp = zmis.addZMI(pjwstk, 2, "whatever01", "pjwstk/whatever01", "2012/11/09 21:12:00.000", new String[] {"pjwstk.edu.pl"}, 1,
				new String[] {"pjwstk.edu.pl"}, "2012/10/18 07:03:00.000", 0.1, 7);
		zmis.addList(tmp, "pph_modules", new String[] {"rewrite"});
		tmp = zmis.addZMI(pjwstk, 2, "whatever02", "pjwstk/whatever02", "2012/11/09 21:13:00.000", new String[] {"gdansk.pjwstk.edu.pl"}, 1,
				new String[] {"gdansk.pjwstk.edu.pl"}, "2012/10/18 07:04:00.000", 0.4, 13);
		zmis.addList(tmp, "pph_modules", new String[] {"odbc"});
		
		return zmis;
	}
	
	public static void main(String[] args) {
		try {
			ZMIs zmis = initializeEnv();
			Scanner sc = new Scanner(System.in);
			while(sc.hasNextLine()) {
				String line = sc.nextLine();
				Pattern p = Pattern.compile("^&(\\w+): (.+);?$");
				Matcher m = p.matcher(line);
				m.find();
				String attr = m.group(1);
				String query = m.group(2);
				SelectStmt select = (SelectStmt) Parsers.parseQuery(query).get(0);
				zmis.getRoot().computeAttribute(attr, select);
				zmis.getRoot().print();
			}
			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}

class ZMIs {
	private ZMI root;
	
	public ZMI getRoot() {
		return root;
	}
	
	public ZMI addZMI(ZMI parent, Integer level, String name, String owner, String time, String[] contacts, Integer cardinality) throws ParseException, UnknownHostException {
		ZMI zmi = new ZMI();
		zmi.addAttribute("level", SimpleType.INTEGER, level != null ? new IntegerValue(level) : null);
		zmi.addAttribute("name", SimpleType.STRING, name != null ? new StringValue(name) : null);
		zmi.addAttribute("owner", SimpleType.STRING, owner != null ? new StringValue(owner) : null);
		addTime(zmi, "time", time);
		addContacts(zmi, "contacts", contacts);
		zmi.addAttribute("cardinality", SimpleType.INTEGER, cardinality != null ? new IntegerValue(cardinality) : null);
		
		if(parent == null)
			root = zmi;
		else
			parent.addChild(zmi);
		return zmi;
	}
	
	public ZMI addZMI(ZMI parent, Integer level, String name, String owner, String time, String[] contacts, Integer cardinality,
			String[] members, String creation, Double cpu_usage, Integer num_cores) throws UnknownHostException, ParseException {
		ZMI zmi = addZMI(parent, level, name, owner, time, contacts, cardinality);
		addContacts(zmi, "members", members);
		addTime(zmi, "creation", creation);
		zmi.addAttribute("cpu_usage", SimpleType.DOUBLE, cpu_usage != null ? new DoubleValue(cpu_usage) : null);
		zmi.addAttribute("num_cores", SimpleType.INTEGER, num_cores != null ? new IntegerValue(num_cores) : null);
		return zmi;
	}
	
	public ZMI addZMI(ZMI parent, Integer level, String name, String owner, String time, String[] contacts, Integer cardinality,
			String[] members, String creation, Double cpu_usage, Integer num_cores, Boolean has_ups, String[] some_names, String expiry) throws UnknownHostException, ParseException {
		ZMI zmi = addZMI(parent, level, name, owner, time, contacts, cardinality, members, creation, cpu_usage, num_cores);
		zmi.addAttribute("has_ups", SimpleType.BOOLEAN, has_ups != null ? new BooleanValue(has_ups) : null);
		addList(zmi, "some_names", some_names);
		addDuration(zmi, "expiry", expiry);
		return zmi;
		
	}
	
	public void addDuration(ZMI zmi, String name, String time) {
		if(time == null) {
			zmi.addAttribute(name, SimpleType.DURATION, null);
		}
		else {
			int start = 1;
			int mul = 1;
			if(time.charAt(0) == '-')
				mul = -1;
			else if(time.charAt(0) != '+')
				start = 0;
			String[] tmp = time.substring(start).split(" ");
			int days = Integer.parseInt(tmp[0]);
			String[] timeS = tmp[1].split(":");
			int hours = Integer.parseInt(timeS[0]);
			int mins = Integer.parseInt(timeS[1]);
			String[] sec = timeS[2].split("\\.");
			int secs = Integer.parseInt(sec[0]);
			int mils = Integer.parseInt(sec[1]);
			zmi.addAttribute(name, new DurationValue(mul * (((((days * 24) + hours) * 60 + mins) * 60 + secs) * 1000 + mils)));
		}
	}
	
	public void addTime(ZMI zmi, String name, String time) throws ParseException {
		Long res = null;
		if(time != null) {
			DateFormat df = (time.split(" ").length > 2) ? TimeValue.createDateFormat() : TimeValue.createNoZoneDateFormat();
			res = df.parse(time).getTime();
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
	
	public void addContacts(ZMI zmi, String name, String[] list) throws UnknownHostException {
		SetValue<ContactValue> contactValues = SetValue.of(SimpleType.CONTACT);
		if(list != null) {
			for(String s : list) {
				contactValues.addItem(new ContactValue(InetAddress.getByName(s)));
			}
		}
		zmi.addAttribute(name, contactValues);
	}
	
}
