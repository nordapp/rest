package org.i3xx.util.core.xml;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class XMLWriter extends DOMWriter {

	private StringWriter out = new StringWriter();

	public XMLWriter() throws UnsupportedEncodingException {
		super(false);
		super.fOut = new PrintWriter(out);
	}

	public XMLWriter(String encoding) throws UnsupportedEncodingException {
		super(false);
		super.sEncoding = encoding;
		super.fOut = new PrintWriter(out);
	}

	public void getXML(Writer out){
		super.setOutput(out);
	}

	public void getXML(OutputStream out) throws UnsupportedEncodingException {
		super.setOutput(out, sEncoding);
	}

	public String getXML(){
		return out.toString();
	}
}
