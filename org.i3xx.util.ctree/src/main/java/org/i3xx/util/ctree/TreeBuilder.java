package org.i3xx.util.ctree;

import org.i3xx.util.ctree.core.ResolverException;

public final class TreeBuilder {

	/**
	 * Builds a new tree of final nodes that is a resolved copy of the origin tree.
	 * 
	 * @param root The IConfNode to build the final tree from.
	 * @return The root of the final tree
	 * @throws ResolverException
	 */
	public static IConfNode doFinal(IConfNode root) throws ResolverException {
		return new FinalConfNode(root, null);
	}
	
}
