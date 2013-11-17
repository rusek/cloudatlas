package pl.edu.mimuw.cloudatlas.query;

import java.util.regex.Pattern;

import pl.edu.mimuw.cloudatlas.attributes.BooleanValue;
import pl.edu.mimuw.cloudatlas.attributes.SimpleType;
import pl.edu.mimuw.cloudatlas.attributes.StringValue;
import pl.edu.mimuw.cloudatlas.attributes.Type;

public class RegexpExpr extends Expr {

	private Expr expr;
	private String patternSource;
	private Pattern pattern;
	
	public RegexpExpr(Expr expr, String patternSource) {
		assert expr != null;
		assert patternSource != null;
		
		this.expr = expr;
		this.patternSource = patternSource;
		// Make sure PatternSyntaxException looks nicely as parse error
		this.pattern = Pattern.compile(patternSource);
	}
	
	@Override
	public Result evaluate(Env env) throws EvaluationException {
		Result exprResult = expr.evaluate(env);
		if (!exprResult.getType().equals(SimpleType.STRING)) {
			throw new EvaluationException("Cannot execute regexp on type " + exprResult.getType());
		}
		
		Function1<StringValue, BooleanValue> func = new Function1<StringValue, BooleanValue>() {
			public Type<BooleanValue> getReturnType() {
				return SimpleType.BOOLEAN;
			}

			public BooleanValue evaluate(StringValue arg)
					throws EvaluationException {
				if (arg == null) {
					return null;
				} else {
					return new BooleanValue(pattern.matcher(arg.getString()).matches());
				}
			}
		};
		
		return Functions.evaluate(func, exprResult);
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
