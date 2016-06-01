package org.i3xx.util.ctree.func;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Administrator
 *
 */
public class VarNode implements IVarNode {
	
	private static final Logger logger = LoggerFactory.getLogger(VarNode.class);
	
	protected OP op;
	protected String value;
	protected IVarNode left;
	protected IVarNode right;
	protected boolean comment;
	
	public static enum OP {REPLACE, SHIFT, LINK}
	
	/**
	 * 
	 */
	public VarNode(){
		op = null;
		left = null;
		right = null;
		comment = false;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IVarNode#isLeaf()
	 */
	public boolean isLeaf() {
		return ((left == null) && (right == null) && (value != null));
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IVarNode#isUnary()
	 */
	public boolean isUnary() {
		if(op==null)
			return false;
		
		switch(op){
		case REPLACE:
			return true;
		case SHIFT:
			return true;
			default:
				return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IVarNode#isComment()
	 */
	public boolean isComment() {
		return comment;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IVarNode#setComment(boolean)
	 */
	public void setComment(boolean value){
		comment = value;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IVarNode#addLeft()
	 */
	public IVarNode addLeft() {
		if(left == null)
			left = new VarNode();
		
		return left;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IVarNode#getLeft()
	 */
	public IVarNode getLeft() {
		return left;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IVarNode#setLeft(com.i3xx.util.ctree.IVarNode)
	 */
	public void setLeft(IVarNode left) {
		this.left = left;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IVarNode#getOp()
	 */
	public OP getOp() {
		return op;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IVarNode#setOp(com.i3xx.util.ctree.VarNode.OP)
	 */
	public void setOp(OP op) {
		this.op = op;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IVarNode#addRight()
	 */
	public IVarNode addRight() {
		if(right == null)
			right = new VarNode();
		
		return right;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IVarNode#getRight()
	 */
	public IVarNode getRight() {
		return right;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IVarNode#setRight(com.i3xx.util.ctree.IVarNode)
	 */
	public void setRight(IVarNode right) {
		this.right = right;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IVarNode#getValue()
	 */
	public String getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IVarNode#setValue(java.lang.String)
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	//--- print and parse ---
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		if(isLeaf())
			return value;
		
		//Println.debug("leaf="+isLeaf()+" op="+(op!=null)+" left="+(left!=null)+" right="+(right!=null));
		if(logger.isDebugEnabled())
			logger.debug("leaf:{}, op:{}, left:{}, right:{}", isLeaf(), (op!=null), (left!=null), (right!=null));
		
		switch(op){
		case LINK:
			return left.toString() + OP_LINK + right.toString();
		case SHIFT:
			return right.toString();
		case REPLACE:
			return right.toString();
			default:
				return "";
		}
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IVarNode#parse()
	 */
	public void parse() {
		
		if(value==null)
			return;
		
		int p1 = 0;
		
		//operatoren
		if((p1 = value.lastIndexOf(IVarNode.OP_LINK)) > -1) {
			setOp(VarNode.OP.LINK);
			addRight().setValue( value.substring(p1 + IVarNode.OP_LINK.length()) );
			addLeft().setValue( value.substring(0, p1) );
			
			value = null;
			//recursion
			/*Println.debug("set and parse left: "+getLeft().getValue());*/
			left.parse();
			/*Println.debug("got left: left="+
					(left.getLeft()==null || left.getLeft().isLeaf() || left.getLeft().getValue()==null)+
					" right="+(left.getRight()==null || left.getRight().isLeaf() || left.getRight().getValue()==null)+
					" op="+(left.getOp())+
					" value="+left.getValue());*/
			if(logger.isDebugEnabled())
				logger.debug("got left: left:{}, right:{}, op:{}, value:{}",
						(left.getLeft()==null || left.getLeft().isLeaf() || left.getLeft().getValue()==null),
						(left.getRight()==null || left.getRight().isLeaf() || left.getRight().getValue()==null),
						(left.getOp()), left.getValue()	);
			
			/*Println.debug("set and parse right: "+getRight().getValue());*/
			right.parse();
			/*Println.debug("got right: left="+
					(right.getLeft()==null || right.getLeft().isLeaf() || right.getLeft().getValue()==null)+
					" right="+(right.getRight()==null || right.getRight().isLeaf() || right.getRight().getValue()==null)+
					" op="+(right.getOp())+
					" value="+right.getValue());*/
			if(logger.isDebugEnabled())
				logger.debug("got left: left:{}, right:{}, op:{}, value:{}",
						(right.getLeft()==null || right.getLeft().isLeaf() || right.getLeft().getValue()==null),
						(right.getRight()==null || right.getRight().isLeaf() || right.getRight().getValue()==null),
						(right.getOp()), right.getValue()	);
			
		}else
			
		//unary operators
		if( value.contains(IVarNode.OP_REPLACE) ){
			setOp(VarNode.OP.REPLACE);
			addRight().setValue( value );
			
			value = null;
		}else
		
		//unary operators
		if( value.contains(IVarNode.OP_SHIFT) ){
			setOp(VarNode.OP.SHIFT);
			addRight().setValue( value );
			
			value = null;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IVarNode#resolve(com.i3xx.util.ctree.IResolver)
	 */
	public void resolve(IResolver resolver) {
		//
		//Println.debug("resolve: right="+right+" left="+left+" op="+op+" value="+value+" resolver="+resolver);
		logger.debug("resolve right:{}, left:{}, op:{}, value:{}, resolver:{}", right, left, op, value, resolver);
		if(isLeaf()){
			//nothing to resolve but
		}else if(isUnary()){
			//Wildcard resolver tested; passed
			resolver.resolve(this);
		}else{
			//Println.debug("resolve(2): leaf="+isLeaf()+" unary="+(op!=null?isUnary():"")+" op="+op+" value="+value+" resolver="+resolver);
			if(logger.isDebugEnabled())
				logger.debug("resolve leaf:{}, unary:{}, value:{}, resolver:{}", isLeaf(), (op!=null?isUnary():""), value, resolver);
			//resolve value first
			right.resolve(resolver);
			//then resolve path
			left.resolve(resolver);
			//then resolve all
			resolver.resolve(this);
		}
	}
}
