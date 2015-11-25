package org.i3xx.util.ctree.func;


public interface IResolver {
	
	/**
	 * @param node
	 * @return
	 */
	void resolve(IVarNode node);
	
	/**
	 * @return true if anything has been resolved false otherwise
	 */
	boolean resolved();
	
	/**
	 * @return true if there is nothing more to resolve, false otherwise
	 */
	boolean finished();
}
