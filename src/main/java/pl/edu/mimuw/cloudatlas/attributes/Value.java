package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public abstract class Value implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public abstract void compactWrite(DataOutput output) throws IOException;
	
	public abstract Type<? extends Value> getType();

	public abstract int hashCode();
	public abstract boolean equals(Object obj);
	
	public abstract Value deepCopy();
}
