package com.i3xx.util.ctree.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipReader extends AbstractLineReader {
	
	private String fileName; 
	private String entryName;
	
	public ZipReader(String fileName, String entryName) {
		super();
		
		this.in = null;
		this.fileName = fileName;
		this.entryName = entryName;
		this.line = null;
	}
	
	@Override
	public boolean available() throws IOException {
		if(in==null){
			ZipFile file = new ZipFile(fileName);
			ZipEntry entry = file.getEntry(entryName);
			in = new BufferedReader( new InputStreamReader( 
					file.getInputStream(entry)));
		}
		return super.available();
	}
}
