package pl.edu.mimuw.cloudatlas.query;

public abstract class Expr {
	
	public abstract Result evaluate(Env env) throws EvaluationException;
	
	public abstract int hashCode();
	public abstract boolean equals(Object obj);
}
