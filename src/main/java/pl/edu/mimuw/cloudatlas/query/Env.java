package pl.edu.mimuw.cloudatlas.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;
import pl.edu.mimuw.cloudatlas.zones.Attribute;
import pl.edu.mimuw.cloudatlas.zones.ZMI;

public abstract class Env {

	public abstract Result getAttributeAsResult(String attributeName);
	
	public abstract List<Row> getInitialRows();
	
	public abstract Env subEnv(Row row);
	
	public abstract Env subEnv(List<Row> rows);
	
	public abstract Result getStoredResult(Object idObject);
	
	public abstract void storeResult(Object idObject, Result result);
	
	public static Env createFromZMIs(List<ZMI> infos) {
		return new RootEnv(infos);
	}
	
	public static class Row {
		private RootEnv owner;
		private List<Value> values;
		
		private Row(RootEnv owner, List<Value> values) {
			this.owner = owner;
			this.values = values;
		}
	}
	
	private static abstract class SubEnv extends Env {
		protected final RootEnv owner;
		
		private SubEnv(RootEnv owner) {
			this.owner = owner;
		}

		@Override
		public List<Row> getInitialRows() {
			return owner.getInitialRows();
		}

		@Override
		public Env subEnv(Row row) {
			return owner.subEnv(row);
		}

		@Override
		public Env subEnv(List<Row> rows) {
			return owner.subEnv(rows);
		}

		@Override
		public Result getStoredResult(Object idObject) {
			return owner.getStoredResult(idObject);
		}

		@Override
		public void storeResult(Object idObject, Result result) {
			owner.storeResult(idObject, result);
		}
	}
	
	private static class RowEnv extends SubEnv {
		private Row row;
		
		private RowEnv(Row row) {
			super(row.owner);
			this.row = row;
		}

		@Override
		public Result getAttributeAsResult(String attributeName) {
			Integer index = owner.columnIndices.get(attributeName);
			if (index == null) {
				return null;
			} else {
				return new OneResult(owner.columnTypes.get(index), row.values.get(index));
			}
		}
	}
	
	private static class RowListEnv extends SubEnv {
		private final List<Row> rows = new ArrayList<Row>();
		
		private RowListEnv(RootEnv owner, List<Row> rows) {
			super(owner);
			this.rows.addAll(rows);
		}

		@Override
		public Result getAttributeAsResult(String attributeName) {
			Integer index = owner.columnIndices.get(attributeName);
			if (index == null) {
				return null;
			} else {
				List<Value> values = new ArrayList<Value>();
				for (Row row : rows) {
					values.add(row.values.get(index));
				}
				return new ColumnResult(owner.columnTypes.get(index), values);
			}
		}
	}
	
	private static class RootEnv extends Env {
		private final Map<String, Integer> columnIndices = new HashMap<String, Integer>();
		private final List<Type<? extends Value>> columnTypes = new ArrayList<Type<? extends Value>>();
		private final List<Row> rows = new ArrayList<Row>();
		
		private final IdentityHashMap<Object, Result> storedResults = new IdentityHashMap<Object, Result>();
		
		private RootEnv(List<ZMI> infos) {
			fillColumnIndicesAndTypes(infos);
			fillRows(infos);
		}
		
		private void fillColumnIndicesAndTypes(List<ZMI> infos) {
			for (ZMI info : infos) {
				for (Attribute attribute : info.getAttributes()) {
					Integer currentIndex = columnIndices.get(attribute.getName());
					if (currentIndex == null) {
						int assignedIndex = columnTypes.size();
						columnIndices.put(attribute.getName(), assignedIndex);
						columnTypes.add(attribute.getType());
					} else if (!columnTypes.get(currentIndex).equals(attribute.getType())) {
						throw new IllegalArgumentException("Inconsistent types for attribute " + attribute.getName());
					}
				}
			}
		}
		
		private void fillRows(List<ZMI> infos) {
			for (ZMI info : infos) {
				List<Value> rowValues = new ArrayList<Value>(Collections.<Value>nCopies(columnTypes.size(), null));
				for (Attribute attribute : info.getAttributes()) {
					rowValues.set(columnIndices.get(attribute.getName()), attribute.getValue());
				}
				rows.add(new Row(this, rowValues));
			}
		}

		@Override
		public List<Row> getInitialRows() {
			return Collections.unmodifiableList(rows);
		}

		@Override
		public Env subEnv(Row row) {
			assert row.owner == this;
			
			return new RowEnv(row);
		}

		@Override
		public Env subEnv(List<Row> rows) {
			assert ownedByMe(rows);
			
			return new RowListEnv(this, rows);
		}
		
		private boolean ownedByMe(List<Row> rows) {
			for (Row row : rows) {
				if (row.owner != this) {
					return false;
				}
			}
			return true;
		}

		@Override
		public Result getAttributeAsResult(String attributeName) {
			throw new UnsupportedOperationException("You should create subenvironment first");
		}

		@Override
		public Result getStoredResult(Object idObject) {
			assert idObject != null;
			
			return storedResults.get(idObject);
		}

		@Override
		public void storeResult(Object idObject, Result result) {
			assert idObject != null;
			assert result != null;
			
			storedResults.put(idObject, result);
		}
	}
}
