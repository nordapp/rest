package org.i3xx.util.ctree.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.i3xx.util.ctree.IConfNode;


public class NodePrinter {

	public static final String endOfLine="\n";
	
	private boolean raw;
	private Writer out;
	
	public NodePrinter(boolean raw, Writer out) {
		this.raw = raw;
		this.out = out;
	}
	
	/**
	 * @param node The root node
	 * @throws IOException
	 */
	public void process(IConfNode node) throws IOException
	{
		process("", node);
	}
	
	/**
	 * @param path The path
	 * @param node The node
	 * @throws IOException
	 */
	protected void process(String path, IConfNode node) throws IOException
	{
		Iterator<IConfNode> iter = node.getChildNodes2();
		
		while(iter.hasNext()){
			IConfNode temp = iter.next();
			String value = raw ? temp.rawValue() : temp.value();
			
			if(value != null){
				String line = (path+"."+temp.name()+" "+value).substring(1);
				//FIXME
				//protect '[' and ']' with '\' => '\[' '\]'
				//line = CString.replace(line, "[", "\\[");
				//line = CString.replace(line, "]", "\\]");
				
				out.write(line);
				out.write(endOfLine);
				out.flush();
			}
			
			process(path+"."+temp.name(), temp);
		}
	}
	
}
