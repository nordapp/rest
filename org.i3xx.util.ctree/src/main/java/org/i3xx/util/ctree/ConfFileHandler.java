/*
 * Created on 25.01.2005
 *
 */
package org.i3xx.util.ctree;

import java.io.BufferedInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.i3xx.util.core.xml.SimpleXMLParserWrapper;
import org.i3xx.util.core.xml.XMLParserWrapper;
import org.i3xx.util.ctree.xml.ConfParser;
import org.i3xx.util.ctree.xml.NodeWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 * @author S. Hauptmann
 * 
 * @Deprecated: Use new parser concept (com.uil.ctree.parser.*).
 *
 */
@SuppressWarnings("deprecation")
public class ConfFileHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(ConfFileHandler.class);
	
	public static final int ZIP_FILE_TYPE = 3;
	public static final int XML_FILE_TYPE = 2;
	public static final int DEFAULT_FILE_TYPE = 1;
	public static final int UNEXPECTED_FILE_TYPE = 0;
	
	private IConfNode root;

	/**
	 * 
	 */
	public ConfFileHandler(IConfNode root) {
		super();
		
		this.root = root;
	}

	/**
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public String getFile(String fileName) throws IOException {
		logger.debug("Read conf file '{}'.", fileName);
		
		Reader in = new FileReader(fileName);
		StringWriter out = new StringWriter();
		int c=0;
		char[] buf = new char[1024];
		while((c=in.read(buf))>-1)
			out.write(buf, 0, c);
		
		in.close();
		
		return out.toString();
	}
	
	/**
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public String getXFile(String fileName) throws IOException {
		//Parse XML and write dotted notation
		//Use com.i3xx.ob.util.ctree.xml.ConfParser
		ConfParser confParser = new ConfParser(root);
		confParser.addconf(fileName);
		
		StringWriter out = new StringWriter();
		NodeWriter writer = new NodeWriter(out);
		//((ILoggable)writer).addLogger(logger);
		writer.process(confParser.getAdds());
		out.close();
		
		logger.debug("Read conf file '{}' as xml file.", fileName);
		
		return out.toString();
	}
	
	/**
	 * @param file
	 * @param entry
	 * @return
	 * @throws IOException
	 */
	public String getZipFile(ZipFile file, ZipEntry entry) throws IOException {
		InputStreamReader in = new InputStreamReader( 
				file.getInputStream(entry));
		
		logger.debug("Read entry '{}' from module '{}'.", file.getName(), entry.getName());
		
		StringWriter out = new StringWriter();
		int c=0;
		char[] buf = new char[1024];
		while((c=in.read(buf))>-1)
			out.write(buf, 0, c);
		
		in.close();
		
		return out.toString();
	}
	
	/**
	 * @param file
	 * @param entry
	 * @return
	 * @throws IOException
	 */
	public String getZipXFile(ZipFile file, ZipEntry entry) throws IOException {
		
		BufferedInputStream in = new BufferedInputStream(
				file.getInputStream(entry));
		
		XMLParserWrapper p = new SimpleXMLParserWrapper();
		p.setXML(in);
		
		//Parse XML and write dotted notation
		//Use com.i3xx.ob.util.ctree.xml.ConfParser
		Document document = null;
		try {
			p.process();
			document = p.getDocument();
		} catch (SAXException e) {
			throw new IOException(e.toString());
		}
		ConfParser confParser = new ConfParser(root);
		confParser.addconf(document);
		
		StringWriter out = new StringWriter();
		NodeWriter writer = new NodeWriter(out);
		//((ILoggable)writer).addLogger(logger);
		writer.process(confParser.getAdds());
		out.close();
		
		logger.debug("Read xml entry '{}' from module '{}'.", file.getName(), entry.getName());
		
		return out.toString();
	}
	
	/**
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public int getFileType(ZipFile file, ZipEntry entry) throws IOException {
		
		byte[] buf = new byte[4];
		
		InputStream in = file.getInputStream(entry);
		in.read(buf);
		in.close();
		
		if( buf[0]==0x50 && buf[1]==0x4B && buf[2]==0x3 && buf[3]==0x4 ){
			//PK34: 50 4B 03 04
			return ZIP_FILE_TYPE;
		}else if( buf[0]==0x3C && buf[1]==0x3F ){
			//<?: 3C 3F
			return XML_FILE_TYPE;
		}else{
			return DEFAULT_FILE_TYPE;
		}
	}
	
	/**
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public int getFileType(String fileName) throws IOException {
		
		//Test file type
		RandomAccessFile f = new RandomAccessFile(fileName, "r");
		byte[] buf = new byte[4]; 
		f.read(buf);
		f.close();
		
		if( buf[0]==0x50 && buf[1]==0x4B && buf[2]==0x3 && buf[3]==0x4 ){
			//PK34: 50 4B 03 04
			return ZIP_FILE_TYPE;
		}else if( buf[0]==0x3C && buf[1]==0x3F ){
			//<?: 3C 3F
			return XML_FILE_TYPE;
		}else{
			return DEFAULT_FILE_TYPE;
		}
	}
}
