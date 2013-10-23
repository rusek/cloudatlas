package pl.edu.mimuw.cloudatlas.query;

public class NamedExpr {

	private Expr expr;
	private String name;
	
	public NamedExpr(Expr expr, String name) {
		assert expr != null;
		
		this.expr = expr;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Expr getExpr() {
		return expr;
	}
}
