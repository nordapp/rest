package org.i3xx.util.ctree.core;

import org.i3xx.util.ctree.IConfNode;

/**
 * @author Stefan
 */
public interface IVisitor {

	/**
	 * @param node The node to visit
	 */
	void visit(IConfNode node);
}
