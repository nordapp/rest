/*
 * Created on 30.06.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.i3xx.util.ctree.xml;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.i3xx.util.ctree.IConfNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 * 
 * @Deprecated: Use new parser concept (com.uil.ctree.parser.*).
 */
@Deprecated
public class NodeWriter {
	
	private static final Logger logger = LoggerFactory.getLogger(NodeWriter.class);
	
	public static String endOfLine="\n";
	
	protected Writer out;
	
	/**
	 * 
	 */
	public NodeWriter(Writer out) {
		super();
		
		this.out = out;
	}
	
	public void process(IConfNode node) throws IOException
	{
		process("", node);
	}
	
	protected void process(String path, IConfNode node) throws IOException
	{
		Iterator<IConfNode> iter = node.getChildNodes2();
		
		while(iter.hasNext()){
			IConfNode temp = iter.next();
			
			if(temp.rawValue() != null){
				String line = (path+"."+temp.name()+" "+temp.rawValue()).substring(1);
				//FIXME
				//protect '[' and ']' with '\' => '\[' '\]'
				//line = CString.replace(line, "[", "\\[");
				//line = CString.replace(line, "]", "\\]");
				
				logger.debug("Write plain log '{}'.\n", line);
				
				out.write(line);
				out.write(NodeWriter.endOfLine);
				out.flush();
			}
			
			process(path+"."+temp.name(), temp);
		}
	}
}
