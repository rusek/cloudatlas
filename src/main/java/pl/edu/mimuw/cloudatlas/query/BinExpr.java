package pl.edu.mimuw.cloudatlas.query;

import pl.edu.mimuw.cloudatlas.attributes.Type;
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
	
	private Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypesOrThrow(
			Type<? extends Value> type1, Type<? extends Value> type2) throws EvaluationException {
		Function2<? extends Value, ? extends Value, ? extends Value> retVal = op.getFuncForTypes(type1, type2);
		if (retVal == null) {
			throw new EvaluationException("Cannot evaluate " + op + " on " + type1 + " and " + type2);
		}
		return retVal;
	}
	
	@Override
	public Result evaluate(Env env) throws EvaluationException {
		final Result leftResult = this.left.evaluate(env);
		final Result rightResult = this.right.evaluate(env);
		
		// No type safety here - types of leftResult and rightResult are known only in runtime
		@SuppressWarnings("unchecked")
		final Function2<Value, Value, Value> func = (Function2<Value, Value, Value>)
				getFuncForTypesOrThrow(leftResult.getType(), rightResult.getType());
		
		return leftResult.accept(new ResultVisitor<Result, EvaluationException>() {

			public Result visit(final OneResult leftResult) throws EvaluationException {
				return rightResult.accept(new ResultVisitor<Result, EvaluationException>() {

					public Result visit(OneResult rightResult)
							throws EvaluationException {
						return OneResult.createFromFunc(func, leftResult.getValue(), rightResult.getValue());
					}

					public Result visit(ListResult rightResult)
							throws EvaluationException {
						return ListResult.createFromFunc(func, leftResult.getValue(), rightResult.getValues());
					}

					public Result visit(ColumnResult rightResult)
							throws EvaluationException {
						return ColumnResult.createFromFunc(func, leftResult.getValue(), rightResult.getValues());
					}
					
				});
			}

			public Result visit(final ListResult leftResult) throws EvaluationException {
				return rightResult.accept(new ResultVisitor<Result, EvaluationException>() {

					public Result visit(OneResult rightResult)
							throws EvaluationException {
						return ListResult.createFromFunc(func, leftResult.getValues(), rightResult.getValue());
					}

					public Result visit(ListResult rightResult)
							throws EvaluationException {
						throw new EvaluationException("Cannot apply binary operation to ListResult and ListResult");
					}

					public Result visit(ColumnResult rightResult)
							throws EvaluationException {
						throw new EvaluationException("Cannot apply binary operation to ListResult and ColumnResult");
					}
					
				});
			}

			public Result visit(final ColumnResult leftResult) throws EvaluationException {
				return rightResult.accept(new ResultVisitor<Result, EvaluationException>() {

					public Result visit(OneResult rightResult)
							throws EvaluationException {
						return ColumnResult.createFromFunc(func, leftResult.getValues(), rightResult.getValue());
					}

					public Result visit(ListResult rightResult)
							throws EvaluationException {
						throw new EvaluationException("Cannot apply binary operation to ColumnResult and ListResult");
					}

					public Result visit(ColumnResult rightResult)
							throws EvaluationException {
						return ColumnResult.createFromFunc(func, leftResult.getValues(), rightResult.getValues());
					}
					
				});
			}
			
		});
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
