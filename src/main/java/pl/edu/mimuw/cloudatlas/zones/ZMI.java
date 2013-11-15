package pl.edu.mimuw.cloudatlas.zones;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;

public class ZMI {
	
	private final Map<String, Attribute> attributes = new HashMap<String, Attribute>();
	
	public Collection<Attribute> getAttributes() {
		return attributes.values();
	}
	
	public <V extends Value> void addAttribute(String name, Type<V> type, V value) {
		assert !attributes.containsKey(name);
		
		attributes.put(name, new Attribute(name, type, value));
	}
	
	public void addAttribute(String name, Type<? extends Value> type) {
		addAttribute(name, type, null);
	}
	
	public void addAttribute(String name, Value value) {
		assert !attributes.containsKey(name); 
		
		attributes.put(name, new Attribute(name, value));
	}
	
	public Value getAttributeValue(String name) {
		Attribute attribute = attributes.get(name);
		return attribute == null ? null : attribute.getValue();
	}

}
