package org.i3xx.util.dbinfoapi.core;

public interface IAttribute {
	
	/**
	 * Return true if an attribute exists
	 * 
	 * @param name The name of the attribute
	 * @return 
	 */
	boolean hasAttribute(String name);
	
	/**
	 * Gets an attribute
	 * 
	 * @param name The name of the attribute
	 * @return The value of the attribute
	 */
	Object getAttribute(String name);
	
	/**
	 * Sets an attribute
	 * 
	 * @param name The name of the attribute
	 * @param value The value of the attribute
	 */
	void setAttribute(String name, Object value);

}
