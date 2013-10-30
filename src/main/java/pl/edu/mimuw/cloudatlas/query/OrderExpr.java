package pl.edu.mimuw.cloudatlas.query;

public class OrderExpr {

	private Expr expr;
	private Ord ord;
	private NullOrd nullOrd;
	
	public OrderExpr(Expr expr, Ord ord, NullOrd nullOrd) {
		assert expr != null;
		assert ord != null;
		assert nullOrd != null;
		
		this.expr = expr;
		this.ord = ord;
		this.nullOrd = nullOrd;
	}
	
	public Expr getExpr() {
		return expr;
	}
	public Ord getOrd() {
		return ord;
	}
	public NullOrd getNullOrd() {
		return nullOrd;
	}

	@Override
	public String toString() {
		return "OrderExpr [expr=" + expr + ", ord=" + ord + ", nullOrd="
				+ nullOrd + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expr == null) ? 0 : expr.hashCode());
		result = prime * result + ((nullOrd == null) ? 0 : nullOrd.hashCode());
		result = prime * result + ((ord == null) ? 0 : ord.hashCode());
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
		OrderExpr other = (OrderExpr) obj;
		if (expr == null) {
			if (other.expr != null)
				return false;
		} else if (!expr.equals(other.expr))
			return false;
		if (nullOrd != other.nullOrd)
			return false;
		if (ord != other.ord)
			return false;
		return true;
	}
	
}
