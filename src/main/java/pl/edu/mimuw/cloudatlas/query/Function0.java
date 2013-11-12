package pl.edu.mimuw.cloudatlas.query;

import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;

public interface Function0<R extends Value> {

	public Type<R> getReturnType();
	
	public R evaluate() throws EvaluationException;
}
