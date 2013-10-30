package pl.edu.mimuw.cloudatlas.query;

import java.util.List;

import pl.edu.mimuw.cloudatlas.attributes.IntegerValue;
import pl.edu.mimuw.cloudatlas.attributes.StringValue;

import junit.framework.TestCase;

public class ParseTest extends TestCase {
	
	public static void testInteger() throws ParseException {
		Expr expected = new ValueExpr(new IntegerValue(100));
		Expr actual = parseExpr(" 100 ");
		assertEquals(expected, actual);
	}
	
	public static void testCanParse() throws ParseException {
		Parsers.parseQuery("SELECT 1");
		Parsers.parseQuery("SELECT 1 WHERE 2");
		Parsers.parseQuery("SELECT 1 WHERE 2 ORDER BY 3");
		Parsers.parseQuery("SELECT 1 WHERE 2 ORDER BY 3 ASC");
		Parsers.parseQuery("SELECT 1 WHERE 2 ORDER BY 3 DESC");
		Parsers.parseQuery("SELECT 1 WHERE 2 ORDER BY 3 NULLS FIRST");
		Parsers.parseQuery("SELECT 1 WHERE 2 ORDER BY 3 NULLS LAST");
		Parsers.parseQuery("SELECT 1 WHERE 2 ORDER BY 3 DESC NULLS LAST");
		Parsers.parseQuery("SELECT 1 ORDER BY 3 DESC NULLS FIRST");
		Parsers.parseQuery("SELECT 1, 2, 3");
		Parsers.parseQuery("SELECT 1 WHERE 2 AND 3");
		Parsers.parseQuery("SELECT 1 ORDER BY 2, 3, 4");
		Parsers.parseQuery("SELECT now()");
		Parsers.parseQuery("SELECT avg(x + 2)");
		Parsers.parseQuery("SELECT first(2, 2)");
		Parsers.parseQuery("SELECT <1, 2>");
		Parsers.parseQuery("SELECT <1, 2> > -3");
		Parsers.parseQuery("SELECT <1, 2 >= 5> > -3");
		Parsers.parseQuery("SELECT <1, 2 >= 5> > -3");
		Parsers.parseQuery("SELECT []");
		Parsers.parseQuery("SELECT {}");
		Parsers.parseQuery("SELECT \"aaa\" REGEXP \"bbb\" ");
		Parsers.parseQuery("SELECT \"aaa\" REGEXP \"bbb\" ");
	}
	
	public static void testString() throws ParseException {
		parseExprAndExpectString(" \"aaa\"", "aaa");
		parseExprAndExpectString(" \"\"", "");
		parseExprAndExpectString("\"\\n\"", "\n");
		parseExprAndExpectString("\"\\r\"", "\r");
		parseExprAndExpectString("\"\\\"\"", "\"");
		parseExprAndExpectString("\"x\\\\x\"", "x\\x");
		parseExprAndExpectString("\"\\\\\\\"\\\\\"", "\\\"\\");
	}
	
	public static void testPrecedence() throws ParseException {
		assertExprSourcesEqual("1 + 1 + 1", "(1 + 1) + 1");
		assertExprSourcesEqual("1 - 1 - 1", "(1 - 1) - 1");
		assertExprSourcesEqual("1 * 1 * 1", "(1 * 1) * 1");
		assertExprSourcesEqual("1 / 1 / 1", "(1 / 1) / 1");
		assertExprSourcesEqual("1 + 1 * 1 + 1", "1 + (1 * 1) + 1");
		assertExprSourcesEqual("1 * 1 + 1", "(1 * 1) + 1");
		assertExprSourcesEqual("1 * 1 / 1 % 1", "((1 * 1) / 1) % 1");
		assertExprSourcesEqual("-1 + 1", "(-1) + 1");
		assertExprSourcesEqual("NOT 1 + 2", "NOT (1 + 2)");
		assertExprSourcesEqual("1 AND 2 OR 3", "(1 AND 2) OR 3");
		assertExprSourcesEqual("1 OR 2 AND 3", "1 OR (2 AND 3)");
		assertExprSourcesEqual("NOT NOT 1 AND NOT 2", "(NOT (NOT 1)) AND (NOT 2)");
		assertExprSourcesEqual("NOT 1 < 2", "NOT (1 < 2)");
		assertExprSourcesEqual("NOT -1 * 2", "NOT ((-1) * 2)");
	}
	
	private static void parseExprAndExpectString(String source, String expectedString) throws ParseException {
		Expr expected = new ValueExpr(new StringValue(expectedString));
		Expr actual = parseExpr(source);
		assertEquals(expected, actual);
	}
	
	private static Expr parseExpr(String expr) throws ParseException {
		List<Stmt> stmts = Parsers.parseQuery("SELECT (" + expr + ")");
		return ((SelectStmt) stmts.get(0)).getSelection().get(0).getExpr();
	}
	
	private static void assertSourcesEqual(String expected, String actual) throws ParseException {
		List<Stmt> expectedList = Parsers.parseQuery(expected);
		List<Stmt> actualList = Parsers.parseQuery(actual);
		assertEquals(expectedList, actualList);
	}
	
	private static void assertExprSourcesEqual(String expected, String actual) throws ParseException {
		assertSourcesEqual("SELECT (" + expected + ")", "SELECT (" + actual + ")");
	}

}
