package pl.edu.mimuw.cloudatlas.agent;

public class ByteArrays {

	private ByteArrays() {}
	
	public static int readInt(byte[] array, int offset) {
		return (((array[offset] & 0xff) << 24) |
				((array[offset + 1] & 0xff) << 16) |
				((array[offset + 2] & 0xff) << 8) |
				(array[offset + 3] & 0xff));
	}
	
	public static void writeInt(byte[] array, int offset, int value) {
		array[offset] = (byte) (0xff & (value >> 24));
		array[offset + 1] = (byte) (0xff & (value >> 16));
		array[offset + 2] = (byte) (0xff & (value >> 8));
		array[offset + 3] = (byte) (0xff & value);
	}

	public static long readLong(byte[] array, int offset) {
		return (((long)(array[offset] & 0xff) << 56) |
			    ((long)(array[offset + 1] & 0xff) << 48) |
				((long)(array[offset + 2] & 0xff) << 40) |
				((long)(array[offset + 3] & 0xff) << 32) |
				((long)(array[offset + 4] & 0xff) << 24) |
				((long)(array[offset + 5] & 0xff) << 16) |
				((long)(array[offset + 6] & 0xff) <<  8) |
				((long)(array[offset + 7] & 0xff)));
	}
	
	public static void writeLong(byte[] array, int offset, long value) {
		array[offset] = (byte)(0xff & (value >> 56));
		array[offset + 1] = (byte)(0xff & (value >> 48));;
		array[offset + 2] = (byte)(0xff & (value >> 40));
		array[offset + 3] = (byte)(0xff & (value >> 32));
		array[offset + 4] = (byte)(0xff & (value >> 24));
		array[offset + 5] = (byte)(0xff & (value >> 16));
		array[offset + 6] = (byte)(0xff & (value >> 8));
		array[offset + 7] = (byte)(0xff & value);
	}
}
