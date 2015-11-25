package org.i3xx.util.core;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;

import org.i3xx.util.basic.io.CURL;
import org.xml.sax.InputSource;

/**
 *	StreamSource kann folgende Speichertypen annehmen:
 *	http: Datenspeicher ist ein Web-Server (nur lesen).
 *	file: Die Daten werden im Dateisystem gespeichert.
 *	text: Datenspeicher ist der �bergebene String (nur lesen).
 *	pipe: Pipes sind einfache serielle Datenspeicher. Sie werden
 *		  gef�llt und durch das Auslesen geleert. Nur benannte
 *		  Pipes sind persistent (pipe://Name).
 *
 */
public class StreamSource implements Serializable
{

	private static final long serialVersionUID = -5468807186714100744L;
	
	public final static int FILE_RESOURCE = 0;
	public final static int HTTP_RESOURCE = 1;
	public final static int TEXT_RESOURCE = 3;

	protected int resourcetype = 0;
	protected String resource = null;
	protected String systemId = null;
	protected String encoding = null;

	public StreamSource(String uri) throws IOException
	{
		this(uri, null, "UTF-8");
	}

	public StreamSource(String uri, String enc) throws IOException
	{
		this(uri, null, enc);
	}

	public StreamSource(String uri, String sys, String enc) throws IOException
	{
		if( uri==null )
			throw new IOException("Null value is no valid resource.");
		else if( uri.equals("") )
			throw new IOException("Empty value is no valid resource.");

		//Protocols
		//TODO add https support
		if( uri.startsWith("file:///") ){
			resourcetype = FILE_RESOURCE;
			try {
				resource = CURL.fileURLtoFilename(uri);
			} catch (URISyntaxException e) {
				throw new IOException(e);
			}
		}else if( uri.startsWith("http://") ){
			resourcetype = HTTP_RESOURCE;
			resource = uri;
		}else if( uri.startsWith("<") ){
			resourcetype = TEXT_RESOURCE;
			resource = uri;
		}else if( uri.startsWith("{") ){
			resourcetype = TEXT_RESOURCE;
			resource = uri;
		}else{
			throw new IOException(uri+" is no valid resource.");
		}

		encoding = enc;
		systemId = sys;
	}

	/**
	 * @return
	 */
	public int resourceType() {
		return resourcetype;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		if( resourcetype == HTTP_RESOURCE ){
			return resource;
		}else if( resourcetype == FILE_RESOURCE ){
			return resource;
		}else if( resourcetype == TEXT_RESOURCE ){
			return resource;
		}else{
			return "";
		}
	}

	/**
	 * @param uri
	 * @return
	 * @throws IOException
	 */
	public static StreamSource of(String uri) throws IOException
	{
		return new StreamSource(uri);
	}

	/**
	 * @param uri
	 * @param enc
	 * @return
	 * @throws IOException
	 */
	public static StreamSource of(String uri, String enc) throws IOException
	{
		return new StreamSource(uri, enc);
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public InputStream openInputStream() throws IOException
	{
		if( resourcetype == HTTP_RESOURCE ){
			URL url = new URL(resource);
			return ( url.openStream() );
		}else if( resourcetype == FILE_RESOURCE ){
			return ( new FileInputStream(resource) );
		}else if( resourcetype == TEXT_RESOURCE ){
			return ( new ByteArrayInputStream(
				resource.getBytes(encoding) ) );
		}else{
			throw new IOException("Unspecified resource type.");
		}
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public OutputStream openOutputStream() throws IOException
	{
		if( resourcetype == HTTP_RESOURCE ){
			throw new IOException("Ressource "+resource+" is read only.");
		}else if( resourcetype == FILE_RESOURCE ){
			return ( new FileOutputStream(resource) );
		}else if( resourcetype == TEXT_RESOURCE ){
			throw new IOException("Ressource "+resource+" is read only.");
		}else{
			throw new IOException("Unspecified resource type.");
		}
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public Reader openReader() throws IOException
	{
		if( resourcetype == TEXT_RESOURCE ){
			return ( new StringReader(resource) );
		}else{
			return ( new InputStreamReader( openInputStream(), encoding ) );
		}
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public Writer openWriter() throws IOException
	{
		if( resourcetype == TEXT_RESOURCE ){
			return ( new TextWriter() );
		}else{
			return ( new OutputStreamWriter( openOutputStream(), encoding ) );
		}
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public File getFile() throws IOException
	{
		if( resourcetype == HTTP_RESOURCE ){
			throw new IOException(
				"No java.io.File object available for resource "+resource);
		}else if( resourcetype == FILE_RESOURCE ){
			return ( new File(resource) );
		}else if( resourcetype == TEXT_RESOURCE ){
			throw new IOException(
				"No java.io.File object available for resource "+resource);
		}else{
			throw new IOException("Unspecified resource type.");
		}
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public InputSource getInputSource() throws IOException
	{
		if( resourcetype == HTTP_RESOURCE ){
			InputSource res = new InputSource();
			res.setByteStream( openInputStream() );
			res.setSystemId( systemId!=null ? systemId : resource );
			res.setEncoding( encoding );

			return res;
		}else if( resourcetype == FILE_RESOURCE ){
			InputSource res = new InputSource();
			res.setByteStream( openInputStream() );
			res.setSystemId( systemId!=null ? systemId : resource );
			res.setEncoding( encoding );

			return res;
		}else if( resourcetype == TEXT_RESOURCE ){
			InputSource res = new InputSource();
			res.setCharacterStream( openReader() );
			res.setSystemId( systemId );
			res.setEncoding( encoding );

			return res;
		}else{
			throw new IOException("Unspecified resource type.");
		}
	}

	//----------------------------------
	// Writer f�r TEXT_RESSOURCE
	//----------------------------------
	
	private class TextWriter extends StringWriter
	{
		public TextWriter() {
			super();
		}
		
		public void close() throws IOException {
			super.close();
			
			resource = toString();
		}
	}
}
