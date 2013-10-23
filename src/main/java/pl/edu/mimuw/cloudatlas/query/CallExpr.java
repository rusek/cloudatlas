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
}
