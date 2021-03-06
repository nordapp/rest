package org.i3xx.util.ctree.linker;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.i3xx.util.ctree.IConfNode;
import org.i3xx.util.ctree.core.IValueItem;
import org.i3xx.util.ctree.core.IVisitor;
import org.i3xx.util.ctree.func.IResolver;
import org.i3xx.util.ctree.func.IVarNode;
import org.i3xx.util.ctree.func.LinkResolver;
import org.i3xx.util.ctree.func.ShiftResolver;
import org.i3xx.util.ctree.func.VarNode;
import org.i3xx.util.ctree.func.WildcardResolver;
import org.i3xx.util.ctree.impl.LinkableResolverFactory;
import org.i3xx.util.ctree.impl.NodeParser;
import org.i3xx.util.ctree.impl.VisitorWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Link the referenced nodes to the result nodes.
 * 
 * @author Stefan
 *
 */
public class Linker extends VisitorWalker<IConfNode> {
	
	private static final Logger logger = LoggerFactory.getLogger(Linker.class);
	
	protected IConfNode[] data;
	
	public Linker(IConfNode root) {
		super();
		
		this.data = null;
		setRoot(root);
	}
	
	/**
	 * @param root The root to link
	 * @param data The data to link to
	 */
	public Linker(IConfNode root, IConfNode data) {
		super();
		
		this.data = new IConfNode[]{data};
		setRoot(root);
	}
	
	/**
	 * @param root The root to link
	 * @param data The data to link to
	 */
	public Linker(IConfNode root, IConfNode[] data) {
		super();
		
		this.data = data;
		setRoot(root);
	}

	/**
	 * run the linking process
	 */
	public void process() {
		final Linker lk = this;
		walk( new IVisitor(){ 
			public void visit(IConfNode node) {
				lk.visit(node);
			}});
	}
	
	/**
	 * @param node The node to visit
	 */
	private void visit(IConfNode node) {
		if(logger.isTraceEnabled())
			logger.trace("The linker visits: {}, resolver:{}", node.getFullName(), node.resolver()!=null);
		
		if(node.resolver() instanceof LinkedResolver){
			LinkedResolver lr = (LinkedResolver)node.resolver();
			IValueItem[] values = new IValueItem[lr.paths.size()];
			for(int i=0;i<lr.paths.size();i++){
				String path = lr.paths.get(i);
				
				//Println.debug(path);
				
				//TODO
				List<IConfNode> copies = new ArrayList<IConfNode>();
				
				//a function is still in the name
				IVarNode vnode = new VarNode();
				vnode.setValue(path);
				vnode.parse();
				
				IResolver resolverA = new WildcardResolver(node.getFullName());
				vnode.resolve(resolverA);
				//resolver.resolved();
				
				IResolver resolverB = new ShiftResolver(path);
				vnode.resolve(resolverB);
				
				IResolver resolverC = new LinkResolver(node, copies);
				vnode.resolve(resolverC);
				//
				
				//The copies of the link resolver must be processed
				NodeParser parser = new NodeParser(new LinkableResolverFactory());
				for(IConfNode c : copies){
					parser.visit(c);
					visit(c);
				}
				
				if(path.startsWith("#")){
					values[i] = new StringItem(path.substring(1));
					if(logger.isDebugEnabled())
						logger.debug("Visit string-item (text) node:{}, text:{}", path, values[i].toString());
				}else if(resolverA.resolved()){
					values[i] = new StringItem(path);
					if(logger.isDebugEnabled())
						logger.debug("Visit string-item (wild) node:{}, text:{}", path, values[i].toString());
				}else if(resolverB.resolved()){
					values[i] = new StringItem(path);
					if(logger.isDebugEnabled())
						logger.debug("Visit string-item (shift) node:{}, text:{}", path, values[i].toString());
				}else if(resolverC.resolved()){
					values[i] = new StringItem(path);
					if(logger.isDebugEnabled())
						logger.debug("Visit string-item (link) node:{}, text:{}", path, values[i].toString());
				}else{
					try{
						IConfNode link = null;
						//read root
						try{
							link = root.get(path);
						}catch(NoSuchElementException e){}
						//read data
						if(data!=null){
							for(int k=0;k<data.length && link==null;k++){
								try{
									link = data[i].get(path);
								}catch(NoSuchElementException e){}
							}//for
						}//fi
						values[i] = new LinkedItem(link);
						if(logger.isDebugEnabled())
							logger.debug("Visit link-item (link) node:{}, value:{}", path, values[i].getValue());
						//NoSuchElementException
						if(link==null){
							logger.debug("Link not found: '{}'.", path);
							values[i] = new StringItem(path);
							if(logger.isDebugEnabled())
								logger.debug("Visit string-item (null) node:{}, text:{}", path, values[i].toString());
						}
					}catch(Exception e){
						logger.debug("", e);
						logger.info("Caution: Unable to resolve the link '{}'. Check the configuration.", path);
						values[i] = new StringItem(path);
					}
				}//fi
			}//for
			lr.values = values;
		}//fi
	}

}
