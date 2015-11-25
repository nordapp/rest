package com.i3xx.util.ctree.parser;

import java.io.BufferedReader;
import java.io.IOException;

public class AbstractLineReader extends AbstractReader {

	protected String line;
	protected BufferedReader in;
	
	public AbstractLineReader() {
		super();
		
		this.in = null;
		this.line = null;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.parser.AbstractReader#available()
	 */
	public boolean available() throws IOException {
		if(line==null){
			line = this.in.readLine();
		}
		return line!=null;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.parser.AbstractReader#readNexte()
	 */
	public String readNext() throws IOException {
		if(line==null){
			line = this.in.readLine();
		}
		String resl = line;
		line = null;
		return resl;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.parser.AbstractReader#close()
	 */
	public void close() throws IOException {
		if(in!=null)
			in.close();
	}
}
