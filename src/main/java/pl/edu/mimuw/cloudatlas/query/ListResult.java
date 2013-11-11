package pl.edu.mimuw.cloudatlas.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;

public class ListResult extends Result {

	private Type<? extends Value> type;
	private List<Value> values;

	public ListResult(Type<? extends Value> type, List<Value> values) {
		assert type != null;
		assert values != null;
		
		this.type = type;
		this.values = values;
	}
	
	public Type<? extends Value> getType() {
		return type;
	}

	public List<Value> getValues() {
		return values;
	}

	@Override
	public <R, E extends Exception> R accept(ResultVisitor<R, E> visitor) throws E {
		return visitor.visit(this);
	}
	
	public static <A1 extends Value, A2 extends Value, R extends Value> ListResult createFromFunc(
			Function2<A1, A2, R> func, List<A1> arg1, List<A2> arg2) throws EvaluationException {
		ListResult retVal = new ListResult(func.getReturnType(), new ArrayList<Value>());
		
		Iterator<A1> it1 = arg1.iterator();
		Iterator<A2> it2 = arg2.iterator();
		
		while (it1.hasNext() && it2.hasNext()) {
			retVal.values.add(func.evaluate(it1.next(), it2.next()));
		}
		
		return retVal;
	}
	
	public static <A1 extends Value, A2 extends Value, R extends Value> ListResult createFromFunc(
			Function2<A1, A2, R> func, A1 arg1, List<A2> arg2) throws EvaluationException {
		return createFromFunc(func, Collections.nCopies(arg2.size(), arg1), arg2);
	}
	
	public static <A1 extends Value, A2 extends Value, R extends Value> ListResult createFromFunc(
			Function2<A1, A2, R> func, List<A1> arg1, A2 arg2) throws EvaluationException {
		return createFromFunc(func, arg1, Collections.nCopies(arg1.size(), arg2));
	}

}
