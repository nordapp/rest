package org.i3xx.util.ctree.impl;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.i3xx.util.ctree.IConfNode;
import org.i3xx.util.ctree.core.IResolveRaw;
import org.i3xx.util.ctree.core.IResolverFactory;
import org.i3xx.util.ctree.core.IVisitor;
import org.i3xx.util.ctree.linker.LinkedResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NodeParser implements IVisitor {
	
	private final static Logger logger = LoggerFactory.getLogger(NodeParser.class);
	
	private IResolverFactory factory;
	private Pattern patternA;
	private Pattern patternB;
	
	public NodeParser(IResolverFactory factory) {
		
		// Description of the regular expression: Extract a variable from a statement in a text line
		// or an element.
		//
		//  name     ::= PCNAME | *
		//  func     ::= name '-' '>' name
		//  path     ::= name ( '.' [ name | func ] )*
		//  text-var ::= '[' path ']'
		//  xml-var  ::= '$' '{' path '}'
		//  linkA    ::= text-var
		//  linkB    ::= xml-var
		//
		//                '[' name ( '.' name )* ']'
		//                 [( name (  .  name )* )]
		//
		//                '$' '{' name ( '.' name )* '}'
		//                 $   {  name (  .  name )* )}
		//
		//
		//Pattern:
		//this.patternA = Pattern.compile("\\$\\{(\\w+(\\.\\w+)*)\\}");
		//this.patternB = Pattern.compile("\\[(\\w+(\\.\\w+)*)\\]");
		//Pattern with function:
		//this.patternA = Pattern.compile("\\$\\{((\\w+|\\*)(\\.(\\w+|\\*|(\\w+|\\*)\\-\\>(\\w+|\\*)))*)\\}");
		//this.patternB = Pattern.compile("\\[((\\w+|\\*)(\\.(\\w+|\\*|(\\w+|\\*)\\-\\>(\\w+|\\*)))*)\\]");
		//Pattern that replaces \\[ and \\] with \uFF5B and \uFF5D
		//this.patternB = Pattern.compile("\uFF5B((\\w+|\\*)(\\.(\\w+|\\*|(\\w+|\\*)\\-\\>(\\w+|\\*)))*)\uFF5D");
		//Pattern that use (\\w+(\\-\\w+)*)+ instead of \\w+
		//this.patternA = Pattern.compile("\\$\\{(((\\w+(\\-\\w+)*)+|\\*)+(\\.((\\w+(\\-\\w+)*)+|\\*|((\\w+(\\-\\w+)*)+|\\*)\\-\\>((\\w+(\\-\\w+)*)+|\\*)))*)\\}");
		//this.patternB = Pattern.compile("\uFF5B(((\\w+(\\-\\w+)*)+|\\*)+(\\.((\\w+(\\-\\w+)*)+|\\*|((\\w+(\\-\\w+)*)+|\\*)\\-\\>((\\w+(\\-\\w+)*)+|\\*)))*)\uFF5D");
		//Allow a single name
		//this.patternA = Pattern.compile("\\$\\{(((\\w+(\\-\\w+)*)+|\\*)?((\\.|(\\w+(\\-\\w+)*)+\\-\\>(\\w+(\\-\\w+)*)+)((\\w+(\\-\\w+)*)+|\\*|((\\w+(\\-\\w+)*)+|\\*)\\-\\>((\\w+(\\-\\w+)*)+|\\*)))*)\\}");
		//this.patternB = Pattern.compile("\uFF5B(((\\w+(\\-\\w+)*)+|\\*)?((\\.|(\\w+(\\-\\w+)*)+\\-\\>(\\w+(\\-\\w+)*)+)((\\w+(\\-\\w+)*)+|\\*|((\\w+(\\-\\w+)*)+|\\*)\\-\\>((\\w+(\\-\\w+)*)+|\\*)))*)\uFF5D");
		//this.patternA = Pattern.compile("\\$\\{(((\\w+(\\-\\w+)*)+|\\*)+(\\.((\\w+(\\-\\w+)*)+|\\*|((\\w+(\\-\\w+)*)+|\\*)\\-\\>((\\w+(\\-\\w+)*)+|\\*)))*)\\}");
		//this.patternB = Pattern.compile("\uFF5B(((\\w+(\\-\\w+)*)+|\\*)+(\\.((\\w+(\\-\\w+)*)+|\\*|((\\w+(\\-\\w+)*)+|\\*)\\-\\>((\\w+(\\-\\w+)*)+|\\*)))*)\uFF5D");
		//this.patternA = Pattern.compile("\\$\\{(((\\w+(\\-\\w+)*|\\*)(\\.(\\w+(\\-\\w+)*|\\*))*)(\\-\\>((\\w+(\\-\\w+)*|\\*)(\\.(\\w+(\\-\\w+)*|\\*))*))?)\\}");
		//this.patternB = Pattern.compile("\uFF5B(((\\w+(\\-\\w+)*|\\*)(\\.(\\w+(\\-\\w+)*|\\*))*)(\\-\\>((\\w+(\\-\\w+)*|\\*)(\\.(\\w+(\\-\\w+)*|\\*))*))?)\uFF5D");
		this.patternA = Pattern.compile("\\$\\{(((\\w[\\w-]*|\\*)(\\.(\\w[\\w-]*|\\*))*)(\\-\\>((\\w[\\w-]*|\\*|\\{\\d+\\})(\\.(\\w[\\w-]*|\\*|\\{\\d+\\}))*))?)\\}");
		this.patternB = Pattern.compile("\uFF5B(((\\w[\\w-]*|\\*)(\\.(\\w[\\w-]*|\\*))*)(\\-\\>((\\w[\\w-]*|\\*|\\{\\d+\\})(\\.(\\w[\\w-]*|\\*|\\{\\d+\\}))*))?)\uFF5D");
		//This is the word definition ::= ((\\w[\\w-]*|\\*)(\\.(\\w[\\w-]*|\\*))*)
		//word = \w[\w-]* | \*
		//path ::= word ( '.' word )*
		
		//Note:
		//Pattern b doesn't match \[ \] because of the 2nd backslash.
		
		this.factory = factory;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.core.IVisitor#visit(com.i3xx.util.ctree.IConfNode)
	 */
	public void visit(IConfNode node) {
		process(node);
	}
	
	/**
	 * @param node The node to process
	 * @throws IOException
	 */
	public void process(IConfNode node) {
		
		String param = node.value();
		
		//XML -> Flat text + protection '\[', '\]', '\\'
		//Protection -> Charset 0xFFnn
		//Processing
		//Charset 0xFFnn -> Protection
		//Unprotect
		
		if(logger.isTraceEnabled())
			logger.trace("The param of {} is '{}'", node.getFullName(), param);
		
		//
		// The root node has neither a resolver nor a value
		//
		if(param==null)
			return;
		
		//
		// Sets a resolver 
		//
		IResolveRaw resolver = null;
		
		//The XML-style resolver
		if(resolver==null){
			//The XML style link
			int lpos = 0;
			Matcher m = patternA.matcher(param);
			
			if(logger.isTraceEnabled()) {
				logger.trace("The NodeParser matches (xml style) '{}' {}", param, m.find());
				m.reset();
			}
			
			while(m.find()){
				if(resolver==null)
					resolver = factory.getResolver(IResolverFactory.TYPE_LINK);
				String t = m.group(1);
				if(m.start()>lpos){
					((LinkedResolver)resolver).addPath("#"+param.substring(lpos, m.start())); 
				}
				lpos = m.end();
				((LinkedResolver)resolver).addPath(t);
			}
			if(lpos>0 && lpos<param.length()){
				((LinkedResolver)resolver).addPath("#"+param.substring(lpos));
			}
			//lpos = param.length();
		}
		
		//The text-style resolver
		if(resolver==null){
			//The text style link
			int lpos = 0;
			Matcher m = patternB.matcher(param);
			
			if(logger.isTraceEnabled()) {
				logger.trace("The NodeParser matches (text style) '{}' {}", param, m.find());
				m.reset();
			}
			
			while(m.find()){
				if(resolver==null)
					resolver = factory.getResolver(IResolverFactory.TYPE_LINK);
				String t = m.group(1);
				if(m.start()>lpos){
					((LinkedResolver)resolver).addPath("#"+param.substring(lpos, m.start())); 
				}
				lpos = m.end();
				//protect the '[' by a '\'
				if(m.start()>0 && param.charAt( m.start()-1 )=='\\'){
					((LinkedResolver)resolver).addPath("#"+m.group(0));
				}else{
					((LinkedResolver)resolver).addPath(t);
				}
			}
			if(lpos>0 && lpos<param.length()){
				((LinkedResolver)resolver).addPath("#"+param.substring(lpos));
			}
			//lpos = param.length();
		}
		
		if(resolver==null && Protector.match(param))
			resolver = factory.getResolver(IResolverFactory.TYPE_ESCAPE);
		
		//The default resolver
		if(resolver==null)
			resolver = factory.getResolver(IResolverFactory.TYPE_DEFAULT);
		
		node.resolver( resolver );
	}
	
}
