package pl.edu.mimuw.cloudatlas.query;

import pl.edu.mimuw.cloudatlas.attributes.Value;

public class BinExpr extends Expr {

	private Expr left;
	private Expr right;
	private BinOp op;

	public BinExpr(Expr left, Expr right, BinOp op) {
		assert left != null;
		assert right != null;
		assert op != null;
		
		this.left = left;
		this.right = right;
		this.op = op;
	}
	
	@Override
	public Result evaluate(Env env) throws EvaluationException {
		Result leftResult = this.left.evaluate(env);
		Result rightResult = this.right.evaluate(env);
		
		Function2<? extends Value, ? extends Value, ? extends Value> func = op.getFuncForTypes(
				leftResult.getType(), rightResult.getType());
		if (func == null) {
			throw new EvaluationException("Cannot evaluate " + op + " on " + leftResult.getType() + " and " +
					rightResult.getType());
		}
		
		return Functions.evaluate(func, leftResult, rightResult);
	}
	
	public Expr getLeft() {
		return left;
	}
	
	public Expr getRight() {
		return right;
	}
	
	public BinOp getOp() {
		return op;
	}
	
	@Override
	public String toString() {
		return "BinExpr [left=" + left + ", right=" + right + ", op=" + op
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((op == null) ? 0 : op.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
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
		BinExpr other = (BinExpr) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (op != other.op)
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}
}
