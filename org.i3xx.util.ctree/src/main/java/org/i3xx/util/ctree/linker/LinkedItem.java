package org.i3xx.util.ctree.linker;

import java.io.Serializable;

import org.i3xx.util.ctree.IConfNode;
import org.i3xx.util.ctree.core.IValueItem;


//This class needs to be immutable
public final class LinkedItem implements IValueItem, Serializable, Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public final IConfNode node;
	
	public LinkedItem(IConfNode node) {
		this.node = node;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		//
		// Uses the same source node
		//
		return super.clone();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IValueItem#getValue()
	 */
	public char[] getValue() {
		String value = node.value();
		return value==null ? new char[0] : value.toCharArray();
	}

}
