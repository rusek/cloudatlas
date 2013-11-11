package pl.edu.mimuw.cloudatlas.query;

import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;

public class SelectionResult {

	private Type<? extends Value> type;
	private Value value;
	private String name;
	
	public SelectionResult(Type<? extends Value> type, Value value, String name) {
		assert type != null;
		
		this.type = type;
		this.value = value;
		this.name = name;
	}
	
	public Type<? extends Value> getType() {
		return type;
	}
	
	public Value getValue() {
		return value;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return "SelectionResult [" + type + ", " + value + ", " + name + "]";
	}
}
