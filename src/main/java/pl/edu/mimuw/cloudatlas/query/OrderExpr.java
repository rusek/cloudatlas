package pl.edu.mimuw.cloudatlas.query;

public class OrderExpr extends Expr {

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
	
}
