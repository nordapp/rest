/*
 * Created on 30.06.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.i3xx.util.ctree.xml;

import java.io.IOException;
import java.io.InputStream;

import org.i3xx.util.core.ConvertStringTokenizer;
import org.i3xx.util.core.StreamSource;
import org.i3xx.util.core.xml.SimpleXMLParserWrapper;
import org.i3xx.util.core.xml.XMLParserWrapper;
import org.i3xx.util.ctree.ConfNode;
import org.i3xx.util.ctree.IConfNode;
import org.i3xx.util.ctree.IParser;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 * 
 * @Deprecated: Use new parser concept (com.util.ctree.parser.*).
 */
@Deprecated
public class ConfParser implements IParser {

	private IConfNode root;
	private IConfNode adds;

	public ConfParser() throws IOException
	{
		this.root = new ConfNode();
		this.adds = new ConfNode();
	}

	public ConfParser(IConfNode root) throws IOException
	{
		if(root == null)
			root = new ConfNode();

		this.root = root;
		this.adds = new ConfNode();
	}

	public ConfParser(Document document) throws IOException
	{
		this.root = new ConfNode();
		this.adds = null;
		
		NodeIterator iter = new NodeIterator(root, adds);
		iter.iterate(document);
	}
	
	/**
	 * @param document
	 * @throws IOException
	 */
	public void addconf(Document document) throws IOException
	{
		NodeIterator iter = new NodeIterator(root, adds);
		iter.iterate(document);
	}

	/**
	 * @param file
	 * @throws IOException
	 */
	public void addconf(String file) throws IOException
	{
		XMLParserWrapper p = new SimpleXMLParserWrapper();
		p.setXML(StreamSource.of(file, "iso-8859-15").getInputSource());
		
		NodeIterator iter = new NodeIterator(root, adds);
		try {
			p.process();
			iter.iterate(p.getDocument());
		} catch (SAXException e) {
			throw new IOException(e.toString());
		}
	}

	/**
	 * @param in
	 * @throws IOException
	 */
	public void addconf(InputStream in) throws IOException
	{
		XMLParserWrapper p = new SimpleXMLParserWrapper();
		p.setXML(in);
		
		NodeIterator iter = new NodeIterator(root, adds);
		try {
			p.process();
			iter.iterate(p.getDocument());
		} catch (SAXException e) {
			throw new IOException(e.toString());
		}
	}

	/**
	 * @param k
	 * @param v
	 * @throws IOException
	 */
	public void addconf(String k, String v) throws IOException
	{
		root.create(k).value( convert(v) );
	}

	/**
	 * @return
	 */
	public IConfNode getRoot()
	{
		return root;
	}

	/**
	 * @return
	 */
	public IConfNode getAdds()
	{
		return adds;
	}

	/**
	 * Konverter f�r Konfigurationseintr�ge ${...} -> [...]
	 *
	 * @param value The configured value
	 * @return
	 * @throws IOException
	 */
	public static String convert(String value) throws IOException
	{
		StringBuffer buf = new StringBuffer();
		
		//escape '[', ']' and '\' because in the node tree
		//these chars have a special meaning.
		for(char c:value.toCharArray()){
			if(c=='[' || c==']' || c=='\\')
				buf.append('\\');
			
			buf.append(c);
		}
		value = buf.toString();
		
		ConvertStringTokenizer tok = new ConvertStringTokenizer(value, "${", "}");
		buf = new StringBuffer();
		while(tok.hasNextToken()){
			String tmp = tok.nextToken();
			if(tok.tokenIsText()){
				buf.append(tmp);
			}else if(tok.tokenIsElement()){
				//Any node containing a placeholder is linked in a replace list and
				//the replacement is made after creating all nodes.
				//ConvertStringTokenizer removes the brackets. For the node tree another
				//kind of brackets is used. To avoid the escaping of these brackets ('[' and ']')
				//use a replacement (CHAR_X_5B and CHAR_X_5D) instead.
				buf.append(org.i3xx.util.ctree.ConfParser.CHAR_X_5B + tmp + 
						org.i3xx.util.ctree.ConfParser.CHAR_X_5D);
			}
		}
		return buf.toString();
	}
}
