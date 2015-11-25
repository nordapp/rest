package com.i3xx.util.ctree.parser;

import java.io.IOException;
import java.util.Map;

public abstract class AbstractReader {

	protected Map<String, String> params;
	
	public AbstractReader() {
		this.setParams(null);
	}
	
	/**
	 * @return true if a line is available to be read
	 * @throws IOException 
	 */
	public abstract boolean available() throws IOException;
	
	/**
	 * Reads the next data (line, element, etc)
	 * 
	 * @return
	 * @throws IOException
	 */
	public abstract String readNext() throws IOException;
	
	/**
	 * Closes the reader
	 * @throws IOException
	 */
	public abstract void close() throws IOException;
	
	/**
	 * @param params the params to set
	 */
	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	/**
	 * @return the params
	 */
	public Map<String, String> getParams() {
		return params;
	}
}
