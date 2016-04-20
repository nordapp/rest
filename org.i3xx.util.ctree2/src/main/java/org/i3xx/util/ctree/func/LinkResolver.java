package org.i3xx.util.ctree.func;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.i3xx.util.ctree.IConfNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LinkResolver implements IResolver {
	
	private static final Logger logger = LoggerFactory.getLogger(LinkResolver.class);
	
	protected IConfNode icNode;
	protected boolean resolved;
	protected boolean finished;
	protected List<IConfNode> copies;
	
	public LinkResolver(IConfNode node){
		this(node, null);
	}
	
	public LinkResolver(IConfNode node, List<IConfNode> copies) {
		super();
		this.icNode = node;
		this.copies = copies;
		this.resolved = false;
		this.finished = false;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IResolver#resolve(com.i3xx.util.ctree.IVarNode)
	 */
	public void resolve(IVarNode node) {

		
		//must have a link operator
		if(node.getOp()!=VarNode.OP.LINK)
			return;
		
		//needs a valid left leaf node
		if(node.getLeft()==null || !node.getLeft().isLeaf())
			return;
		
		//needs a valid right leaf node
		if(node.getRight()==null || !node.getRight().isLeaf())
			return;
		
		String src = node.getLeft().getValue();
		String dest = node.getRight().getValue(); 
		
		//Println.debug("<> src:"+src+" dest:"+dest);
		if(dest.equals(""))
			dest = icNode.getFullName();
		
		try{
			IConfNode srcNode = icNode.get(src);
			copyNode(srcNode, dest);
		}catch(NoSuchElementException e){
			logger.debug("LinkResolver: Unable to get the path '{}' used by '{}'.", src, dest);
			return;
		}
		
		resolved = true;
		
		//The result is resolved. The value is a comment
		//Caution: It is strictly recommended not to resolve the result value
		//         in any case.
		node.setComment(true);
		node.setOp(null);
		node.setLeft(null);
		node.setRight(null);
		node.setValue( src+" to "+dest);
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
	 * Copies the values and avoids double paths
	 * 
	 * @param srcNode
	 * @param destNode
	 */
	private void copyNode(IConfNode srcNode, String destPath) {
		
		Map<String, String> map = new LinkedHashMap<String, String>();
		readNode(srcNode, map);
		
		String srcPath = srcNode.getFullName();
		int len = srcPath.length();
		for(Map.Entry<String, String>elem:map.entrySet()){
			String keyPath = destPath+elem.getKey().substring(len);
			String value = elem.getValue();
			//If value contains a link with wildcard, this wildcard must be resolved here.
			//Println.debug("<> src:"+srcPath+" dest:"+destPath+" key:"+keyPath+" value:"+value);
			IConfNode node = srcNode.create(keyPath);
			if(node.rawValue()!=null){
				String v = node.rawValue();
				//
				// The copy operator '->' overwrites the node by the same value.
				// This may be a problem but up to now it works. (sh 18.12.2012)
				//
				if(v!=null && value!=null && !v.equals(value)){
					System.err.println("Caution: Link overwrites '"+keyPath+"' in configuration '"+v+"' with '"+value+"'.");
				}
                logger.debug("The link operator should not overwrite an existing configuration entry.\n" +
                		"the key is: '{}'\nthe value is: '{}'.\nthe new value is: '{}'.\n", keyPath, v, value);
                
                //TODO:
                continue;
			}
			node.value(value);
			if(logger.isDebugEnabled())
				logger.debug("Creates a new node src:{} key:{}, value:{}", srcPath, keyPath, value);
			//Test node content by query if necessary
			//Println.debug("++ key:"+keyPath+" value:"+value);
			
			if(copies!=null)
				copies.add(node);
		}
	}
	
	/**
	 * Helper method to copy conf nodes
	 * 
	 * @param node
	 * @param entries
	 */
	private void readNode(IConfNode node, Map<String, String>entries) {
		if(node.isLeafNode()){
			entries.put(node.getFullName(), node.rawValue());
		}else{
			entries.put(node.getFullName(), node.rawValue());
			for(IConfNode n:node.getChildArray())
				readNode(n, entries);
		}//fi
	}
}
