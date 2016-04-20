package org.i3xx.util.ctree.core;

import java.io.Serializable;

import org.i3xx.util.ctree.IConfNode;
import org.i3xx.util.ctree.impl.Protector;


public class EscapeResolver implements IResolveRaw, Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IResolveRaw#resolve(com.i3xx.util.ctree.IConfNode)
	 */
	public String resolve(IConfNode node) throws ResolverException {
		return Protector.unwrap( node.rawValue() );
	}
}
