package com.i3xx.util.ctree.core;

import com.i3xx.util.ctree.IConfNode;

/**
 * @author Stefan
 */
public interface IVisitor {

	/**
	 * @param node The node to visit
	 */
	void visit(IConfNode node);
}
