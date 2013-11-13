package pl.edu.mimuw.cloudatlas.query;

import java.util.Date;

import pl.edu.mimuw.cloudatlas.attributes.SimpleType;
import pl.edu.mimuw.cloudatlas.attributes.StringValue;
import pl.edu.mimuw.cloudatlas.attributes.TimeValue;
import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;

public enum CallFunc {
	to_string {
		public Function1<? extends Value, ? extends Value> getFuncByArgType(
				Type<? extends Value> type) {
			return new Function1<Value, StringValue>() {

				public Type<StringValue> getReturnType() {
					return SimpleType.STRING;
				}

				public StringValue evaluate(Value arg)
						throws EvaluationException {
					if (arg == null) {
						return new StringValue("NULL");
					} else {
						return new StringValue(arg.toString());
					}
				}
				
			};
		}
	},
	now {
		public Function0<? extends Value> getNoArgFunc() {
			return new Function0<TimeValue>() {

				public Type<TimeValue> getReturnType() {
					return SimpleType.TIME;
				}

				public TimeValue evaluate() throws EvaluationException {
					return new TimeValue(new Date().getTime());
				}};
		}
	};

	public Function0<? extends Value> getNoArgFunc() {
		return null;
	}
	
	public Function1<? extends Value, ? extends Value> getFuncByArgType(
			Type<? extends Value> type) {
		return null;
	}
	
	public Function2<? extends Value, ? extends Value, ? extends Value> getFuncByArgTypes(
			Type<? extends Value> type1, Type<? extends Value> type2) {
		return null;
	}
	
	public static CallFunc getByName(String funcName) {
		try {
			return CallFunc.valueOf(funcName);
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
}
