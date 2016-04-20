package org.i3xx.util.ctree.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class LineReader extends AbstractLineReader {
	
	/**
	 * @param in The reader to read from
	 * @throws IOException 
	 */
	public LineReader(Reader in) throws IOException {
		super();
		
		this.in = new BufferedReader(in);
	}
}
