package pl.edu.mimuw.cloudatlas.query;

import pl.edu.mimuw.cloudatlas.attributes.Value;

public class ValueExpr extends Expr {
	
	private Value value;
	
	public ValueExpr(Value value) {
		assert value != null;
		
		this.value = value;
	}
	
	public Result evaluate(Env env) throws EvaluationException {
		return new OneResult(value.getType(), value);
	}

	public Value getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "ValueExpr [value=" + value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValueExpr other = (ValueExpr) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
