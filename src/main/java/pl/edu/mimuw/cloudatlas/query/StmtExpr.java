package pl.edu.mimuw.cloudatlas.query;

public class StmtExpr extends Expr {

	private Stmt stmt;

	public StmtExpr(Stmt stmt) {
		this.stmt = stmt;
	}
	
	@Override
	public Result evaluate(Env env) throws EvaluationException {
		if (this.stmt instanceof SelectStmt) {
			return ((SelectStmt) this.stmt).evaluateAsExpr(env);
		} else {
			throw new EvaluationException("Cannot compute value of " + this.stmt.getClass() + " in expression");
		}
	}

	public Stmt getStmt() {
		return stmt;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stmt == null) ? 0 : stmt.hashCode());
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
		StmtExpr other = (StmtExpr) obj;
		if (stmt == null) {
			if (other.stmt != null)
				return false;
		} else if (!stmt.equals(other.stmt))
			return false;
		return true;
	}
}
