package pl.edu.mimuw.cloudatlas.query;

import pl.edu.mimuw.cloudatlas.attributes.Value;

public class UnExpr extends Expr {

	private Expr expr;
	private UnOp op;
	
	public UnExpr(Expr expr, UnOp op) {
		assert expr != null;
		assert op != null;
		
		this.expr = expr;
		this.op = op;
	}
	
	@Override
	public Result evaluate(Env env) throws EvaluationException {
		Result result = expr.evaluate(env);
		
		Function1<? extends Value, ? extends Value> func = op.getFuncByArgType(result.getType());
		if (func == null) {
			throw new EvaluationException("Cannot apply " + op + " operator to type " + result.getType());
		}
		return Functions.evaluate(func, result);
	}

	public Expr getExpr() {
		return expr;
	}
	
	public UnOp getOp() {
		return op;
	}

	@Override
	public String toString() {
		return "UnExpr [expr=" + expr + ", op=" + op + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expr == null) ? 0 : expr.hashCode());
		result = prime * result + ((op == null) ? 0 : op.hashCode());
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
		UnExpr other = (UnExpr) obj;
		if (expr == null) {
			if (other.expr != null)
				return false;
		} else if (!expr.equals(other.expr))
			return false;
		if (op != other.op)
			return false;
		return true;
	}
}
