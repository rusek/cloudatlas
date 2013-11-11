package pl.edu.mimuw.cloudatlas.query;

public abstract class Expr {
	
	public Result evaluate(Env env) throws EvaluationException {
		throw new RuntimeException("evaluate() not implemented for" + this.getClass()); // FIXME remove this
	}
	
	public abstract int hashCode();
	public abstract boolean equals(Object obj);
}
