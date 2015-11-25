package com.i3xx.util.ctree.parser;

import java.io.BufferedReader;
import java.io.IOException;

public class FileReader extends AbstractLineReader {
	
	private String fileName;
	
	/**
	 * @param in The reader to read from
	 * @throws IOException 
	 */
	public FileReader(String fileName) throws IOException {
		super();
		
		this.fileName = fileName;
	}
	
	/**
	 * @return true if a line is available to be read
	 * @throws IOException 
	 */
	public boolean available() throws IOException {
		if(in==null){
			in = new BufferedReader(new java.io.FileReader(fileName));
		}
		return super.available();
	}
}
