package pl.edu.mimuw.cloudatlas.query;

import java.util.Date;
import java.util.regex.Matcher;

import pl.edu.mimuw.cloudatlas.attributes.BooleanValue;
import pl.edu.mimuw.cloudatlas.attributes.CollectionType;
import pl.edu.mimuw.cloudatlas.attributes.CollectionValue;
import pl.edu.mimuw.cloudatlas.attributes.ListType;
import pl.edu.mimuw.cloudatlas.attributes.ListValue;
import pl.edu.mimuw.cloudatlas.attributes.SetType;
import pl.edu.mimuw.cloudatlas.attributes.SetValue;
import pl.edu.mimuw.cloudatlas.attributes.SimpleType;
import pl.edu.mimuw.cloudatlas.attributes.SimpleValue;
import pl.edu.mimuw.cloudatlas.attributes.StringValue;
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
								if (arg == null) {
									return null;
								}
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
								if (arg == null) {
									return null;
								}
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
								if (arg == null) {
									return null;
								}
								try {
									return new IntegerValue(Integer.parseInt(arg.getString()));
								} catch (NumberFormatException ex) {
									throw new EvaluationException("Cannot convert string to integer: " +
											arg.getString());
								}
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
						if (arg == null) {
							return null;
						}
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
						if (arg == null) {
							return null;
						}
						try {
							return new DoubleValue(Double.parseDouble(arg.getString()));
						} catch (NumberFormatException ex) {
							throw new EvaluationException("Cannot convert string to double: " + arg.getString());
						}
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
						if (arg == null) {
							return null;
						} else if (arg.getString().equals("true")) {
							return new BooleanValue(true);
						} else if (arg.getString().equals("false")) {
							return new BooleanValue(false);
						} else {
							throw new EvaluationException("Cannot convert string to boolean: " + arg.getString());
						}
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
						if (arg == null) {
							return null;
						}
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
						if (arg == null) {
							return null;
						}
						Matcher match = DurationValue.PATTERN.matcher(arg.getString());
						if (!match.matches()) {
							throw new EvaluationException("Cannot convert string to duration: " + arg.getString());
						}
						
						long sign = match.group(1).equals("+") ? 1 : -1;
						long days = Integer.parseInt(match.group(2));
						long hours = Integer.parseInt(match.group(3));
						long mins = Integer.parseInt(match.group(4));
						long secs = Integer.parseInt(match.group(5));
						long mils = Integer.parseInt(match.group(6));
						
						if (hours > 23 || mins > 59 || secs > 59) {
							throw new EvaluationException("Cannot convert string to duration: " + arg.getString());
						}
						
						return new DurationValue(sign * (((((days * 24) + hours) * 60 + mins) * 60 + secs) * 1000 + mils));	
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

					public Type<TimeValue> getReturnType() {
						return SimpleType.TIME;
					}

					public TimeValue evaluate(StringValue arg)
							throws EvaluationException {
						if (arg == null) {
							return null;
						}
						try {
							return new TimeValue(TimeValue.createDateFormat().parse(arg.getString()).getTime());
						} catch (Exception e) {
							throw new EvaluationException("String " + arg.getString() + " couldn't be parsed as Time.");
						}
					}
				};
			}
			else {
				return null;
			}
		}
	},
	
	to_list {
		public Function1<? extends Value, ? extends Value> getFuncByArgType(
				Type<? extends Value> type) {
			if (type instanceof SetType) {
				@SuppressWarnings("unchecked")
				SetType<? extends SimpleValue> setType = (SetType<? extends SimpleValue>) type;
				return SetToList.withItemType(setType.getItemType());
			} else {
				return null;
			}
		}
	},
	
	to_set {
		public Function1<? extends Value, ? extends Value> getFuncByArgType(
				Type<? extends Value> type) {
			if (type instanceof ListType) {
				@SuppressWarnings("unchecked")
				ListType<? extends SimpleValue> listType = (ListType<? extends SimpleValue>) type;
				return ListToSet.withItemType(listType.getItemType());
			} else {
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
				}
			};
		}
	
	
	},
	
	epoch {
		private static final long EPOCH_TIME = 946681200000l;
		public Function0<? extends Value> getNoArgFunc() {
			return new Function0<TimeValue>() {

				public Type<TimeValue> getReturnType() {
					return SimpleType.TIME;
				}

				public TimeValue evaluate() throws EvaluationException {
					return new TimeValue(EPOCH_TIME);
				}
				
			};
		}
	},
	
	size {
		public Function1<? extends Value, ? extends Value> getFuncByArgType(
				Type<? extends Value> type) {
			if(type.equals(SimpleType.STRING)) {
				return new Function1<StringValue, IntegerValue>() {

					@Override
					public Type<IntegerValue> getReturnType() {
						return SimpleType.INTEGER;
					}

					@Override
					public IntegerValue evaluate(StringValue arg)
							throws EvaluationException {
						if (arg == null) {
							return null;
						}
						return new IntegerValue(arg.getString().length());
					}
					
				};
			}
			
			else if(type instanceof CollectionType) {
				return new Function1<CollectionValue<? extends SimpleValue>, IntegerValue>() {
	
					@Override
					public Type<IntegerValue> getReturnType() {
						return SimpleType.INTEGER;
					}
	
					@Override
					public IntegerValue evaluate(CollectionValue<? extends SimpleValue> arg)
							throws EvaluationException {
						if (arg == null) {
							return null;
						}
						return new IntegerValue(arg.size());
					}
					
				};
			}
			else {
				return null;
			}
		}
	},
	
	round {
		public Function1<? extends Value, ? extends Value> getFuncByArgType(
				Type<? extends Value> type) {
			if(type.equals(SimpleType.DOUBLE)) {
				return new Function1<DoubleValue, DoubleValue>() {

					@Override
					public Type<DoubleValue> getReturnType() {
						return SimpleType.DOUBLE;
					}

					@Override
					public DoubleValue evaluate(DoubleValue arg)
							throws EvaluationException {
						if (arg == null) {
							return null;
						}
						return new DoubleValue(Math.round(arg.getDouble()));
					}
				};
			}
			else {
				return null;
			}
		}
	},
	
	floor {
		public Function1<? extends Value, ? extends Value> getFuncByArgType(
				Type<? extends Value> type) {
			if(type.equals(SimpleType.DOUBLE)) {
				return new Function1<DoubleValue, DoubleValue>() {

					@Override
					public Type<DoubleValue> getReturnType() {
						return SimpleType.DOUBLE;
					}

					@Override
					public DoubleValue evaluate(DoubleValue arg)
							throws EvaluationException {
						if (arg == null) {
							return null;
						}
						return new DoubleValue(Math.floor(arg.getDouble()));
					}
				};
			}
			else {
				return null;
			}
		}
	},
	
	ceil {
		public Function1<? extends Value, ? extends Value> getFuncByArgType(
				Type<? extends Value> type) {
			if(type.equals(SimpleType.DOUBLE)) {
				return new Function1<DoubleValue, DoubleValue>() {

					@Override
					public Type<DoubleValue> getReturnType() {
						return SimpleType.DOUBLE;
					}

					@Override
					public DoubleValue evaluate(DoubleValue arg)
							throws EvaluationException {
						if (arg == null) {
							return null;
						}
						return new DoubleValue(Math.ceil(arg.getDouble()));
					}
				};
			}
			else {
				return null;
			}
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
	
	private static class ListToSet<V extends SimpleValue> implements Function1<ListValue<V>, SetValue<V>> {
		
		private final SimpleType<V> itemType;
		
		public ListToSet(SimpleType<V> itemType) {
			assert itemType != null;
			
			this.itemType = itemType;
		}
		
		public static <V extends SimpleValue> ListToSet<V> withItemType(SimpleType<V> itemType) {
			return new ListToSet<V>(itemType);
		}

		@Override
		public Type<SetValue<V>> getReturnType() {
			return SetType.of(itemType);
		}

		@Override
		public SetValue<V> evaluate(ListValue<V> arg)
				throws EvaluationException {
			if (arg == null) {
				return null;
			} else {
				SetValue<V> result = SetValue.of(itemType);
				result.getItems().addAll(arg.getItems());
				return result;
			}
		}
		
	};
	
	private static class SetToList<V extends SimpleValue> implements Function1<SetValue<V>, ListValue<V>> {
		
		private final SimpleType<V> itemType;
		
		public SetToList(SimpleType<V> itemType) {
			assert itemType != null;
			
			this.itemType = itemType;
		}
		
		public static <V extends SimpleValue> SetToList<V> withItemType(SimpleType<V> itemType) {
			return new SetToList<V>(itemType);
		}

		@Override
		public Type<ListValue<V>> getReturnType() {
			return ListType.of(itemType);
		}

		@Override
		public ListValue<V> evaluate(SetValue<V> arg)
				throws EvaluationException {
			if (arg == null) {
				return null;
			} else {
				ListValue<V> result = ListValue.of(itemType);
				result.getItems().addAll(arg.getItems());
				return result;
			}
		}
		
	};
}
