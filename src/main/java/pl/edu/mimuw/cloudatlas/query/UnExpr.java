package pl.edu.mimuw.cloudatlas.query;

public class UnExpr extends Expr {

	private Expr expr;
	private UnOp op;
	
	public UnExpr(Expr expr, UnOp op) {
		assert expr != null;
		assert op != null;
		
		this.expr = expr;
		this.op = op;
	}
	
	public Expr getExpr() {
		return expr;
	}
	
	public UnOp getOp() {
		return op;
	}
}
