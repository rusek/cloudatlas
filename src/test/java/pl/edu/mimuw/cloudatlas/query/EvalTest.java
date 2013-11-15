package pl.edu.mimuw.cloudatlas.query;

import java.util.ArrayList;
import java.util.List;

import pl.edu.mimuw.cloudatlas.attributes.BooleanValue;
import pl.edu.mimuw.cloudatlas.attributes.DoubleValue;
import pl.edu.mimuw.cloudatlas.attributes.DurationValue;
import pl.edu.mimuw.cloudatlas.attributes.IntegerValue;
import pl.edu.mimuw.cloudatlas.attributes.ListType;
import pl.edu.mimuw.cloudatlas.attributes.ListValue;
import pl.edu.mimuw.cloudatlas.attributes.SetValue;
import pl.edu.mimuw.cloudatlas.attributes.SimpleType;
import pl.edu.mimuw.cloudatlas.attributes.SimpleValue;
import pl.edu.mimuw.cloudatlas.attributes.StringValue;
import pl.edu.mimuw.cloudatlas.attributes.Value;
import pl.edu.mimuw.cloudatlas.zones.ZMI;
import junit.framework.TestCase;

public class EvalTest extends TestCase {
	
	public static void testOrderBy() throws Exception {
		ZMI zmi1 = new ZMI();
		zmi1.addAttribute("id", new IntegerValue(1));
		zmi1.addAttribute("str", new StringValue("xx"));
		zmi1.addAttribute("w1", new DoubleValue(1.0));
		zmi1.addAttribute("intList", ListType.of(SimpleType.INTEGER));
		
		ZMI zmi2 = new ZMI();
		zmi2.addAttribute("id", new IntegerValue(2));
		zmi2.addAttribute("w1", new DoubleValue(2.0));
		
		ZMI zmi3 = new ZMI();
		zmi3.addAttribute("id", new IntegerValue(3));
		zmi3.addAttribute("str", new StringValue("x"));
		zmi3.addAttribute("w1", new DoubleValue(2.0));
		
		ZMI zmi4 = new ZMI();
		zmi4.addAttribute("id", new IntegerValue(4));
		zmi4.addAttribute("str", new StringValue("yx"));
		zmi4.addAttribute("w1", new DoubleValue(1.0));
		
		List<ZMI> zmis = new ArrayList<ZMI>();
		zmis.add(zmi1); zmis.add(zmi2); zmis.add(zmi3); zmis.add(zmi4);
		
		assertSelectThrows("SELECT 1 ORDER BY intList", zmis);
		
		assertSelectReturns("SELECT first(10, id)", zmis,
				listWith(new IntegerValue(1), new IntegerValue(2), new IntegerValue(3), new IntegerValue(4)));
		assertSelectReturns("SELECT first(10, id) ORDER BY id ASC", zmis,
				listWith(new IntegerValue(1), new IntegerValue(2), new IntegerValue(3), new IntegerValue(4)));
		assertSelectReturns("SELECT first(10, id) ORDER BY id ASC NULLS FIRST", zmis,
				listWith(new IntegerValue(1), new IntegerValue(2), new IntegerValue(3), new IntegerValue(4)));
		assertSelectReturns("SELECT first(10, id) ORDER BY id ASC NULLS LAST", zmis,
				listWith(new IntegerValue(1), new IntegerValue(2), new IntegerValue(3), new IntegerValue(4)));
		assertSelectReturns("SELECT first(10, id) ORDER BY id DESC", zmis,
				listWith(new IntegerValue(4), new IntegerValue(3), new IntegerValue(2), new IntegerValue(1)));
		
		assertSelectReturns("SELECT first(10, id) ORDER BY str", zmis,
				listWith(new IntegerValue(3), new IntegerValue(1), new IntegerValue(4), new IntegerValue(2)));
		assertSelectReturns("SELECT first(10, id) ORDER BY str ASC", zmis,
				listWith(new IntegerValue(3), new IntegerValue(1), new IntegerValue(4), new IntegerValue(2)));
		assertSelectReturns("SELECT first(10, id) ORDER BY str DESC", zmis,
				listWith(new IntegerValue(2), new IntegerValue(4), new IntegerValue(1), new IntegerValue(3)));
		assertSelectReturns("SELECT first(10, id) ORDER BY str ASC NULLS FIRST", zmis,
				listWith(new IntegerValue(2), new IntegerValue(3), new IntegerValue(1), new IntegerValue(4)));
		assertSelectReturns("SELECT first(10, id) ORDER BY str ASC", zmis,
				listWith(new IntegerValue(3), new IntegerValue(1), new IntegerValue(4), new IntegerValue(2)));
		assertSelectReturns("SELECT first(10, id) ORDER BY str DESC NULLS FIRST", zmis,
				listWith(new IntegerValue(2), new IntegerValue(4), new IntegerValue(1), new IntegerValue(3)));
		assertSelectReturns("SELECT first(10, id) ORDER BY str DESC NULLS LAST", zmis,
				listWith(new IntegerValue(4), new IntegerValue(1), new IntegerValue(3), new IntegerValue(2)));
		
		assertSelectReturns("SELECT first(10, id) ORDER BY w1 ASC, id ASC", zmis,
				listWith(new IntegerValue(1), new IntegerValue(4), new IntegerValue(2), new IntegerValue(3)));
		assertSelectReturns("SELECT first(10, id) ORDER BY w1 ASC, id DESC", zmis,
				listWith(new IntegerValue(4), new IntegerValue(1), new IntegerValue(3), new IntegerValue(2)));
		assertSelectReturns("SELECT first(10, id) ORDER BY w1 DESC, id ASC", zmis,
				listWith(new IntegerValue(2), new IntegerValue(3), new IntegerValue(1), new IntegerValue(4)));
		assertSelectReturns("SELECT first(10, id) ORDER BY w1 DESC, id DESC", zmis,
				listWith(new IntegerValue(3), new IntegerValue(2), new IntegerValue(4), new IntegerValue(1)));
		
	}
	
	public static void testAggregates() throws Exception {
		ZMI zmi1 = new ZMI();
		zmi1.addAttribute("int", new IntegerValue(1));
		zmi1.addAttribute("nullInt", SimpleType.INTEGER, null);
		zmi1.addAttribute("intList", listWith(new IntegerValue(2), new IntegerValue(3)));
		zmi1.addAttribute("str", new StringValue("a"));
		zmi1.addAttribute("float", new DoubleValue(3.5));
		zmi1.addAttribute("dur", new DurationValue(100));
		zmi1.addAttribute("boolTF", new BooleanValue(false));
		zmi1.addAttribute("boolTT", new BooleanValue(true));
		zmi1.addAttribute("boolFF", new BooleanValue(false));
		
		ZMI zmi2 = new ZMI();
		zmi2.addAttribute("intList", listWith(new IntegerValue(2), new IntegerValue(3), new IntegerValue(5)));
		zmi2.addAttribute("str", new StringValue("b"));
		zmi2.addAttribute("strSet", setWith(new StringValue("aa"), new StringValue("bb")));
		zmi2.addAttribute("float", new DoubleValue(2));
		zmi2.addAttribute("dur", new DurationValue(500));
		zmi2.addAttribute("boolTF", new BooleanValue(true));

		ZMI zmi3 = new ZMI();
		zmi3.addAttribute("int", new IntegerValue(5));
		zmi3.addAttribute("str", new StringValue("c"));
		zmi3.addAttribute("strSet", setWith(new StringValue("dd"), new StringValue("aa")));
		zmi3.addAttribute("boolTT", new BooleanValue(true));
		zmi3.addAttribute("boolFF", new BooleanValue(false));
		
		List<ZMI> zmis = new ArrayList<ZMI>();
		zmis.add(zmi1); zmis.add(zmi2); zmis.add(zmi3);
		
		assertSelectThrows("SELECT count(1)", zmis);
		assertSelectReturns("SELECT count(int)", zmis, new IntegerValue(2));
		assertSelectReturns("SELECT count(is_null(int))", zmis, new IntegerValue(3));
		assertSelectReturns("SELECT count(str)", zmis, new IntegerValue(3));
		
		assertSelectThrows("SELECT unfold(1)", zmis);
		assertSelectThrows("SELECT unfold(str)", zmis);
		assertSelectReturns("SELECT count(unfold(strSet))", zmis, new IntegerValue(4));
		assertSelectReturns("SELECT count(is_null(unfold(intList)))", zmis, new IntegerValue(5));
		assertSelectReturns("SELECT count(is_null(unfold(strSet)))", zmis, new IntegerValue(4));
		
		assertSelectThrows("SELECT distinct(1)", zmis);
		assertSelectReturns("SELECT count(distinct(str))", zmis, new IntegerValue(3));
		assertSelectReturns("SELECT count(distinct(int))", zmis, new IntegerValue(2));
		assertSelectReturns("SELECT count(is_null(distinct(int)))", zmis, new IntegerValue(2));
		assertSelectReturns("SELECT count(distinct(unfold(intList)))", zmis, new IntegerValue(3));
		assertSelectReturns("SELECT count(is_null(distinct(unfold(intList))))", zmis, new IntegerValue(3));
		
		assertSelectThrows("SELECT avg(0)", zmis);
		assertSelectReturns("SELECT avg(int)", zmis, new DoubleValue(3.0));
		assertSelectReturns("SELECT avg(int) WHERE false", zmis, null);
		assertSelectThrows("SELECT avg(str)", zmis);
		assertSelectThrows("SELECT avg(intList)", zmis);
		assertSelectReturns("SELECT avg(float)", zmis, new DoubleValue(5.5 / 2.0));
		assertSelectReturns("SELECT avg(float) WHERE float > 666.0", zmis, null);
		
		assertSelectThrows("SELECT sum(0)", zmis);
		assertSelectReturns("SELECT sum(int)", zmis, new IntegerValue(6));
		assertSelectReturns("SELECT sum(float)", zmis, new DoubleValue(5.5));
		assertSelectReturns("SELECT sum(dur)", zmis, new DurationValue(600));

		assertSelectReturns("SELECT sum(int) WHERE false", zmis, null);
		assertSelectReturns("SELECT sum(float) WHERE false", zmis, null);
		assertSelectReturns("SELECT sum(dur) WHERE false", zmis, null);
		assertSelectThrows("SELECT sum(str)", zmis);
		
		assertSelectThrows("SELECT land(false)", zmis);
		assertSelectThrows("SELECT land(int)", zmis);
		assertSelectReturns("SELECT land(boolTF)", zmis, new BooleanValue(false));
		assertSelectReturns("SELECT land(boolTF) WHERE boolTF = true", zmis, new BooleanValue(true));
		assertSelectReturns("SELECT land(boolTF) WHERE boolTF = false", zmis, new BooleanValue(false));
		assertSelectReturns("SELECT land(boolTT)", zmis, new BooleanValue(true));
		assertSelectReturns("SELECT land(boolFF)", zmis, new BooleanValue(false));
		assertSelectReturns("SELECT land(boolTF) WHERE false", zmis, null);
		
		assertSelectThrows("SELECT lor(false)", zmis);
		assertSelectThrows("SELECT lor(int)", zmis);
		assertSelectReturns("SELECT lor(boolTF)", zmis, new BooleanValue(true));
		assertSelectReturns("SELECT lor(boolTF) WHERE boolTF = true", zmis, new BooleanValue(true));
		assertSelectReturns("SELECT lor(boolTF) WHERE boolTF = false", zmis, new BooleanValue(false));
		assertSelectReturns("SELECT lor(boolTT)", zmis, new BooleanValue(true));
		assertSelectReturns("SELECT lor(boolFF)", zmis, new BooleanValue(false));
		assertSelectReturns("SELECT lor(boolTF) WHERE false", zmis, null);
		
		assertSelectThrows("SELECT min(1)", zmis);
		assertSelectThrows("SELECT min(intList)", zmis);
		assertSelectReturns("SELECT min(int)", zmis, new IntegerValue(1));
		assertSelectReturns("SELECT min(int) WHERE int > 1", zmis, new IntegerValue(5));
		assertSelectReturns("SELECT min(nullInt)", zmis, null);
		
		assertSelectThrows("SELECT max(1)", zmis);
		assertSelectThrows("SELECT max(intList)", zmis);
		assertSelectReturns("SELECT max(int)", zmis, new IntegerValue(5));
		assertSelectReturns("SELECT max(int) WHERE int < 5", zmis, new IntegerValue(1));
		assertSelectReturns("SELECT max(nullInt)", zmis, null);
		
		assertSelectThrows("SELECT first(1, 1)", zmis);
		assertSelectThrows("SELECT first(false, 1)", zmis);
		assertSelectThrows("SELECT first(1, false)", zmis);
		assertSelectThrows("SELECT first(false, int)", zmis);
		assertSelectThrows("SELECT first(1, intList)", zmis);
		assertSelectReturns("SELECT first(1, int)", zmis, listWith(new IntegerValue(1)));
		assertSelectThrows("SELECT first(min(nullInt), int)", zmis);
		assertSelectReturns("SELECT first(2, int)", zmis, listWith(new IntegerValue(1), new IntegerValue(5)));
		assertSelectReturns("SELECT first(3, int)", zmis, listWith(new IntegerValue(1), new IntegerValue(5)));
		assertSelectReturns("SELECT first(0, int)", zmis, ListValue.of(SimpleType.INTEGER));
		assertSelectReturns("SELECT first(-2147483649, int)", zmis, ListValue.of(SimpleType.INTEGER));
		assertSelectReturns("SELECT first(2147483648, int)", zmis, listWith(new IntegerValue(1), new IntegerValue(5)));
		assertSelectReturns("SELECT first(3, unfold(intList))", zmis,
				listWith(new IntegerValue(2), new IntegerValue(3), new IntegerValue(2)));
		
		assertSelectThrows("SELECT last(1, 1)", zmis);
		assertSelectThrows("SELECT last(false, 1)", zmis);
		assertSelectThrows("SELECT last(1, false)", zmis);
		assertSelectThrows("SELECT last(false, int)", zmis);
		assertSelectThrows("SELECT last(1, intList)", zmis);
		assertSelectReturns("SELECT last(1, int)", zmis, listWith(new IntegerValue(5)));
		assertSelectThrows("SELECT last(min(nullInt), int)", zmis);
		assertSelectReturns("SELECT last(2, int)", zmis, listWith(new IntegerValue(1), new IntegerValue(5)));
		assertSelectReturns("SELECT last(3, int)", zmis, listWith(new IntegerValue(1), new IntegerValue(5)));
		assertSelectReturns("SELECT last(0, int)", zmis, ListValue.of(SimpleType.INTEGER));
		assertSelectReturns("SELECT last(-2147483649, int)", zmis, ListValue.of(SimpleType.INTEGER));
		assertSelectReturns("SELECT last(2147483648, int)", zmis, listWith(new IntegerValue(1), new IntegerValue(5)));
		assertSelectReturns("SELECT last(3, unfold(intList))", zmis,
				listWith(new IntegerValue(2), new IntegerValue(3), new IntegerValue(5)));
		assertSelectReturns("SELECT last(3, unfold(intList)) ORDER BY dur DESC NULLS FIRST", zmis,
				listWith(new IntegerValue(5), new IntegerValue(2), new IntegerValue(3)));
		
		assertSelectThrows("SELECT random(1, 1)", zmis);
		assertSelectThrows("SELECT random(false, 1)", zmis);
		assertSelectThrows("SELECT random(1, false)", zmis);
		assertSelectThrows("SELECT random(false, int)", zmis);
		assertSelectThrows("SELECT random(1, intList)", zmis);
		{
			boolean found1 = false, found5 = false;
			for (int i = 0; i < 100 && !(found1 && found5); i++) {
				Value value = evaluateOneValueSelect("SELECT random(1, int)", zmis);
				if (value.equals(listWith(new IntegerValue(5)))) {
					found5 = true;
				} else if (value.equals(listWith(new IntegerValue(1)))) {
					found1 = true;
				} else {
					fail("Unexpected value: " + value);
				}
			}
			assertTrue(found1);
			assertTrue(found5);
			
		}
		assertSelectThrows("SELECT random(min(nullInt), int)", zmis);
		// TODO waiting for size(...)
		//assertSelectReturns("SELECT size((random(2, int))", zmis, new IntegerValue(2));
		//assertSelectReturns("SELECT size(random(3, int))", zmis, new IntegerValue(2));
		assertSelectReturns("SELECT random(0, int)", zmis, ListValue.of(SimpleType.INTEGER));
		assertSelectReturns("SELECT random(-2147483649, int)", zmis, ListValue.of(SimpleType.INTEGER));
		//assertSelectReturns("SELECT size(random(2147483648, int))", zmis, new IntegerValue(2));
		
	}
	
	// Helpers
	
	private static <V extends SimpleValue> ListValue<V> listWith(V... items) {
		@SuppressWarnings("unchecked")
		ListValue<V> retVal = ListValue.of((SimpleType<V>) items[0].getType());
		for (V item : items) {
			retVal.addItem(item);
		}
		return retVal;
	}
	
	private static <V extends SimpleValue> SetValue<V> setWith(V... items) {
		@SuppressWarnings("unchecked")
		SetValue<V> retVal = SetValue.of((SimpleType<V>) items[0].getType());
		for (V item : items) {
			retVal.addItem(item);
		}
		return retVal;
	}
	
	private static void assertSelectThrows(String source) throws ParseException {
		List<ZMI> zmis = new ArrayList<ZMI>();
		assertSelectThrows(source, zmis);
	}
	
	private static void assertSelectThrows(String source, ZMI zmi) throws ParseException {
		List<ZMI> zmis = new ArrayList<ZMI>();
		zmis.add(zmi);
		assertSelectThrows(source, zmis);
	}
	
	private static void assertSelectThrows(String source, List<ZMI> zmis) throws ParseException {
		try {
			Value value = evaluateOneValueSelect(source, zmis);
			fail("EvaluationException not thrown, returned " + value);
		}
		catch (EvaluationException ex) {
			// Uncomment to see error messages
			// System.out.println(ex.getMessage());
		}
	}
	
	private static void assertSelectReturns(String source, Value expected) throws EvaluationException, ParseException {
		List<ZMI> zmis = new ArrayList<ZMI>();
		assertSelectReturns(source, zmis, expected);
	}

	private static void assertSelectReturns(String source, ZMI zmi, Value expected) throws EvaluationException, ParseException {
		List<ZMI> zmis = new ArrayList<ZMI>();
		zmis.add(zmi);
		assertSelectReturns(source, zmis, expected);
	}

	private static void assertSelectReturns(String source, List<ZMI> zmis, Value expected) throws EvaluationException, ParseException {
		Value actual = evaluateOneValueSelect(source, zmis);
		assertEquals(expected, actual);
	}

	private static Value evaluateOneValueSelect(String source, List<ZMI> zmis) throws EvaluationException, ParseException {
		SelectStmt select = (SelectStmt) Parsers.parseQuery(source).get(0);
		Env env = Env.createFromZMIs(zmis);
		return select.evaluate(env).get(0).getValue();
	}
}
