package com.i3xx.util.core.xml;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Locale;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.TextImpl;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author S. Hauptmann
 *
 */
public class SimpleXMLParserWrapper extends XMLParserWrapper
{
	protected final DOMParser parser;
	protected InputSource source;


	/**
	 * 
	 */
	public SimpleXMLParserWrapper()
	{
		parser = new DOMParser();
		parser.setLocale(new Locale("de", "DE"));
		source = null;
	}

	/**
	 * @param validation
	 * @throws SAXException
	 */
	public SimpleXMLParserWrapper(boolean validation) throws SAXException
	{
		parser = new DOMParser();
		parser.setFeature( "http://xml.org/sax/features/validation", validation);
		source = null;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.xml.XMLParserWrapper#setXML(java.lang.String)
	 */
	public void setXML( String in ){ setXML(new StringReader(in)); }
	/* (non-Javadoc)
	 * @see com.i3xx.util.xml.XMLParserWrapper#setXML(java.io.Reader)
	 */
	public void setXML( Reader in){ source = new InputSource(in); }
	/* (non-Javadoc)
	 * @see com.i3xx.util.xml.XMLParserWrapper#setXML(java.io.InputStream)
	 */
	public void setXML( InputStream in){ source = new InputSource(in); }
	/* (non-Javadoc)
	 * @see com.i3xx.util.xml.XMLParserWrapper#setXML(org.xml.sax.InputSource)
	 */
	public void setXML( InputSource is){ source = is; }

	/**
	 * @param out
	 * @throws IOException
	 * @throws SAXException
	 */
	public void getXML(Writer out) throws IOException, SAXException
	{
		Node root = parser.getDocument().getDocumentElement();
		XMLWriter writer = new XMLWriter();
		writer.getXML(out);
		writer.write(root);
	}

	/**
	 * @param out
	 * @throws IOException
	 * @throws SAXException
	 */
	public void getXML(OutputStream out) throws IOException, SAXException
	{
		Node root = parser.getDocument().getDocumentElement();
		XMLWriter writer = new XMLWriter();
		writer.getXML(out);
		writer.write(root);
	}

	/**
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public String getXML() throws IOException, SAXException
	{
		return getXML("utf8");
	}
	
	/**
	 * @param encoding
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public String getXML(String encoding) throws IOException, SAXException
	{
		//XML-String erstellen..
		Node root = parser.getDocument();
		XMLWriter writer = new XMLWriter(encoding);
		writer.write(root);
		return writer.getXML();
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.xml.XMLParserWrapper#getDocument()
	 */
	public Document getDocument() throws IOException, SAXException
	{
		return parser.getDocument();
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.xml.XMLParserWrapper#process()
	 */
	public void process() throws IOException, SAXException
	{
		parser.parse(source);
	}
	
	/**
	 * @throws IOException
	 */
	public void cleanup() throws IOException {
		InputStream bin = source.getByteStream();
		Reader cin = source.getCharacterStream();
		
		if(bin != null)
			bin.close();
		if(cin != null)
			cin.close();
	}

	/**
	 *	Wert aus dem DOM-tree auslesen
	 */
	public String getValue(String name)
	{
		try{
			Node root = parser.getDocument().getDocumentElement();
			Node node = XPathAPI.selectSingleNode(root, name);
			NodeList list = node.getChildNodes();
			if(list.getLength()>0)
				if(list.item(0).getNodeName().startsWith("#"))
					return list.item(0).getNodeValue();

			return "";
		}catch(Exception e){
			//Feld nicht gefunden
			return null;
		}
	}

	/**
	 *	Wert im DOM-tree ver�ndern
	 */
	public void setValue(String name, String value){
		try{
			//Sollten noch Erstetzungen vorhanden sein.
			//value = CharEntity.unwrapXML(value);

			Node root = parser.getDocument().getDocumentElement();
			Node node = XPathAPI.selectSingleNode(root, name);
			NodeList list = node.getChildNodes();
			if(list.getLength()==1){
				if(list.item(0).getNodeName().startsWith("#"))
					list.item(0).setNodeValue(value);
			}else if(list.getLength()==0){
				Node nde = new TextImpl((DocumentImpl)node.getOwnerDocument(), value);
				node.appendChild(nde);
			}
		}catch(Exception e){
			//Feld nicht gefunden
		}
	}
}
