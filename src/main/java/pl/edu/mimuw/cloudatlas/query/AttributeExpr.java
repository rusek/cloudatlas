package pl.edu.mimuw.cloudatlas.query;

public class AttributeExpr extends Expr {

	private String attributeName;
	
	public AttributeExpr(String attributeName) {
		assert attributeName != null;
		
		this.attributeName = attributeName;
	}

	public String getAttributeName() {
		return attributeName;
	}
}
