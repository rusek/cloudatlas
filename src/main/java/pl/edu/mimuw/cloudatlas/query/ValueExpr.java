package pl.edu.mimuw.cloudatlas.query;

import pl.edu.mimuw.cloudatlas.attributes.Value;

public class ValueExpr extends Expr {
	
	private Value value;
	
	public ValueExpr(Value value) {
		assert value != null;
		
		this.value = value;
	}

	public Value getValue() {
		return value;
	}

}
