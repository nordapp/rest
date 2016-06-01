package org.i3xx.util.ctree.func;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShiftResolver implements IResolver {
	
	Logger logger = LoggerFactory.getLogger(ShiftResolver.class);
	
	protected String fullName;
	protected boolean resolved;
	protected boolean finished;
	
	public ShiftResolver(String fullName) {
		this.fullName = fullName;
		resolved = false;
		finished = false;
	}

	public void resolve(IVarNode node) {
		if(logger.isDebugEnabled())
			logger.debug("AGR::{} op={} left={} right={} resolve={}",
					node.getValue(), node.getOp(), node.getLeft(), node.getRight(),
					(!(!node.isLeaf() || node.getValue()==null || node.getOp()!=VarNode.OP.SHIFT)));
		
		//needs a valid leaf node and an operator REPLACE
		if(node.isLeaf() || node.getOp()!=VarNode.OP.SHIFT || !node.getRight().isLeaf())
			return;
		
		String destName = node.getRight().getValue();
		int lp = fullName.indexOf(VarNode.OP_LINK);
		if(lp<0)
			return;
		
		String srcName = fullName.substring(0, lp);
		String[] parts = srcName.split("\\.");
		destName = ComputeText.complete(destName, parts);
		
		node.setValue( destName );
		
		resolved = true;
		finished = true;
		
		node.setOp(null);
		node.setLeft(null);
		node.setRight(null);
	}

	public boolean resolved() {
		return resolved;
	}

	public boolean finished() {
		return finished;
	}

}
