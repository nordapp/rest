package org.i3xx.util.dbinfoapi.impl;

import java.util.HashMap;
import java.util.Map;

public class AttributeImpl {
	
	/**
	 * The attributes of the collection
	 */
	protected Map<String, Object> attributes;
	
	public AttributeImpl() {
		this.attributes = new HashMap<String, Object>();
	}
	
	/**
	 * Return true if an attribute exists
	 * 
	 * @param name The name of the attribute
	 * @return 
	 */
	public boolean hasAttribute(String name) {
		return attributes.containsKey(name);
	}
	
	/**
	 * Gets an attribute
	 * 
	 * @param name The name of the attribute
	 * @return The value of the attribute
	 */
	public Object getAttribute(String name) {
		return attributes.get(name);
	}
	
	/**
	 * Sets an attribute
	 * 
	 * @param name The name of the attribute
	 * @param value The value of the attribute
	 */
	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}

}
