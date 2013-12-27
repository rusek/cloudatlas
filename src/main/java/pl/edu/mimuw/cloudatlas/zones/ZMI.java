package pl.edu.mimuw.cloudatlas.zones;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;

public class ZMI {
	
	private final Map<String, Attribute> attributes = new LinkedHashMap<String, Attribute>();
	
	public Collection<String> getAttributeNames() {
		return attributes.keySet();
	}
	
	public Collection<Attribute> getAttributes() {
		return attributes.values();
	}
	
	public boolean hasAttribute(String name) {
		return attributes.containsKey(name);
	}
	
	public Attribute getAttribute(String name) {
		return attributes.get(name);
	}
	
	public void removeAttribute(String name) {
		attributes.remove(name);
	}
	
	public void addAttribute(String name, Type<? extends Value> type, Value value) {
		assert !attributes.containsKey(name);
		
		setAttribute(name, type, value);
	}
	
	public void addAttribute(String name, Type<? extends Value> type) {
		addAttribute(name, type, null);
	}
	
	public void addAttribute(String name, Value value) {
		addAttribute(name, value.getType(), value);
	}
	
	public void setAttribute(String name, Value value) {
		attributes.put(name, new Attribute(name, value));
	}
	
	public void setAttribute(String name, Type<? extends Value> type) {
		setAttribute(name, type, null);
	}
	
	public void setAttribute(String name, Type<? extends Value> type, Value value) {
		attributes.put(name, new Attribute(name, type, value));
	}
	
	public Value getAttributeValue(String name) {
		Attribute attribute = attributes.get(name);
		return attribute == null ? null : attribute.getValue();
	}
	
	public ZMI deepCopy() {
		ZMI copy = new ZMI();
		for (Attribute attr : attributes.values()) {
			copy.attributes.put(attr.getName(), attr.deepCopy());
		}
		return copy;
	}
	
	public void compactWrite(DataOutput output) throws IOException {
		output.writeInt(attributes.size());
		for (Attribute attribute : attributes.values()) {
			attribute.compactWrite(output);
		}
	}
	
	public static ZMI compactRead(DataInput input) throws IOException {
		int size = input.readInt();
		if (size < 0) {
			throw new IOException("Negative length");
		}
		
		ZMI zmi = new ZMI();
		for (int i = 0; i < size; i++) {
			Attribute attribute = Attribute.compactRead(input);
			zmi.attributes.put(attribute.getName(), attribute);
		}
		
		return zmi;
	}
}
