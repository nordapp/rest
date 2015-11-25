package com.i3xx.util.ctree.parser;

import java.io.IOException;
import java.io.Reader;

import org.xml.sax.SAXException;

import com.i3xx.util.core.xml.SimpleXMLParserWrapper;
import com.i3xx.util.core.xml.XMLParserWrapper;


/**
 * This XML Reader translates the XML-Data into a the key-value format. This format is read by the 
 * DefaultParser and processed by the key-value rules.
 * 
 * @author Stefan
 *
 */
public class XmlReader extends AbstractElementReader {

	private String fileName;
	
	public XmlReader(String fileName) {
		this.fileName = fileName;
	}
	
	public boolean available() throws IOException {
		if(!init){
			init = true;
			
			Reader r = new java.io.FileReader(fileName);
			XMLParserWrapper p = new SimpleXMLParserWrapper();
			p.setXML(r);
			
			NodeIterator iter = new NodeIterator(ArrayIndexMap.get(fileName));
			try {
				p.process();
				iter.iterate(p.getDocument());
			} catch (SAXException e) {
				throw new IOException(e.toString());
			}
			elements.addAll( iter.getList() );
			r.close();
		}
		return super.available();
	}
	
}
