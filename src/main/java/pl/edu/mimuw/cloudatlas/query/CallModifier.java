package pl.edu.mimuw.cloudatlas.query;

import pl.edu.mimuw.cloudatlas.attributes.IntegerValue;
import pl.edu.mimuw.cloudatlas.attributes.SimpleType;

public enum CallModifier {
	count {
		public Result evaluate(Result arg) throws EvaluationException {
			return arg.accept(new ResultVisitor<Result, EvaluationException>() {

				public Result visit(OneResult result)
						throws EvaluationException {
					throw new EvaluationException("Function count is not applicable to OneResult");
				}

				public Result visit(ListResult result)
						throws EvaluationException {
					return new OneResult(SimpleType.INTEGER, new IntegerValue(result.getValues().size()));
				}

				public Result visit(ColumnResult result)
						throws EvaluationException {
					return new OneResult(SimpleType.INTEGER, new IntegerValue(result.getValues().size()));
				}
				
			});
		}
	};
	
	public Result evaluate(Result arg) throws EvaluationException {
		throw new EvaluationException("Function " + this.name() + " is not applicable to 1 argument");
	}
	
	public Result evaluate(Result arg1, Result arg2) throws EvaluationException {
		throw new EvaluationException("Function " + this.name() + " is not applicable to 2 arguments");
	}
	
	public static CallModifier getByName(String modifierName) {
		try {
			return CallModifier.valueOf(modifierName);
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
}
