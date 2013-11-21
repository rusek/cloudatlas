package pl.edu.mimuw.cloudatlas.zones;

import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;

public class Attribute {

	private final String name;
	private final Type<? extends Value> type;
	private Value value;
	
	public Attribute(String name, Type<? extends Value> type, Value value) {
		assert name != null;
		assert type != null;
		
		this.name = name;
		this.type = type;
		this.value = value;
	}
	
	public Attribute(String name, Value value) {
		assert name != null;
		assert value != null;
		
		this.name = name;
		this.type = value.getType();
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Type<? extends Value> getType() {
		return type;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}
	
	public String toString() {
		return name + ": " + value.toString() + " <" + type.toString() + ">";
	}
	
	public Attribute deepCopy() {
		return new Attribute(name, type, value);
	}
}
