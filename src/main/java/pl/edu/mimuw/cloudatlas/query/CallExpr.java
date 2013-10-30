package pl.edu.mimuw.cloudatlas.query;

import java.util.List;

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
}
