package org.i3xx.util.ctree;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.i3xx.util.ctree.core.ResolverException;
import org.i3xx.util.ctree.impl.DefaultParser;
import org.i3xx.util.ctree.impl.LinkableResolverFactory;
import org.i3xx.util.ctree.impl.NodeParser;
import org.i3xx.util.ctree.impl.Protector;
import org.i3xx.util.ctree.impl.VisitorWalker;
import org.i3xx.util.ctree.linker.Linker;

public final class TreeBuilder {
	
	/**
	 * Parses the content of the file into the tree.
	 * 
	 * @param root The root of the tree
	 * @param file The file to parse
	 * @param props The static properties, arguments and environ variables to be set into the tree (optional, may be null)
	 * @return The root of the tree
	 * @throws IOException 
	 */
	public static IConfNode doParse(IConfNode root, File file, Properties props) throws IOException {
		
		//Parses the file
		DefaultParser parser = new DefaultParser(file);
		parser.setRules(root);
		parser.process();
		
		//Adds the properties
		if(props!=null) {
			for(String key : props.stringPropertyNames() ){
				String val = Protector.wrap( props.getProperty(key) );
				root.create(key).value(val);
			}//for
		}
		
		//Sets and configures the resolver
		NodeParser parserA = new NodeParser(new LinkableResolverFactory());
		VisitorWalker<IConfNode> walker = new VisitorWalker<IConfNode>();
		walker.setRoot(root);
		walker.walk(parserA);
		
		//Do the linking and functional processing
		Linker linker = new Linker(root);
		linker.process();
		
		return root;
	}
	
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
