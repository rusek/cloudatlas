package pl.edu.mimuw.cloudatlas.query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Timestamp;

import pl.edu.mimuw.cloudatlas.attributes.BooleanValue;
import pl.edu.mimuw.cloudatlas.attributes.SimpleType;
import pl.edu.mimuw.cloudatlas.attributes.StringValue;
import pl.edu.mimuw.cloudatlas.attributes.BooleanValue;
import pl.edu.mimuw.cloudatlas.attributes.IntegerValue;
import pl.edu.mimuw.cloudatlas.attributes.DoubleValue;
import pl.edu.mimuw.cloudatlas.attributes.DurationValue;
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
	
	is_null {
		public Function1<? extends Value, ? extends Value> getFuncByArgType(
				Type<? extends Value> type) {
			return new Function1<Value, BooleanValue>() {

				public Type<BooleanValue> getReturnType() {
					return SimpleType.BOOLEAN;
				}

				public BooleanValue evaluate(Value arg)
						throws EvaluationException {
					return new BooleanValue(arg == null);
				}
				
			};
		}
	},
	to_integer {
		public Function1<? extends Value, ? extends Value> getFuncByArgType(
				Type<? extends Value> type) {
					if(type.equals(SimpleType.DOUBLE)) {
						return new Function1<DoubleValue, IntegerValue>() {

							public Type<IntegerValue> getReturnType() {
								return SimpleType.INTEGER;
							}

							public IntegerValue evaluate(DoubleValue arg)
									throws EvaluationException {
								return new IntegerValue(Math.round(arg.getDouble()));
							}
							
						};
					}
					
					else if(type.equals(SimpleType.DURATION)) {
						return new Function1<DurationValue, IntegerValue>() {

							public Type<IntegerValue> getReturnType() {
								return SimpleType.INTEGER;
							}

							public IntegerValue evaluate(DurationValue arg)
									throws EvaluationException {
								return new IntegerValue(arg.getTotalMiliseconds());
							}
							
						};
					}
					
					else if(type.equals(SimpleType.STRING)) {
						return new Function1<StringValue, IntegerValue>() {

							public Type<IntegerValue> getReturnType() {
								return SimpleType.INTEGER;
							}

							public IntegerValue evaluate(StringValue arg)
									throws EvaluationException {
								return new IntegerValue(Integer.parseInt(arg.getString()));
							}
							
						};
					}
					
					else {
						return null;
					}
			
		}
	},
	
	to_double {
		public Function1<? extends Value, ? extends Value> getFuncByArgType(
				Type<? extends Value> type) {
			
			if(type.equals(SimpleType.INTEGER)) {
				return new Function1<IntegerValue, DoubleValue>() {

					public Type<DoubleValue> getReturnType() {
						return SimpleType.DOUBLE;
					}

					public DoubleValue evaluate(IntegerValue arg)
							throws EvaluationException {
						return new DoubleValue(new Double(arg.getInteger()));
						
					}
					
				};
			}
			
			else if(type.equals(SimpleType.STRING)) {
				return new Function1<StringValue, DoubleValue>() {

					public Type<DoubleValue> getReturnType() {
						return SimpleType.DOUBLE;
					}

					public DoubleValue evaluate(StringValue arg)
							throws EvaluationException {
						return new DoubleValue(Double.parseDouble(arg.getString()));
					}
					
				};
			}
			
			else {
				return null;
			}
		}
		
		
	},
	
	to_boolean {
		public Function1<? extends Value, ? extends Value> getFuncByArgType(
				Type<? extends Value> type) {
			
			if(type.equals(SimpleType.STRING)) {
				return new Function1<StringValue, BooleanValue>() {

					public Type<BooleanValue> getReturnType() {
						return SimpleType.BOOLEAN;
					}

					public BooleanValue evaluate(StringValue arg)
							throws EvaluationException {
						return new BooleanValue(Boolean.parseBoolean(arg.getString()));
					}
					
				};
			}
			
			else {
				return null;
			}
		};
	},
	
	to_duration {
		public Function1<? extends Value, ? extends Value> getFuncByArgType(
				Type<? extends Value> type) {
			if(type.equals(SimpleType.INTEGER)) {
				return new Function1<IntegerValue, DurationValue>() {

					public Type<DurationValue> getReturnType() {
						return SimpleType.DURATION;
					}

					public DurationValue evaluate(IntegerValue arg)
							throws EvaluationException {
						return new DurationValue(arg.getInteger());
					}
				};
			
			}
			
			else if(type.equals(SimpleType.STRING)) {
				return new Function1<StringValue, DurationValue>() {

					public Type<DurationValue> getReturnType() {
						return SimpleType.DURATION;
					}

					public DurationValue evaluate(StringValue arg)
							throws EvaluationException {
						String s = arg.getString();
						int start = 1;
						int mul = 1;
						if(s.charAt(0) == '-')
							mul = -1;
						else if(s.charAt(0) != '+')
							start = 0;
						String[] tmp = s.substring(start).split(" ");
						int days = Integer.parseInt(tmp[0]);
						String[] time = tmp[1].split(":");
						int hours = Integer.parseInt(time[0]);
						int mins = Integer.parseInt(time[1]);
						String[] sec = time[2].split(".");
						int secs = Integer.parseInt(sec[0]);
						int mils = Integer.parseInt(sec[1]);
						return new DurationValue(mul * (((((days * 24) + hours) * 60 + mins) * 60 + secs) * 1000 + mils));	
					}
					
				};
			}
			
			else {
				return null;
			}
		
		};	
	},
	
	to_time {
		public Function1<? extends Value, ? extends Value> getFuncByArgType(
				Type<? extends Value> type) {
			if(type.equals(SimpleType.STRING)) {
				return new Function1<StringValue, TimeValue>() {

					@Override
					public Type<TimeValue> getReturnType() {
						return SimpleType.TIME;
					}

					@Override
					public TimeValue evaluate(StringValue arg)
							throws EvaluationException {
	
							try {
								return new TimeValue(TimeValue.createDateFormat().parse(arg.getString()).getTime());
							} catch (Exception e) {
								return null;
							}
					}
				};
			}
			else {
				return null;
			}
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
