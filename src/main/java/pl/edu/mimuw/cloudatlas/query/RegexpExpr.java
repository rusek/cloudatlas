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

	@Override
	public String toString() {
		return "RegexpExpr [expr=" + expr + ", patternSource=" + patternSource
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expr == null) ? 0 : expr.hashCode());
		result = prime * result
				+ ((patternSource == null) ? 0 : patternSource.hashCode());
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
		RegexpExpr other = (RegexpExpr) obj;
		if (expr == null) {
			if (other.expr != null)
				return false;
		} else if (!expr.equals(other.expr))
			return false;
		if (patternSource == null) {
			if (other.patternSource != null)
				return false;
		} else if (!patternSource.equals(other.patternSource))
			return false;
		return true;
	}
}
