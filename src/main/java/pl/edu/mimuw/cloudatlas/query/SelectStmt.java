package pl.edu.mimuw.cloudatlas.query;

import java.util.List;

public class SelectStmt extends Stmt {
	
	private List<NamedExpr> selection;
	private Expr where;
	private List<OrderExpr> ordering;

	public SelectStmt(List<NamedExpr> selection, Expr where, List<OrderExpr> ordering) {
		this.selection = selection;
		this.where = where;
		this.ordering = ordering;
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

	public List<OrderExpr> getOrdering() {
		return ordering;
	}

}
