package org.i3xx.util.ctree.func;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The wildcards are replaced by the path name of the configuration node.
 * 
 * name-1 '.' name-2 '.' name-3 '[' path-1 '-' '>' path-2 ']'
 * 
 * Types of path-1 and path-2
 * part1.part2 => part1.part2
 * part1.* => part1.name-2
 * *.part2 => name-1.part2
 * *.* => name-1.name-2
 * 
 * @author Stefan
 * @see read-links.conf for excemples
 */
public class WildcardResolver implements IResolver {
	
	Logger logger = LoggerFactory.getLogger(WildcardResolver.class);
	
	protected String destName;
	protected boolean resolved;
	protected boolean finished;
	
	public WildcardResolver(String destName) {
		this.destName = destName;
		resolved = false;
		finished = false;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IResolver#resolve(com.i3xx.util.ctree.IVarNode)
	 */
	public void resolve(IVarNode node) {
		
		//if(destName.startsWith("data.object.agreement")){
		//	Println.debug("AGR::"+node.getValue()+" op="+node.getOp()+" left="+node.getLeft()+" right="+node.getRight()+" resolve="+(!(!node.isLeaf() || node.getValue()==null || node.getOp()!=VarNode.OP.REPLACE)));
		//}
		if(logger.isDebugEnabled())
			logger.debug("AGR::{} op={} left={} right={} resolve={}"+
					node.getValue(), node.getOp(), node.getLeft(), node.getRight(),
					(!(!node.isLeaf() || node.getValue()==null || node.getOp()!=VarNode.OP.REPLACE)));
		
		//needs a valid leaf node and an operator REPLACE
		if(node.isLeaf() || node.getOp()!=VarNode.OP.REPLACE || !node.getRight().isLeaf())
			return;
		
		String fullName = node.getRight().getValue();
		
		String[] paths = destName.split("\\.");
		String[] parts = fullName.split("\\.");
		for(int i=0;i<parts.length;i++){
			String part = parts[i];
			//paths is too short - cannot match
			if(i==paths.length){
				//Println.debug("#1 "+fullName);
				printName(parts, node.getRight());
				finished = false;
				return;
			}
			
			String path = paths[i];
			if(part.equals(path)){
				continue;
			}else if( part.contains("*") ){
				//Println.debug("#2 "+part+" "+fullName+" "+destName);
				parts[i] = path;
				continue;
			}else{
				//doesn't match
				//Println.debug("#3 "+fullName+" "+destName+" "+part+" "+path);
				printName(parts, node.getRight());
				
				//last part is not equals
				if(i==(parts.length-1))
					break;
				
				finished = false;
				return;
			}
		}
		//match until last element in part and path
		//the function is done
		
		//Println.debug("--"+node.toString());
		resolved = true;
		finished = true;
		
		node.setOp(null);
		node.setLeft(null);
		node.setRight(null);
		printName(parts, node);
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IResolver#resolved()
	 */
	public boolean resolved() {
		return resolved;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IResolver#finished()
	 */
	public boolean finished() {
		return finished;
	}
	
	/**
	 * @param parts
	 * @param node
	 */
	private void printName(String[] parts, IVarNode node) {
		//create new key string :: part [ '.' part ]*
		StringBuffer buf = new StringBuffer();
		for(String part:parts){
			buf.append('.');
			buf.append(part);
		}
		buf.deleteCharAt(0);
		
		node.setValue( buf.toString() );
	}
}
