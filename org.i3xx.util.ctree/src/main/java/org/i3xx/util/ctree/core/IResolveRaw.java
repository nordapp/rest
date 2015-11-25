package org.i3xx.util.ctree.core;

import org.i3xx.util.ctree.IConfNode;

public interface IResolveRaw {
	
	/**
	 * @return The cloned Object
	 */
	Object clone() throws CloneNotSupportedException;
	
	/**
	 * The resolver to resolve the value
	 * 
	 * @param raw The node
	 * @return The resolved value
	 */
	String resolve(IConfNode node) throws ResolverException;
}
