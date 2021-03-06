package org.i3xx.util.ctree.linker;

import java.io.Serializable;

import org.i3xx.util.ctree.core.IValueItem;


// This class needs to be immutable
public final class StringItem implements IValueItem, Serializable, Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final char[] value;
	
	public StringItem(String value) {
		this.value = value.toCharArray();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		//
		// Uses the same source immutable array
		//
		return super.clone();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IValueItem#getValue()
	 */
	public char[] getValue() {
		return value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new String(value);
	}

}
