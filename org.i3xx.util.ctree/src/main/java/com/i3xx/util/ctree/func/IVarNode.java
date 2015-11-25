package com.i3xx.util.ctree.func;

import com.i3xx.util.ctree.func.VarNode.OP;

public interface IVarNode {
	
	public static final String OP_LINK = "->";
	public static final String OP_REPLACE = "*";
	
	/**
	 * @return Returns true if a node is a leaf node
	 */
	boolean isLeaf();
	
	/**
	 * @return returns true if the operator is unary
	 */
	boolean isUnary();
	
	/**
	 * A comment is a plain result or a description that will be not resolved
	 *  
	 * @return returns true if the node is a comment
	 */
	boolean isComment();
	
	/**
	 * A comment is a plain result or a description that will be not resolved
	 *  
	 * @param value True to flag the node as comment
	 */
	void setComment(boolean value);
	
	/**
	 * @return Adds and returns the left.
	 */
	IVarNode addLeft();
	
	/**
	 * @return Returns the left.
	 */
	IVarNode getLeft();
	
	/**
	 * @param left The left to set.
	 */
	void setLeft(IVarNode left);

	/**
	 * @return Returns the op.
	 */
	OP getOp();

	/**
	 * @param op The op to set.
	 */
	void setOp(OP op);

	/**
	 * @return Adds and returns the right.
	 */
	IVarNode addRight();

	/**
	 * @return Returns the right.
	 */
	IVarNode getRight();

	/**
	 * @param right The right to set.
	 */
	void setRight(IVarNode right);
	
	/**
	 * @return Returns the value.
	 */
	String getValue();

	/**
	 * @param value The value to set.
	 */
	void setValue(String value);
	
	/**
	 * 
	 */
	void parse();
	
	/**
	 * @param resolver
	 */
	void resolve(IResolver resolver);
}
