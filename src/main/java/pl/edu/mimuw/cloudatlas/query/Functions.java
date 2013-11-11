package pl.edu.mimuw.cloudatlas.query;

import pl.edu.mimuw.cloudatlas.attributes.Value;

public class Functions {

	private Functions() {}
	
	public static Result evaluate(Function2<? extends Value, ? extends Value, ? extends Value> func,
			Result leftArg, final Result rightArg) throws EvaluationException {
		@SuppressWarnings("unchecked")
		final Function2<Value, Value, Value> funcCasted = (Function2<Value, Value, Value>) func;
		
		return leftArg.accept(new ResultVisitor<Result, EvaluationException>() {

			public Result visit(final OneResult leftResult) throws EvaluationException {
				return rightArg.accept(new ResultVisitor<Result, EvaluationException>() {

					public Result visit(OneResult rightResult)
							throws EvaluationException {
						return OneResult.createFromFunc(funcCasted, leftResult.getValue(), rightResult.getValue());
					}

					public Result visit(ListResult rightResult)
							throws EvaluationException {
						return ListResult.createFromFunc(funcCasted, leftResult.getValue(), rightResult.getValues());
					}

					public Result visit(ColumnResult rightResult)
							throws EvaluationException {
						return ColumnResult.createFromFunc(funcCasted, leftResult.getValue(), rightResult.getValues());
					}
					
				});
			}

			public Result visit(final ListResult leftResult) throws EvaluationException {
				return rightArg.accept(new ResultVisitor<Result, EvaluationException>() {

					public Result visit(OneResult rightResult)
							throws EvaluationException {
						return ListResult.createFromFunc(funcCasted, leftResult.getValues(), rightResult.getValue());
					}

					public Result visit(ListResult rightResult)
							throws EvaluationException {
						throw new EvaluationException("Cannot apply operation to ListResult and ListResult");
					}

					public Result visit(ColumnResult rightResult)
							throws EvaluationException {
						throw new EvaluationException("Cannot apply operation to ListResult and ColumnResult");
					}
					
				});
			}

			public Result visit(final ColumnResult leftResult) throws EvaluationException {
				return rightArg.accept(new ResultVisitor<Result, EvaluationException>() {

					public Result visit(OneResult rightResult)
							throws EvaluationException {
						return ColumnResult.createFromFunc(funcCasted, leftResult.getValues(), rightResult.getValue());
					}

					public Result visit(ListResult rightResult)
							throws EvaluationException {
						throw new EvaluationException("Cannot apply operation to ColumnResult and ListResult");
					}

					public Result visit(ColumnResult rightResult)
							throws EvaluationException {
						return ColumnResult.createFromFunc(funcCasted, leftResult.getValues(), rightResult.getValues());
					}
					
				});
			}
			
		});
	}
	
	public static Result evaluate(Function1<? extends Value, ? extends Value> func,
			Result arg) throws EvaluationException {

		@SuppressWarnings("unchecked")		
		final Function1<Value, Value> funcCasted = (Function1<Value, Value>) func;

		return arg.accept(new ResultVisitor<Result, EvaluationException>() {

			public Result visit(OneResult result) throws EvaluationException {
				return OneResult.createFromFunc(funcCasted, result.getValue());
			}

			public Result visit(ListResult result) throws EvaluationException {
				return ListResult.createFromFunc(funcCasted, result.getValues());
			}

			public Result visit(ColumnResult result) throws EvaluationException {
				return ListResult.createFromFunc(funcCasted, result.getValues());
			}
		});
	}
}
