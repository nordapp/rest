package org.i3xx.util.ctree.core;

public interface IValueItem {
	
	/**
	 * @return The cloned Object
	 */
	Object clone() throws CloneNotSupportedException;
	
	/**
	 * @return The value of the item
	 */
	char[] getValue();
}
