package org.i3xx.util.ctree.impl;

import org.i3xx.util.ctree.IConfNode;
import org.i3xx.util.ctree.core.IVisitor;

public class VisitorWalker<T> {

	protected IConfNode root;
	
	public VisitorWalker() {
		this.root = null;
	}
	
	/**
	 * @param root Sets the root
	 */
	public void setRoot(IConfNode root) {
		this.root = root;
	}
	
	/**
	 * @return Gets the root
	 */
	public IConfNode getRoot() {
		return root;
	}
	
	/**
	 * @param visitor The visitor that walks
	 */
	public void walk(IVisitor visitor) {
		walk(root, visitor);
	}
	
	/**
	 * @param node
	 * @param visitor
	 */
	protected void walk(IConfNode node, IVisitor visitor) {
		//
		visitor.visit(node);
		//traverse
		for(int i=0;i<node.size();i++){
			IConfNode child = node.getChildNode(i);
			walk(child, visitor);
		}
	}
}
