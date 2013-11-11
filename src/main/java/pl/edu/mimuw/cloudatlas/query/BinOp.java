package pl.edu.mimuw.cloudatlas.query;

import pl.edu.mimuw.cloudatlas.attributes.IntegerValue;
import pl.edu.mimuw.cloudatlas.attributes.SimpleType;
import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;

public enum BinOp {
	ADD {
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			if (type1.equals(SimpleType.INTEGER) && type2.equals(SimpleType.INTEGER)) {
				return new Function2<IntegerValue, IntegerValue, IntegerValue>() {

					public Type<IntegerValue> getReturnType() {
						return SimpleType.INTEGER;
					}

					public IntegerValue evaluate(IntegerValue arg1,
							IntegerValue arg2) throws EvaluationException {
						if (arg1 == null || arg2 == null) {
							return null;
						} else {
							return new IntegerValue(arg1.getInteger() + arg2.getInteger());
						}
					}
					
				};
			} else {
				return null;
			}
		}
	},
	SUB {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	MUL {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	DIV {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			// TODO Auto-generated method stub
			return null;
		}
	}, 
	MOD {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	AND {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	OR {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	LT {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	LE {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			// TODO Auto-generated method stub
			return null;
		}
	}, 
	GT {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			// TODO Auto-generated method stub
			return null;
		}
	}, 
	GE {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			// TODO Auto-generated method stub
			return null;
		}
	}, 
	EQ {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	NE {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
	public abstract Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
			Type<? extends Value> type1, Type<? extends Value> type2);
}
