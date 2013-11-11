package pl.edu.mimuw.cloudatlas.query;

import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;

public abstract class Result {

	public abstract Type<? extends Value> getType();
	public abstract <R, E extends Exception> R accept(ResultVisitor<R, E> visitor) throws E;
}
