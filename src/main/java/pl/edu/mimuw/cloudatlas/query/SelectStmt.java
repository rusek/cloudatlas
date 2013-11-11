package pl.edu.mimuw.cloudatlas.query;

import java.util.ArrayList;
import java.util.List;

public class SelectStmt extends Stmt {
	
	private List<NamedExpr> selection;
	private Expr where;
	private List<OrderExpr> ordering;

	public SelectStmt(List<NamedExpr> selection, Expr where, List<OrderExpr> ordering) {
		assert selection != null;
		
		this.selection = selection;
		this.where = where;
		this.ordering = ordering;
	}
	
	public List<SelectionResult> evaluate(Env env) throws EvaluationException {
		if (this.where != null || this.ordering != null) {
			throw new RuntimeException("Not implemented for now :("); // FIXME implement this
		}
		
		List<SelectionResult> retVal = new ArrayList<SelectionResult>();
		for (NamedExpr namedExpr : this.selection) {
			retVal.add(namedExpr.evaluate(env));
		}
		return retVal;
	}
	
	public List<NamedExpr> getSelection() {
		return selection;
	}

	public Expr getWhere() {
		return where;
	}

	@Override
	public String toString() {
		return "SelectStmt [selection=" + selection + ", where=" + where
				+ ", ordering=" + ordering + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ordering == null) ? 0 : ordering.hashCode());
		result = prime * result
				+ ((selection == null) ? 0 : selection.hashCode());
		result = prime * result + ((where == null) ? 0 : where.hashCode());
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
		SelectStmt other = (SelectStmt) obj;
		if (ordering == null) {
			if (other.ordering != null)
				return false;
		} else if (!ordering.equals(other.ordering))
			return false;
		if (selection == null) {
			if (other.selection != null)
				return false;
		} else if (!selection.equals(other.selection))
			return false;
		if (where == null) {
			if (other.where != null)
				return false;
		} else if (!where.equals(other.where))
			return false;
		return true;
	}

	public List<OrderExpr> getOrdering() {
		return ordering;
	}

}
