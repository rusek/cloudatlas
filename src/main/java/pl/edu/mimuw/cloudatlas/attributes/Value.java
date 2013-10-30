package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataOutput;
import java.io.IOException;

// TODO convert to interface?
public abstract class Value {
	
	public abstract void compactWrite(DataOutput output) throws IOException;
	
	public abstract Type<? extends Value> getType();

	public abstract int hashCode();
	public abstract boolean equals(Object obj);
}
