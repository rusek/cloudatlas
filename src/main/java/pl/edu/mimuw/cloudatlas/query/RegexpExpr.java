package pl.edu.mimuw.cloudatlas.query;

public class RegexpExpr extends Expr {

	private Expr expr;
	private String patternSource;
	
	public RegexpExpr(Expr expr, String patternSource) {
		assert expr != null;
		assert patternSource != null;
		
		this.expr = expr;
		this.patternSource = patternSource;
	}
	
	public String getPatternSource() {
		return patternSource;
	}
	public Expr getExpr() {
		return expr;
	}
}
