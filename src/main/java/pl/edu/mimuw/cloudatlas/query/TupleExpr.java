package pl.edu.mimuw.cloudatlas.query;

import java.util.List;

public class TupleExpr extends Expr {

	private List<Expr> items;

	public TupleExpr(List<Expr> items) {
		this.items = items;
	}

	public List<Expr> getItems() {
		return items;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
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
		TupleExpr other = (TupleExpr) obj;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return true;
	}
}
