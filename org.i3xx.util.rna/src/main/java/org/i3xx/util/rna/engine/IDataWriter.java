package org.i3xx.util.rna.engine;

import java.io.IOException;

public interface IDataWriter {
	
	/**
	 * Writes a string array
	 * 
	 * @param value
	 * @throws IOException
	 */
	void write(String field, String[] value) throws IOException;
	
	/**
	 * Writes a long array
	 * 
	 * @param value
	 * @throws IOException
	 */
	void write(String field, long[] value) throws IOException;
	
	/**
	 * Writes a int array
	 * 
	 * @param value
	 * @throws IOException
	 */
	void write(String field, int[] value) throws IOException;
	
	/**
	 * Writes a double array
	 * 
	 * @param value
	 * @throws IOException
	 */
	void write(String field, double[] value) throws IOException;
	
	/**
	 * Writes a structure
	 * 
	 * @param value
	 * @throws IOException
	 */
	void write(String field, byte[] value) throws IOException;
	
	/**
	 * Writes an object. If the object is a data structure,
	 * ensure any data in the object is a simple java type.
	 * 
	 * @param value
	 * @throws IOException
	 */
	void write(String field, Object value) throws IOException;
	
	/**
	 * Writes a string
	 * 
	 * @param value
	 * @throws IOException
	 */
	void write(String field, String value) throws IOException;
	
	/**
	 * Writes a long
	 * 
	 * @param value
	 * @throws IOException
	 */
	void write(String field, long value) throws IOException;
	
	/**
	 * Writes an int
	 * @param value
	 * @throws IOException
	 */
	void write(String field, int value) throws IOException;
	
	/**
	 * Writes a double
	 * 
	 * @param value
	 * @throws IOException
	 */
	void write(String field, double value) throws IOException;
	
	/**
	 * Writes a boolean
	 * 
	 * @param value
	 * @throws IOException
	 */
	void write(String field, boolean value) throws IOException;
	
	/**
	 * @return A sub writer
	 * @throws IOException
	 */
	IDataWriter getSubWriter(String field) throws IOException;
	
	/**
	 * Writes any buffered data out.
	 * 
	 * @throws IOException
	 */
	void flush() throws IOException;
	
	/**
	 * Finish the writing of the current data set.
	 * 
	 * @throws IOException
	 */
	void finish() throws IOException;
	
	/**
	 * Finish the writing of the current data set
	 * and closes the stream.
	 * 
	 * @throws IOException
	 */
	void close() throws IOException;
}
