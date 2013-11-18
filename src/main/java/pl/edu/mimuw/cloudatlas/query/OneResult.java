package pl.edu.mimuw.cloudatlas.query;

import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;

public class OneResult extends Result {
	
	private Type<? extends Value> type;
	private Value value;

	public <V extends Value> OneResult(Type<? extends V> type, V value) {
		assert type != null;
		assert value == null || value.getType().equals(type);
		
		this.type = type;
		this.value = value;
	}
	
	public OneResult(Value value) {
		assert value != null;
		
		this.type = value.getType();
		this.value = value;
	}

	public Type<? extends Value> getType() {
		return type;
	}

	public Value getValue() {
		return value;
	}

	@Override
	public <R, E extends Exception> R accept(ResultVisitor<R, E> visitor) throws E {
		return visitor.visit(this);
	}
	
	public static <A1 extends Value, A2 extends Value, R extends Value> OneResult createFromFunc(
			Function2<A1, A2, R> func, A1 arg1, A2 arg2) throws EvaluationException {
		return new OneResult(func.getReturnType(), func.evaluate(arg1, arg2));
	}
	
	public static <A extends Value, R extends Value> OneResult createFromFunc(
			Function1<A, R> func, A arg) throws EvaluationException {
		return new OneResult(func.getReturnType(), func.evaluate(arg));
	}
}
