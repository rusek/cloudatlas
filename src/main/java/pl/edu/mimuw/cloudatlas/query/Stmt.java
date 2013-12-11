package pl.edu.mimuw.cloudatlas.query;

import java.util.List;

public abstract class Stmt {

	public abstract int hashCode();
	public abstract boolean equals(Object obj);
	
	public abstract List<SelectionResult> executeSelection(Env env) throws EvaluationException;
}
