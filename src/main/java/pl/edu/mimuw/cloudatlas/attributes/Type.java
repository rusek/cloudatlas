package pl.edu.mimuw.cloudatlas.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

// TODO convert to interface?
public abstract class Type<V extends Value> {
	
	public abstract void compactWrite(DataOutput output) throws IOException;
	
	public abstract V compactReadValue(DataInput input) throws IOException;

}
