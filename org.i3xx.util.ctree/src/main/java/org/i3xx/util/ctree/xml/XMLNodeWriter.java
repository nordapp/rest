/*
 * Created on 30.06.2004
 */
package org.i3xx.util.ctree.xml;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.i3xx.util.ctree.IConfNode;


/**
 * @author Administrator
 * 
 * @Deprecated: Use new parser concept (com.uil.ctree.parser.*).
 */
@Deprecated
public class XMLNodeWriter extends NodeWriter{
	
	protected int depth;
	
	/**
	 * 
	 */
	public XMLNodeWriter(Writer out) {
		super(out);
		
		depth = 0;
	}
	
	public void process(IConfNode node) throws IOException
	{
		//write start tag
		if(node.rawValue() == null){
			out.write(getBlanks());
			out.write("<"+node.name()+">");
			out.write(NodeWriter.endOfLine);
			out.flush();
		}else{
			out.write(getBlanks());
			out.write("<"+node.name()+">");
			out.write(node.rawValue());
			out.write("</"+node.name()+">");
			out.write(NodeWriter.endOfLine);
			out.flush();
		}
		
		//iterate
		Iterator<IConfNode> iter = node.getChildNodes2();
		while(iter.hasNext()){
			IConfNode temp = iter.next();
			depth++;
			process(temp);
			depth--;
		}
		
		//write end tag
		if(node.rawValue() == null){
			out.write(getBlanks());
			out.write("</"+node.name()+">");
			out.write(XMLNodeWriter.endOfLine);
			out.flush();
		}
	}
	
	protected String getBlanks() {
		String tmp = "";
		for(int i=0; i<depth; i++)
			tmp += "\t";
		
		return tmp;
	}
}
