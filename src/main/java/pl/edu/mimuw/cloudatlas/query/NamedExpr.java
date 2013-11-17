package pl.edu.mimuw.cloudatlas.query;

public class NamedExpr {

	private Expr expr;
	private String name;
	
	public NamedExpr(Expr expr, String name) {
		assert expr != null;
		
		this.expr = expr;
		this.name = name;
	}
	
	public SelectionResult evaluate(Env env) throws EvaluationException {
		return this.expr.evaluate(env).accept(new ResultVisitor<SelectionResult, EvaluationException>() {

			public SelectionResult visit(OneResult result)
					throws EvaluationException {
				return new SelectionResult(result.getType(), result.getValue(), name);
			}

			public SelectionResult visit(ListResult result)
					throws EvaluationException {
				throw new EvaluationException("Cannot return ListResult from SELECT clause");
			}

			public SelectionResult visit(ColumnResult result)
					throws EvaluationException {
				throw new EvaluationException("Cannot return ColumnResult from SELECT clause");
			}
		});
	}
	
	public String getName() {
		return name;
	}
	
	public Expr getExpr() {
		return expr;
	}

	@Override
	public String toString() {
		return "NamedExpr [expr=" + expr + ", name=" + name + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expr == null) ? 0 : expr.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		NamedExpr other = (NamedExpr) obj;
		if (expr == null) {
			if (other.expr != null)
				return false;
		} else if (!expr.equals(other.expr))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
