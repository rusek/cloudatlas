package pl.edu.mimuw.cloudatlas.query;

public class AttributeExpr extends Expr {

	private String attributeName;
	
	public AttributeExpr(String attributeName) {
		assert attributeName != null;
		
		this.attributeName = attributeName;
	}

	@Override
	public Result evaluate(Env env) throws EvaluationException {
		return env.evaluateAttribute(attributeName);
	}

	public String getAttributeName() {
		return attributeName;
	}

	@Override
	public String toString() {
		return "AttributeExpr [attributeName=" + attributeName + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attributeName == null) ? 0 : attributeName.hashCode());
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
		AttributeExpr other = (AttributeExpr) obj;
		if (attributeName == null) {
			if (other.attributeName != null)
				return false;
		} else if (!attributeName.equals(other.attributeName))
			return false;
		return true;
	}
}
