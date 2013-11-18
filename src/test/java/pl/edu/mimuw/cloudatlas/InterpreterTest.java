package pl.edu.mimuw.cloudatlas;

import pl.edu.mimuw.cloudatlas.attributes.IntegerValue;
import pl.edu.mimuw.cloudatlas.attributes.StringValue;
import junit.framework.TestCase;

public class InterpreterTest extends TestCase {

	public static void testCardinality() throws Exception {
		prep("&cardinalities: SELECT sum(cardinality) AS cardinality");
		assertEquals(new IntegerValue(5), TestInterpreter.rootZone.getZMI().getAttributeValue("cardinality"));
	}

	public static void testCleanup() throws Exception {
		prep("&garbage: SELECT 1 AS garbage");
		TestInterpreter.uninstallQueries();
		assertEquals(null, TestInterpreter.rootZone.getZMI().getAttributeValue("&garbage"));
		assertEquals(new IntegerValue(1), TestInterpreter.rootZone.getZMI().getAttributeValue("garbage"));
	}
	
	public static void testTrickyInput() throws Exception {
		prep("&tricky: SELECT \";\" AS tricky");
		assertEquals(new StringValue(";"), TestInterpreter.rootZone.getZMI().getAttributeValue("tricky"));
	}
	
	public static void testTwoQueries() throws Exception {
		prep("&one: SELECT 1 AS one; &two: SELECT 2 AS two");
		assertEquals(new IntegerValue(1), TestInterpreter.rootZone.getZMI().getAttributeValue("one"));
		assertEquals(new IntegerValue(1), TestInterpreter.rootZone.findZone("/uw").getZMI().getAttributeValue("one"));
		assertEquals(new IntegerValue(1), TestInterpreter.rootZone.findZone("pjwstk").getZMI().getAttributeValue("one"));
		assertEquals(new IntegerValue(2), TestInterpreter.rootZone.getZMI().getAttributeValue("two"));
	}
	
	private static void prep(String query) throws Exception {
		TestInterpreter.initializeZones();
		TestInterpreter.parseQueries(query);
		TestInterpreter.installQueries();
		TestInterpreter.evaluateQueries();
	}
}
