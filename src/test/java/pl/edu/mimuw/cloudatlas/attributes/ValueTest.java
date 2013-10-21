package pl.edu.mimuw.cloudatlas.attributes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;

import junit.framework.TestCase;

public class ValueTest extends TestCase {
	
	public static void testIntegerReadWrite() throws IOException {
		long n = 5;
		IntegerValue v = new IntegerValue(n);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		v.compactWrite(new DataOutputStream(outputStream));
		
		IntegerValue v2 = v.getType().compactReadValue(
				new DataInputStream(new ByteArrayInputStream(outputStream.toByteArray())));
		assertEquals(n, v2.getInteger());
	}
	
	public static void testListReadWrite() throws IOException {
		long expected[] = new long[]{1, 2, 3, 20, 50, 42, -123, 666};
		
		ListValue<IntegerValue> v = ListValue.of(SimpleType.INTEGER);
		for (long i : expected) {
			v.addItem(new IntegerValue(i));
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		v.compactWrite(new DataOutputStream(outputStream));
		
		ListValue<IntegerValue> v2 = v.getType().compactReadValue(
				new DataInputStream(new ByteArrayInputStream(outputStream.toByteArray())));
		assertEquals(expected.length, v2.getItems().size());
		Iterator<IntegerValue> it = v2.getItems().iterator();
		for (long i : expected) {
			assertTrue(i == it.next().getInteger());
		}
	}

}
