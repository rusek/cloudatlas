package pl.edu.mimuw.cloudatlas.query;

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
	
	public Expr getLeft() {
		return left;
	}
	
	public Expr getRight() {
		return right;
	}
	
	public BinOp getOp() {
		return op;
	}
}
