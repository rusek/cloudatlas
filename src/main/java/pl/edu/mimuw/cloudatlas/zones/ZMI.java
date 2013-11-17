package pl.edu.mimuw.cloudatlas.zones;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;
import pl.edu.mimuw.cloudatlas.query.Env;
import pl.edu.mimuw.cloudatlas.query.EvaluationException;
import pl.edu.mimuw.cloudatlas.query.SelectStmt;
import pl.edu.mimuw.cloudatlas.query.SelectionResult;

public class ZMI {
	
	private final Map<String, Attribute> attributes = new HashMap<String, Attribute>();
	private List<ZMI> children = new LinkedList<ZMI>();
	
	
	public Collection<Attribute> getAttributes() {
		return attributes.values();
	}
	
	public <V extends Value> void addAttribute(String name, Type<V> type, V value) {
		assert !attributes.containsKey(name);
		
		setAttribute(name, type, value);
	}
	
	public void addAttribute(String name, Type<? extends Value> type) {
		addAttribute(name, type, null);
	}
	
	public void addAttribute(String name, Value value) {
		assert !attributes.containsKey(name); 
		
		setAttribute(name, value);
	}
	
	private void setAttribute(String name, Value value) {
		attributes.put(name, new Attribute(name, value));
	}
	
	private void setAttribute(String name, Type<? extends Value> type) {
		setAttribute(name, type, null);
	}
	
	private <V extends Value> void setAttribute(String name, Type<V> type, V value) {
		attributes.put(name, new Attribute(name, type, value));
	}
	
	public Value getAttributeValue(String name) {
		Attribute attribute = attributes.get(name);
		return attribute == null ? null : attribute.getValue();
	}
	
	public void addChild(ZMI zmi) {
		children.add(zmi);
	}
	
	public void computeAttribute(String name, SelectStmt select) throws EvaluationException {
		if(children.isEmpty())
			return;
		for(ZMI i : children) {
			i.computeAttribute(name, select);
		}
		Env env = Env.createFromZMIs(children);
		SelectionResult sr = select.evaluate(env).get(0);
		if(sr.getValue() == null)
			setAttribute(name, sr.getType());
		else
			setAttribute(name, sr.getValue());
	}
	
	public void print() {
		System.out.println(toString());
		for(ZMI i : children) {
			i.print();
		}
	}
	
	@Override
	public String toString() {
		String s = "";
		for(Attribute i : attributes.values()) {
			s += i.toString() + "\n";
		}
		return s;
	}

}
