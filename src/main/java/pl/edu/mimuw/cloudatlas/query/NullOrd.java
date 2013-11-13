package pl.edu.mimuw.cloudatlas.query;

public enum NullOrd {
	NULLS_FIRST {
		@Override
		public boolean areNullsFirst(Ord ord) {
			return true;
		}
	},
	NULLS_LAST {
		@Override
		public boolean areNullsFirst(Ord ord) {
			return false;
		}
	},
	UNKNOWN {
		@Override
		public boolean areNullsFirst(Ord ord) {
			return ord == Ord.DESC;
		}
	};
	
	public abstract boolean areNullsFirst(Ord ord);
}
