package pl.edu.mimuw.cloudatlas.query;

import pl.edu.mimuw.cloudatlas.attributes.BooleanValue;
import pl.edu.mimuw.cloudatlas.attributes.DurationValue;
import pl.edu.mimuw.cloudatlas.attributes.IntegerValue;
import pl.edu.mimuw.cloudatlas.attributes.DoubleValue;
import pl.edu.mimuw.cloudatlas.attributes.SimpleType;
import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;

public enum UnOp {
	NEG {
		@Override
		public Function1<? extends Value, ? extends Value> getFuncByArgType(
				Type<? extends Value> type) {
			if (type.equals(SimpleType.INTEGER)) {
				return new Function1<IntegerValue, IntegerValue>() {

					public Type<IntegerValue> getReturnType() {
						return SimpleType.INTEGER;
					}

					public IntegerValue evaluate(IntegerValue arg)
							throws EvaluationException {
						return new IntegerValue(-arg.getInteger());
					}
					
				};
			}
			
			else if (type.equals(SimpleType.DOUBLE)) {
				return new Function1<DoubleValue, DoubleValue>() {

					public Type<DoubleValue> getReturnType() {
						return SimpleType.DOUBLE;
					}

					public DoubleValue evaluate(DoubleValue arg)
							throws EvaluationException {
						return new DoubleValue(-arg.getDouble());
					}
					
				};
			}
			
			else if (type.equals(SimpleType.DURATION)) {
				return new Function1<DurationValue, DurationValue>() {

					public Type<DurationValue> getReturnType() {
						return SimpleType.DURATION;
					}

					public DurationValue evaluate(DurationValue arg)
							throws EvaluationException {
						return new DurationValue(-arg.getTotalMiliseconds());
					}
					
				};
			}
			
			else {
				return null;
			}
		}
	},
	NOT {
		@Override
		public Function1<? extends Value, ? extends Value> getFuncByArgType(
				Type<? extends Value> type) {
			if (type.equals(SimpleType.BOOLEAN)) {
				return new Function1<BooleanValue, BooleanValue>() {

					public Type<BooleanValue> getReturnType() {
						return SimpleType.BOOLEAN;
					}

					public BooleanValue evaluate(BooleanValue arg)
							throws EvaluationException {
						if (arg == null) {
							return null;
						} else {
							return new BooleanValue(!arg.getBoolean());
						}
					}
					
				};
			} else {
				return null;
			}
		}
	};
	
	public abstract Function1<? extends Value, ? extends Value> getFuncByArgType(
			Type<? extends Value> type);
}
