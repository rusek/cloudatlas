package pl.edu.mimuw.cloudatlas.zones;

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
}
