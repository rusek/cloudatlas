package pl.edu.mimuw.cloudatlas.query;

import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;

public interface Function1<A extends Value, R extends Value> {

	public Type<R> getReturnType();
	
	public R evaluate(A arg) throws EvaluationException;
}
