package pl.edu.mimuw.cloudatlas.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.edu.mimuw.cloudatlas.attributes.BooleanValue;
import pl.edu.mimuw.cloudatlas.attributes.DoubleValue;
import pl.edu.mimuw.cloudatlas.attributes.DurationValue;
import pl.edu.mimuw.cloudatlas.attributes.IntegerValue;
import pl.edu.mimuw.cloudatlas.attributes.ListType;
import pl.edu.mimuw.cloudatlas.attributes.ListValue;
import pl.edu.mimuw.cloudatlas.attributes.SetType;
import pl.edu.mimuw.cloudatlas.attributes.SetValue;
import pl.edu.mimuw.cloudatlas.attributes.SimpleType;
import pl.edu.mimuw.cloudatlas.attributes.SimpleValue;
import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;

public enum CallModifier {
	count {
		public Result evaluate(Result arg) throws EvaluationException {
			return arg.accept(new ResultVisitor<Result, EvaluationException>() {

				public Result visit(OneResult result)
						throws EvaluationException {
					throw new EvaluationException("Function count is not applicable to OneResult");
				}

				public Result visit(ListResult result)
						throws EvaluationException {
					return evaluate(result.getValues());
				}

				public Result visit(ColumnResult result)
						throws EvaluationException {
					return evaluate(result.getValues());
				}
				
				private Result evaluate(List<?> values) {
					int count = 0;
					for (Object value : values) {
						if (value != null) {
							count++;
						}
					}
					return new OneResult(SimpleType.INTEGER, new IntegerValue(count));
				}
				
			});
		}
	},
	unfold {
		public Result evaluate(Result arg) throws EvaluationException {
			return arg.accept(new ResultVisitor<Result, EvaluationException>() {

				public Result visit(OneResult result)
						throws EvaluationException {
					throw new EvaluationException("Function unfold is not applicable to OneResult");
				}

				public Result visit(ListResult result)
						throws EvaluationException {
					return evaluate(result.getType(), result.getValues());
				}

				public Result visit(ColumnResult result)
						throws EvaluationException {
					return evaluate(result.getType(), result.getValues());
				}
				
				@SuppressWarnings("unchecked")
				private Result evaluate(Type<? extends Value> type, List<Value> values) throws EvaluationException {
					List<Value> resultValues = new ArrayList<Value>();
					Type<? extends Value> resultType;
					
					if (type instanceof ListType) {
						resultType = ((ListType<? extends Value>) type).getItemType();
						for (Value value : values) {
							if (value != null) {
								resultValues.addAll(((ListValue<? extends Value>) value).getItems());
							}
						}
					} else if (type instanceof SetType) {
						resultType = ((SetType<? extends Value>) type).getItemType();
						for (Value value : values) {
							if (value != null) {
								resultValues.addAll(((SetValue<? extends Value>) value).getItems());
							}
						}
					} else {
						throw new EvaluationException("Cannot unfold type " + type);
					}
					
					return new ListResult(resultType, resultValues);
				}
				
			});
		}
	},
	distinct {
		public Result evaluate(Result arg) throws EvaluationException {
			return arg.accept(new ResultVisitor<Result, EvaluationException>() {

				public Result visit(OneResult result)
						throws EvaluationException {
					throw new EvaluationException("Function distinct is not applicable to OneResult");
				}

				public Result visit(ListResult result)
						throws EvaluationException {
					return evaluate(result.getType(), result.getValues());
				}

				public Result visit(ColumnResult result)
						throws EvaluationException {
					return evaluate(result.getType(), result.getValues());
				}
				
				private Result evaluate(Type<? extends Value> type, List<Value> values) throws EvaluationException {
					Set<Value> seenValues = new HashSet<Value>();
					List<Value> resultValues = new ArrayList<Value>();
					for (Value value : values) {
						if (value != null && !seenValues.contains(value)) {
							resultValues.add(value);
							seenValues.add(value);
						}
					}
					
					return new ListResult(type, resultValues);
				}
				
			});
		}
	},
	avg {
		public Result evaluate(Result arg) throws EvaluationException {
			return arg.accept(new ResultVisitor<Result, EvaluationException>() {

				public Result visit(OneResult result)
						throws EvaluationException {
					throw new EvaluationException("Function avg is not applicable to OneResult");
				}

				public Result visit(ListResult result)
						throws EvaluationException {
					return evaluate(result.getType(), result.getValues());
				}

				public Result visit(ColumnResult result)
						throws EvaluationException {
					return evaluate(result.getType(), result.getValues());
				}
				
				private Result evaluate(Type<? extends Value> type, List<Value> values) throws EvaluationException {
					double sum = 0.0;
					int count = 0;
					
					if (type.equals(SimpleType.INTEGER)) {
						for (Value value : values) {
							if (value != null) {
								sum += ((IntegerValue) value).getInteger();
								count++;
							}
						}
					} else if (type.equals(SimpleType.DOUBLE)) {
						for (Value value : values) {
							if (value != null) {
								sum += ((DoubleValue) value).getDouble();
								count++;
							}
						}
					} else {
						// TODO duration?
						throw new EvaluationException("Function avg cannot be applied to argument of type " + type);
					}
					
					return new OneResult(type, count == 0 ? null : new DoubleValue(sum / count));
				}
				
			});
		}
	},
	sum {
		public Result evaluate(Result arg) throws EvaluationException {
			return arg.accept(new ResultVisitor<Result, EvaluationException>() {

				public Result visit(OneResult result)
						throws EvaluationException {
					throw new EvaluationException("Function sum is not applicable to OneResult");
				}

				public Result visit(ListResult result)
						throws EvaluationException {
					return evaluate(result.getType(), result.getValues());
				}

				public Result visit(ColumnResult result)
						throws EvaluationException {
					return evaluate(result.getType(), result.getValues());
				}
				
				private Result evaluate(Type<? extends Value> type, List<Value> values) throws EvaluationException {
					if (type.equals(SimpleType.INTEGER)) {
						long sum = 0;
						boolean found = false;
						
						for (Value value : values) {
							if (value != null) {
								sum += ((IntegerValue) value).getInteger();
								found = true;
							}
						}
						
						return new OneResult(SimpleType.INTEGER, found ? new IntegerValue(sum) : null);
					} else if (type.equals(SimpleType.DOUBLE)) {
						double sum = 0.0;
						boolean found = false;
						
						for (Value value : values) {
							if (value != null) {
								sum += ((DoubleValue) value).getDouble();
								found = true;
							}
						}
						
						return new OneResult(SimpleType.DOUBLE, found ? new DoubleValue(sum) : null);
					}  else if (type.equals(SimpleType.DURATION)) {
						long sum = 0;
						boolean found = false;
						
						for (Value value : values) {
							if (value != null) {
								sum += ((DurationValue) value).getTotalMiliseconds();
								found = true;
							}
						}
						
						return new OneResult(SimpleType.DURATION, found ? new DurationValue(sum) : null);
					} else {
						throw new EvaluationException("Function sum cannot be applied to argument of type " + type);
					}
				}
				
			});
		}
	},
	land {
		public Result evaluate(Result arg) throws EvaluationException {
			return arg.accept(new ResultVisitor<Result, EvaluationException>() {

				public Result visit(OneResult result)
						throws EvaluationException {
					throw new EvaluationException("Function land is not applicable to OneResult");
				}

				public Result visit(ListResult result)
						throws EvaluationException {
					return evaluate(result.getType(), result.getValues());
				}

				public Result visit(ColumnResult result)
						throws EvaluationException {
					return evaluate(result.getType(), result.getValues());
				}
				
				private Result evaluate(Type<? extends Value> type, List<Value> values) throws EvaluationException {
					if (!type.equals(SimpleType.BOOLEAN)) {
						throw new EvaluationException("Function land cannot be applied to argument of type " + type);
					}
					
					boolean found = false;
					
					for (Value value : values) {
						if (value != null) {
							found = true;
							if (!((BooleanValue) value).getBoolean()) {
								return new OneResult(SimpleType.BOOLEAN, new BooleanValue(false));
							}
						}
					}
					
					return new OneResult(SimpleType.BOOLEAN, found ? new BooleanValue(true) : null);
				}
				
			});
		}
	},
	lor {
		public Result evaluate(Result arg) throws EvaluationException {
			return arg.accept(new ResultVisitor<Result, EvaluationException>() {

				public Result visit(OneResult result)
						throws EvaluationException {
					throw new EvaluationException("Function lor is not applicable to OneResult");
				}

				public Result visit(ListResult result)
						throws EvaluationException {
					return evaluate(result.getType(), result.getValues());
				}

				public Result visit(ColumnResult result)
						throws EvaluationException {
					return evaluate(result.getType(), result.getValues());
				}
				
				private Result evaluate(Type<? extends Value> type, List<Value> values) throws EvaluationException {
					if (!type.equals(SimpleType.BOOLEAN)) {
						throw new EvaluationException("Function lor cannot be applied to argument of type " + type);
					}
					
					boolean found = false;
					
					for (Value value : values) {
						if (value != null) {
							found = true;
							if (((BooleanValue) value).getBoolean()) {
								return new OneResult(SimpleType.BOOLEAN, new BooleanValue(true));
							}
						}
					}
					
					return new OneResult(SimpleType.BOOLEAN, found ? new BooleanValue(false) : null);
				}
				
			});
		}
	},
	first {
		public Result evaluate(Result arg1, Result arg2) throws EvaluationException {
			int quantity = extractQuantity(arg1);
			List<SimpleValue> values = extractSimpleValues(arg2);
			@SuppressWarnings("unchecked")
			ListValue<SimpleValue> returnedValue = ListValue.of((SimpleType<SimpleValue>) arg2.getType());
			returnedValue.addNotNulls(values);
			if (quantity < returnedValue.getItems().size()) {
				returnedValue.getItems().subList(quantity, returnedValue.getItems().size()).clear();
			}
			return new OneResult(returnedValue);
		}
	},
	last {
		public Result evaluate(Result arg1, Result arg2) throws EvaluationException {
			int quantity = extractQuantity(arg1);
			List<SimpleValue> values = extractSimpleValues(arg2);
			@SuppressWarnings("unchecked")
			ListValue<SimpleValue> returnedValue = ListValue.of((SimpleType<SimpleValue>) arg2.getType());
			returnedValue.addNotNulls(values);
			if (quantity < returnedValue.getItems().size()) {
				returnedValue.getItems().subList(0, returnedValue.getItems().size() - quantity).clear();
			}
			return new OneResult(returnedValue);
		}
	},
	random {
		public Result evaluate(Result arg1, Result arg2) throws EvaluationException {
			int quantity = extractQuantity(arg1);
			List<SimpleValue> values = extractSimpleValues(arg2);
			@SuppressWarnings("unchecked")
			ListValue<SimpleValue> returnedValue = ListValue.of((SimpleType<SimpleValue>) arg2.getType());
			returnedValue.addNotNulls(values);
			Collections.shuffle(returnedValue.getItems());
			if (quantity < returnedValue.getItems().size()) {
				returnedValue.getItems().subList(quantity, returnedValue.getItems().size()).clear();
			}
			return new OneResult(returnedValue);
		}
		
	},
	min {
		public Result evaluate(Result arg) throws EvaluationException {
			return arg.accept(new ResultVisitor<Result, EvaluationException>() {

				public Result visit(OneResult result)
						throws EvaluationException {
					throw new EvaluationException("Function min is not applicable to OneResult");
				}

				public Result visit(ListResult result)
						throws EvaluationException {
					return evaluate(result.getType(), result.getValues());
				}

				public Result visit(ColumnResult result)
						throws EvaluationException {
					return evaluate(result.getType(), result.getValues());
				}
				
				@SuppressWarnings({ "rawtypes", "unchecked" })
				private Result evaluate(Type<? extends Value> type, List<Value> values) throws EvaluationException {
					if (!type.isComparable()) {
						throw new EvaluationException("Function min cannot be applied to argument of type " + type);
					}
					
					Value best = null;
					for (Value value : values) {
						if (value != null) {
							if (best == null || ((Comparable) value).compareTo(best) < 0) {
								best = value;
							}
						}
					}
					
					return new OneResult(type, best);
				}
			});
		}
	},
	max {
		public Result evaluate(Result arg) throws EvaluationException {
			return arg.accept(new ResultVisitor<Result, EvaluationException>() {

				public Result visit(OneResult result)
						throws EvaluationException {
					throw new EvaluationException("Function max is not applicable to OneResult");
				}

				public Result visit(ListResult result)
						throws EvaluationException {
					return evaluate(result.getType(), result.getValues());
				}

				public Result visit(ColumnResult result)
						throws EvaluationException {
					return evaluate(result.getType(), result.getValues());
				}
				
				@SuppressWarnings({ "rawtypes", "unchecked" })
				private Result evaluate(Type<? extends Value> type, List<Value> values) throws EvaluationException {
					if (!type.isComparable()) {
						throw new EvaluationException("Function max cannot be applied to argument of type " + type);
					}
					
					Value best = null;
					for (Value value : values) {
						if (value != null) {
							if (best == null || ((Comparable) value).compareTo(best) > 0) {
								best = value;
							}
						}
					}
					
					return new OneResult(type, best);
				}
			});
		}
	};
	
	public Result evaluate(Result arg) throws EvaluationException {
		throw new EvaluationException("Function " + this.name() + " is not applicable to 1 argument");
	}
	
	public Result evaluate(Result arg1, Result arg2) throws EvaluationException {
		throw new EvaluationException("Function " + this.name() + " is not applicable to 2 arguments");
	}
	
	public static CallModifier getByName(String modifierName) {
		try {
			return CallModifier.valueOf(modifierName);
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
	
	// Helper functions
	
	protected Integer extractQuantity(Result arg) throws EvaluationException {
		return arg.accept(new ResultVisitor<Integer, EvaluationException>() {

			public Integer visit(OneResult result)
					throws EvaluationException {
				if (!result.getType().equals(SimpleType.INTEGER)) {
					throw new EvaluationException("First argument of function " + name() + " should be of type " +
							SimpleType.INTEGER + ", but is " + result.getType());
				}
				if (result.getValue() == null) {
					throw new EvaluationException("First argument of function " + name() + " cannot be NULL");
				}
				long quantity = ((IntegerValue) result.getValue()).getInteger();
				if (quantity < 0) {
					return 0;
				} else if (quantity > Integer.MAX_VALUE) {
					// This should be fine - collections should be quite small
					return Integer.MAX_VALUE;
				} else {
					return (int) quantity;
				}
			}

			public Integer visit(ListResult result)
					throws EvaluationException {
				throw new EvaluationException("Function " + name() + " cannot take ListResult as a first argument");
			}

			public Integer visit(ColumnResult result)
					throws EvaluationException {
				throw new EvaluationException("Function " + name() + " cannot take ColumnResult as a first argument");
			}
			
		});
	}
	
	protected List<Value> extractValues(Result arg) throws EvaluationException {
		return arg.accept(new ResultVisitor<List<Value>, EvaluationException>() {

			public List<Value> visit(OneResult result)
					throws EvaluationException {
				throw new EvaluationException("Function " + name() + " is not applicable to OneResult");
			}

			public List<Value> visit(ListResult result)
					throws EvaluationException {
				return result.getValues();
			}

			public List<Value> visit(ColumnResult result)
					throws EvaluationException {
				return result.getValues();
			}
			
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected List<SimpleValue> extractSimpleValues(Result arg) throws EvaluationException {
		if (!(arg.getType() instanceof SimpleType)) {
			throw new EvaluationException("Function " + name() + " can aggregate only simple types, not " +
					arg.getType());
		}
		// Because (List<SimpleValue>) extractValues(arg) is treated by Eclipse as an error
		return (List) extractValues(arg);
	}

}
