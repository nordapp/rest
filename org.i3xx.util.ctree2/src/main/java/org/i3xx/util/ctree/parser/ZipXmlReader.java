package org.i3xx.util.ctree.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.i3xx.util.core.xml.SimpleXMLParserWrapper;
import org.i3xx.util.core.xml.XMLParserWrapper;
import org.xml.sax.SAXException;



/**
 * This XML Reader translates the XML-Data into a the key-value format. This format is read by the 
 * DefaultParser and processed by the key-value rules.
 * 
 * @author Stefan
 *
 */
public class ZipXmlReader extends AbstractElementReader {

	private String fileName;
	private String entryName;
	
	public ZipXmlReader(String fileName, String entryName) {
		this.fileName = fileName;
		this.entryName = entryName;
	}
	
	/**
	 * The file is closed manually
	 * @see org.i3xx.util.ctree.parser.KeyValueRuleIncludeA
	 */
	@SuppressWarnings("resource")
	public boolean available() throws IOException {
		if(!init){
			init = true;
			
			ZipFile file = new ZipFile(fileName);
			ZipEntry entry = file.getEntry(entryName);
			Reader r = new BufferedReader( new InputStreamReader( 
					file.getInputStream(entry)));
			
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
