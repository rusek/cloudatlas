package pl.edu.mimuw.cloudatlas.query;

import pl.edu.mimuw.cloudatlas.attributes.BooleanValue;
import pl.edu.mimuw.cloudatlas.attributes.DoubleValue;
import pl.edu.mimuw.cloudatlas.attributes.DurationValue;
import pl.edu.mimuw.cloudatlas.attributes.TimeValue;
import pl.edu.mimuw.cloudatlas.attributes.IntegerValue;
import pl.edu.mimuw.cloudatlas.attributes.StringValue;
import pl.edu.mimuw.cloudatlas.attributes.SimpleType;
import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;

public enum BinOp {
	// I + I = I; D + D = D; Dur + Dur = Dur; T + Dur = T; Dur + T = T; S + S = S
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
			}
			
			else if (type1.equals(SimpleType.DOUBLE) && type2.equals(SimpleType.DOUBLE)) {
				return new Function2<DoubleValue, DoubleValue, DoubleValue>() {
					
					public Type<DoubleValue> getReturnType() {
						return SimpleType.DOUBLE;
					}
					
					public DoubleValue evaluate(DoubleValue arg1,
							DoubleValue arg2) throws EvaluationException {
						if(arg1 == null || arg2 == null) {
							return null;
						}
						else {
							return new DoubleValue(arg1.getDouble() + arg2.getDouble());
						}
							
					}
				};
			}
			
			else if (type1.equals(SimpleType.DURATION) && type2.equals(SimpleType.DURATION)) {
				return new Function2<DurationValue, DurationValue, DurationValue>() {
					
					public Type<DurationValue> getReturnType() {
						return SimpleType.DURATION;
					}
					
					public DurationValue evaluate(DurationValue arg1,
							DurationValue arg2) throws EvaluationException {
						if(arg1 == null || arg2 == null) {
							return null;
						}
						else {
							return new DurationValue(arg1.getTotalMiliseconds() + arg2.getTotalMiliseconds());
						}
					}
							
					
				};
			}
			
			else if (type1.equals(SimpleType.DURATION) && type2.equals(SimpleType.TIME)) {
				return new Function2<DurationValue, TimeValue, TimeValue>() {
					
					public Type<TimeValue> getReturnType() {
						return SimpleType.TIME;
					}
					
					public TimeValue evaluate(DurationValue arg1,
							TimeValue arg2) throws EvaluationException {
						if(arg1 == null || arg2 == null) {
							return null;
						}
						else {
							return new TimeValue(arg1.getTotalMiliseconds() + arg2.getTimestamp());
						}
							
					}
				};
			}
			
			else if (type1.equals(SimpleType.TIME) && type2.equals(SimpleType.DURATION)) {
				return new Function2<TimeValue, DurationValue, TimeValue>() {
					
					public Type<TimeValue> getReturnType() {
						return SimpleType.TIME;
					}
					
					public TimeValue evaluate(TimeValue arg1,
							DurationValue arg2) throws EvaluationException {
						if(arg1 == null || arg2 == null) {
							return null;
						}
						else {
							return new TimeValue(arg2.getTotalMiliseconds() + arg1.getTimestamp());
						}
							
					}
				};
			}
			
			else if (type1.equals(SimpleType.STRING) && type2.equals(SimpleType.STRING)) {
				return new Function2<StringValue, StringValue, StringValue>() {

					public Type<StringValue> getReturnType() {
						return SimpleType.STRING;
					}

					public StringValue evaluate(StringValue arg1,
							StringValue arg2) throws EvaluationException {
						if (arg1 == null || arg2 == null) {
							return null;
						} else {
							return new StringValue(arg1.getString() + arg2.getString());
						}
					}
					
				};
			}
			
			else {
				return null;
			}
			
		}
		
	},
	// I - I = I; D - D = D; Dur - Dur = Dur; T - Dur = T; T - T = Dur
	SUB {
		@Override
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
							return new IntegerValue(arg1.getInteger() - arg2.getInteger());
						}
					}
					
				};
			}
			
			else if (type1.equals(SimpleType.DOUBLE) && type2.equals(SimpleType.DOUBLE)) {
				return new Function2<DoubleValue, DoubleValue, DoubleValue>() {
					
					public Type<DoubleValue> getReturnType() {
						return SimpleType.DOUBLE;
					}
					
					public DoubleValue evaluate(DoubleValue arg1,
							DoubleValue arg2) throws EvaluationException {
						if(arg1 == null || arg2 == null) {
							return null;
						}
						else {
							return new DoubleValue(arg1.getDouble() - arg2.getDouble());
						}
							
					}
				};
			}
			
			else if (type1.equals(SimpleType.DURATION) && type2.equals(SimpleType.DURATION)) {
				return new Function2<DurationValue, DurationValue, DurationValue>() {
					
					public Type<DurationValue> getReturnType() {
						return SimpleType.DURATION;
					}
					
					public DurationValue evaluate(DurationValue arg1,
							DurationValue arg2) throws EvaluationException {
						if(arg1 == null || arg2 == null) {
							return null;
						}
						else {
							return new DurationValue(arg1.getTotalMiliseconds() - arg2.getTotalMiliseconds());
						}
					}
							
					
				};
			}
			
			
			else if (type1.equals(SimpleType.TIME) && type2.equals(SimpleType.DURATION)) {
				return new Function2<TimeValue, DurationValue, TimeValue>() {
					
					public Type<TimeValue> getReturnType() {
						return SimpleType.TIME;
					}
					
					public TimeValue evaluate(TimeValue arg1,
							DurationValue arg2) throws EvaluationException {
						if(arg1 == null || arg2 == null) {
							return null;
						}
						else {
							return new TimeValue(arg1.getTimestamp() - arg2.getTotalMiliseconds());
						}
							
					}
				};
			}
			
			else if (type1.equals(SimpleType.TIME) && type2.equals(SimpleType.TIME)) {
				return new Function2<TimeValue, TimeValue, DurationValue>() {
					
					public Type<DurationValue> getReturnType() {
						return SimpleType.DURATION;
					}
					
					public DurationValue evaluate(TimeValue arg1,
							TimeValue arg2) throws EvaluationException {
						if(arg1 == null || arg2 == null) {
							return null;
						}
						else {
							return new DurationValue(arg1.getTimestamp() - arg2.getTimestamp());
						}
							
					}
				};
			}
			
			else {
				return null;
			}
		}
	},
	//I * I = I; D * D = D; I * Dur = Dur; Dur * I = Dur;
	MUL {
		@Override
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
							return new IntegerValue(arg1.getInteger() * arg2.getInteger());
						}
					}
					
				};
			}
			
			else if (type1.equals(SimpleType.DOUBLE) && type2.equals(SimpleType.DOUBLE)) {
				return new Function2<DoubleValue, DoubleValue, DoubleValue>() {
					
					public Type<DoubleValue> getReturnType() {
						return SimpleType.DOUBLE;
					}
					
					public DoubleValue evaluate(DoubleValue arg1,
							DoubleValue arg2) throws EvaluationException {
						if(arg1 == null || arg2 == null) {
							return null;
						}
						else {
							return new DoubleValue(arg1.getDouble() * arg2.getDouble());
						}
							
					}
				};
			}
			
			else if (type1.equals(SimpleType.INTEGER) && type2.equals(SimpleType.DURATION)) {
				return new Function2<IntegerValue, DurationValue, DurationValue>() {

					public Type<DurationValue> getReturnType() {
						return SimpleType.DURATION;
					}

					public DurationValue evaluate(IntegerValue arg1,
							DurationValue arg2) throws EvaluationException {
						if (arg1 == null || arg2 == null) {
							return null;
						} else {
							return new DurationValue(arg1.getInteger() * arg2.getTotalMiliseconds());
						}
					}
					
				};
			}
			
			else if (type2.equals(SimpleType.INTEGER) && type1.equals(SimpleType.DURATION)) {
				return new Function2<DurationValue, IntegerValue, DurationValue>() {

					public Type<DurationValue> getReturnType() {
						return SimpleType.DURATION;
					}

					public DurationValue evaluate(DurationValue arg1,
							IntegerValue arg2) throws EvaluationException {
						if (arg1 == null || arg2 == null) {
							return null;
						} else {
							return new DurationValue(arg2.getInteger() * arg1.getTotalMiliseconds());
						}
					}
					
				};
			}
			
			else {
				return null;
			}
		}
	},
	// I / I = D; D / D = D; Dur / Dur = D; Dur / I = Dur;
	DIV {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			if (type1.equals(SimpleType.INTEGER) && type2.equals(SimpleType.INTEGER)) {
				// The result is double - for consistency with avg(...):
				//     avg(intAttr) = sum(intAttr) / count(intAttr)
				return new Function2<IntegerValue, IntegerValue, DoubleValue>() {

					public Type<DoubleValue> getReturnType() {
						return SimpleType.DOUBLE;
					}

					public DoubleValue evaluate(IntegerValue arg1,
							IntegerValue arg2) throws EvaluationException {
						if (arg1 == null || arg2 == null) {
							return null;
						} else if (arg2.getInteger() == 0) {
							throw new EvaluationException("Cannot divide by zero");
						} else {
							return new DoubleValue(((double) arg1.getInteger()) / ((double) arg2.getInteger()));
						}
					}
					
				};
			}
			
			else if (type1.equals(SimpleType.DOUBLE) && type2.equals(SimpleType.DOUBLE)) {
				return new Function2<DoubleValue, DoubleValue, DoubleValue>() {
					
					public Type<DoubleValue> getReturnType() {
						return SimpleType.DOUBLE;
					}
					
					public DoubleValue evaluate(DoubleValue arg1,
							DoubleValue arg2) throws EvaluationException {
						if(arg1 == null || arg2 == null) {
							return null;
						} else if (arg2.getDouble() == 0.0) {
							throw new EvaluationException("Cannot divide by zero");
						}
						else {
							return new DoubleValue(arg1.getDouble() / arg2.getDouble());
						}	
					}
				};
			}
			
			else if(type1.equals(SimpleType.DURATION) && type2.equals(SimpleType.DURATION)) {
				return new Function2<DurationValue, DurationValue, DoubleValue>() {

					public Type<DoubleValue> getReturnType() {
						return SimpleType.DOUBLE;
					}

					public DoubleValue evaluate(DurationValue arg1,
							DurationValue arg2) throws EvaluationException {
						if(arg1 == null || arg2 == null) {
							return null;
						} else if (arg2.getTotalMiliseconds() == 0) {
							throw new EvaluationException("Cannot divide by zero");
						}
						else {
							return new DoubleValue(((double) arg1.getTotalMiliseconds()) /
									((double) arg2.getTotalMiliseconds()));
						}
					}
					
				};
			}
			
			else if (type1.equals(SimpleType.DURATION) && type2.equals(SimpleType.INTEGER)) {
				return new Function2<DurationValue, IntegerValue, DurationValue>() {

					public Type<DurationValue> getReturnType() {
						return SimpleType.DURATION;
					}

					public DurationValue evaluate(DurationValue arg1,
							IntegerValue arg2) throws EvaluationException {
						if (arg1 == null || arg2 == null) {
							return null;
						} else if (arg2.getInteger() == 0) {
							throw new EvaluationException("Cannot divide by zero");
						} else {
							return new DurationValue(arg1.getTotalMiliseconds() / arg2.getInteger());
						}
					}
					
				};
			}
			
			else {
				return null;
			}
		}
	},
	//I % I = I; D % D = D; Dur % Dur = Dur;
	MOD {
		@Override
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
						} else if (arg2.getInteger() == 0) {
							throw new EvaluationException("Modulo by zero");
						} else {
							return new IntegerValue(arg1.getInteger() % arg2.getInteger());
						}
					}
					
				};
			}
			
			else if(type1.equals(SimpleType.DOUBLE) && type2.equals(SimpleType.DOUBLE)) {
				return new Function2<DoubleValue, DoubleValue, DoubleValue>() {

					public Type<DoubleValue> getReturnType() {
						return SimpleType.DOUBLE;
					}

					public DoubleValue evaluate(DoubleValue arg1,
							DoubleValue arg2) throws EvaluationException {
						if(arg1 == null || arg2 == null) {
							return null;
						} else if (arg2.getDouble() == 0.0) {
							throw new EvaluationException("Modulo by zero");
						}
						else {
							return new DoubleValue(arg1.getDouble() % arg2.getDouble());
						}
					}
					
				};
			}
			
			else if(type1.equals(SimpleType.DURATION) && type2.equals(SimpleType.DURATION)) {
				return new Function2<DurationValue, DurationValue, DurationValue>() {

					public Type<DurationValue> getReturnType() {
						return SimpleType.DURATION;
					}

					public DurationValue evaluate(DurationValue arg1,
							DurationValue arg2) throws EvaluationException {
						if(arg1 == null || arg2 == null) {
							return null;
						} else if (arg2.getTotalMiliseconds() == 0) {
							throw new EvaluationException("Modulo by zero");
						}
						else {
							return new DurationValue(arg1.getTotalMiliseconds() % arg2.getTotalMiliseconds());
						}
					}
					
				};
			}
			
			else {
				return null;
			}
		}
	},
	AND {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			if (type1.equals(SimpleType.BOOLEAN) && type2.equals(SimpleType.BOOLEAN)) {
				return new Function2<BooleanValue, BooleanValue, BooleanValue>() {

					public Type<BooleanValue> getReturnType() {
						return SimpleType.BOOLEAN;
					}

					public BooleanValue evaluate(BooleanValue arg1,
							BooleanValue arg2) throws EvaluationException {
						if (arg1 == null || arg2 == null) {
							return null;
						} else {
							return new BooleanValue(arg1.getBoolean() && arg2.getBoolean());
						}
					}
					
				};
			} else {
				return null;
			}
		}
	},
	OR {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			if (type1.equals(SimpleType.BOOLEAN) && type2.equals(SimpleType.BOOLEAN)) {
				return new Function2<BooleanValue, BooleanValue, BooleanValue>() {

					public Type<BooleanValue> getReturnType() {
						return SimpleType.BOOLEAN;
					}

					public BooleanValue evaluate(BooleanValue arg1,
							BooleanValue arg2) throws EvaluationException {
						if (arg1 == null || arg2 == null) {
							return null;
						} else {
							return new BooleanValue(arg1.getBoolean() || arg2.getBoolean());
						}
					}
					
				};
			} else {
				return null;
			}
		}
	},
	LT {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			if (type1.equals(type2) && type1.isComparable()) {
				return new Function2<Value, Value, BooleanValue>() {

					public Type<BooleanValue> getReturnType() {
						return SimpleType.BOOLEAN;
					}

					@SuppressWarnings({ "unchecked", "rawtypes" })
					public BooleanValue evaluate(Value arg1, Value arg2)
							throws EvaluationException {
						if (arg1 == null || arg2 == null) {
							return null;
						} else {
							return new BooleanValue(((Comparable) arg1).compareTo(arg2) < 0);
						}
					}
					
				};
			} else {
				return null;
			}
		}
	},
	LE {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			if (type1.equals(type2) && type1.isComparable()) {
				return new Function2<Value, Value, BooleanValue>() {

					public Type<BooleanValue> getReturnType() {
						return SimpleType.BOOLEAN;
					}

					@SuppressWarnings({ "unchecked", "rawtypes" })
					public BooleanValue evaluate(Value arg1, Value arg2)
							throws EvaluationException {
						if (arg1 == null || arg2 == null) {
							return null;
						} else {
							return new BooleanValue(((Comparable) arg1).compareTo(arg2) <= 0);
						}
					}
					
				};
			} else {
				return null;
			}
		}
	}, 
	GT {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			if (type1.equals(type2) && type1.isComparable()) {
				return new Function2<Value, Value, BooleanValue>() {

					public Type<BooleanValue> getReturnType() {
						return SimpleType.BOOLEAN;
					}

					@SuppressWarnings({ "unchecked", "rawtypes" })
					public BooleanValue evaluate(Value arg1, Value arg2)
							throws EvaluationException {
						if (arg1 == null || arg2 == null) {
							return null;
						} else {
							return new BooleanValue(((Comparable) arg1).compareTo(arg2) > 0);
						}
					}
					
				};
			} else {
				return null;
			}
		}
	}, 
	GE {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			if (type1.equals(type2) && type1.isComparable()) {
				return new Function2<Value, Value, BooleanValue>() {

					public Type<BooleanValue> getReturnType() {
						return SimpleType.BOOLEAN;
					}

					@SuppressWarnings({ "unchecked", "rawtypes" })
					public BooleanValue evaluate(Value arg1, Value arg2)
							throws EvaluationException {
						if (arg1 == null || arg2 == null) {
							return null;
						} else {
							return new BooleanValue(((Comparable) arg1).compareTo(arg2) >= 0);
						}
					}
					
				};
			} else {
				return null;
			}
		}
	}, 
	EQ {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			if (type1.equals(type2)) {
				return new Function2<Value, Value, BooleanValue>() {

					public Type<BooleanValue> getReturnType() {
						return SimpleType.BOOLEAN;
					}

					public BooleanValue evaluate(Value arg1, Value arg2)
							throws EvaluationException {
						if (arg1 == null || arg2 == null) {
							return null;
						} else {
							return new BooleanValue(arg1.equals(arg2));
						}
					}
					
				};
			} else {
				return null;
			}
		}
	},
	NE {
		@Override
		public Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
				Type<? extends Value> type1, Type<? extends Value> type2) {
			if (type1.equals(type2)) {
				return new Function2<Value, Value, BooleanValue>() {

					public Type<BooleanValue> getReturnType() {
						return SimpleType.BOOLEAN;
					}

					public BooleanValue evaluate(Value arg1, Value arg2)
							throws EvaluationException {
						if (arg1 == null || arg2 == null) {
							return null;
						} else {
							return new BooleanValue(!arg1.equals(arg2));
						}
					}
					
				};
			} else {
				return null;
			}
		}
	};
	
	public abstract Function2<? extends Value, ? extends Value, ? extends Value> getFuncForTypes(
			Type<? extends Value> type1, Type<? extends Value> type2);
}
