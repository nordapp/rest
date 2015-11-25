package org.i3xx.util.rna.engine;

import java.io.IOException;

public interface IDataReader {

	/**
	 * Read the field to a string array
	 * 
	 * @param field
	 * @return
	 * @throws IOException
	 */
	String[] readStringArray(String field) throws IOException, ClassNotFoundException;

	/**
	 * Read the field to a long array
	 * 
	 * @param field
	 * @return
	 * @throws IOException
	 */
	long[] readLongArray(String field) throws IOException, ClassNotFoundException;

	/**
	 * Read the field to a int array
	 * 
	 * @param field
	 * @return
	 * @throws IOException
	 */
	int[] readIntArray(String field) throws IOException, ClassNotFoundException;

	/**
	 * Read the field to a double array
	 * 
	 * @param field
	 * @return
	 * @throws IOException
	 */
	double[] readDoubleArray(String field) throws IOException, ClassNotFoundException;
	
	/**
	 * Read the field to a byte array
	 * 
	 * @param field
	 * @return
	 * @throws IOException
	 */
	byte[] readByteArray(String field) throws IOException, ClassNotFoundException;
	
	/**
	 * Read the field to a new data reader
	 * 
	 * @param field
	 * @return
	 * @throws IOException
	 */
	IDataReader readDataReader(String field) throws IOException, ClassNotFoundException;
	
	/**
	 * Read the field to an object
	 * 
	 * @param field
	 * @return
	 * @throws IOException
	 */
	Object readObject(String field) throws IOException, ClassNotFoundException;
	
	/**
	 * Read the field to a string
	 * 
	 * @param field
	 * @return
	 * @throws IOException
	 */
	String readString(String field) throws IOException;
	
	/**
	 * Read the field to a long value
	 * 
	 * @param field
	 * @return
	 * @throws IOException
	 */
	long readLong(String field) throws IOException;

	/**
	 * Read the field to an int value
	 * 
	 * @param field
	 * @return
	 * @throws IOException
	 */
	int readInt(String field) throws IOException;
	
	/**
	 * Read the field to a double value
	 * 
	 * @param field
	 * @return
	 * @throws IOException
	 */
	double readDouble(String field) throws IOException;
	
	/**
	 * Read the field to a boolean value
	 * 
	 * @param field
	 * @return
	 * @throws IOException
	 */
	boolean readBoolean(String field) throws IOException;
	
	/**
	 * Return the available data (fields, number of bytes, etc.)
	 * 
	 * @return
	 * @throws IOException
	 */
	int available() throws IOException;
	
	/**
	 * Finish the writing
	 * 
	 * @throws IOException
	 */
	void close() throws IOException;
}
