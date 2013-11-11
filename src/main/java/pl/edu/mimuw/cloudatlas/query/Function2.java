package pl.edu.mimuw.cloudatlas.query;

import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;

public interface Function2<A1 extends Value, A2 extends Value, R extends Value> {

	public Type<R> getReturnType();
	
	public R evaluate(A1 arg1, A2 arg2) throws EvaluationException;
}
