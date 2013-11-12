package pl.edu.mimuw.cloudatlas.query;

import java.util.ArrayList;
import java.util.List;

import pl.edu.mimuw.cloudatlas.attributes.Value;

public class CallExpr extends Expr {

	private String funcName;
	private List<Expr> args;
	
	public CallExpr(String funcName, List<Expr> args) {
		assert funcName != null;
		assert args != null;
		
		this.funcName = funcName;
		this.args = args;
	}
	
	public String getFuncName() {
		return funcName;
	}
	
	public List<Expr> getArgs() {
		return args;
	}

	@Override
	public String toString() {
		return "CallExpr [funcName=" + funcName + ", args=" + args + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((args == null) ? 0 : args.hashCode());
		result = prime * result
				+ ((funcName == null) ? 0 : funcName.hashCode());
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
		CallExpr other = (CallExpr) obj;
		if (args == null) {
			if (other.args != null)
				return false;
		} else if (!args.equals(other.args))
			return false;
		if (funcName == null) {
			if (other.funcName != null)
				return false;
		} else if (!funcName.equals(other.funcName))
			return false;
		return true;
	}
	
	private Result evaluateAsModifier(List<Result> argResults) throws EvaluationException {
		CallModifier callModifier = CallModifier.getByName(funcName);
		if (callModifier == null) {
			return null;
		}
		if (argResults.size() == 1) {
			return callModifier.evaluate(argResults.get(0));
		} else if (argResults.size() == 2) {
			return callModifier.evaluate(argResults.get(0), argResults.get(1));
		} else {
			throw new EvaluationException("Function " + funcName + " is not applicable to " +
					argResults.size() + " argument(s).");
		}
	}
	
	private Result evaluateAsRegularFunc(List<Result> argResults) throws EvaluationException {
		CallFunc callFunc = CallFunc.getByName(funcName);
		if (callFunc == null) {
			return null;
		}
		if (argResults.size() == 0) {
			Function0<? extends Value> func = callFunc.getNoArgFunc();
			if (func == null) {
				throw new EvaluationException("Function " + funcName + " is not applicable to 0 arguments");
			}
			
			return new OneResult(func.getReturnType(), func.evaluate());
		} else if (argResults.size() == 1) {
			Result result = argResults.get(0);
			Function1<? extends Value, ? extends Value> func = callFunc.getFuncByArgType(result.getType());
			if (func == null) {
				throw new EvaluationException("Function " + funcName + " is not applicable to argument of type " +
						result.getType());
			}
			
			return Functions.evaluate(func, argResults.get(0));
		} else if (argResults.size() == 2) {
			Result result1 = argResults.get(0);
			Result result2 = argResults.get(1);
			Function2<? extends Value, ? extends Value, ? extends Value> func = 
					callFunc.getFuncByArgTypes(result1.getType(), result2.getType());
			if (func == null) {
				throw new EvaluationException("Function " + funcName + " is not applicable to arguments of types " +
						result1.getType() + " and " + result2.getType());
			}
			
			return Functions.evaluate(func, result1, result2);
		} else {
			throw new EvaluationException("Function " + funcName + " is not applicable to " +
					argResults.size() + " argument(s).");
		}
	}

	@Override
	public Result evaluate(Env env) throws EvaluationException {
		List<Result> argResults = new ArrayList<Result>();
		for (Expr argExpr : args) {
			argResults.add(argExpr.evaluate(env));
		}
		
		Result result = evaluateAsModifier(argResults);
		if (result != null) {
			return result;
		}
		
		result = evaluateAsRegularFunc(argResults);
		if (result != null) {
			return result;
		}
		
		throw new EvaluationException("Unrecognized function: " + funcName);
	}
}
