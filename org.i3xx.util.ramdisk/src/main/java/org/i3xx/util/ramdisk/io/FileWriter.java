package org.i3xx.util.ramdisk.io;

import java.io.IOException;
import java.io.StringWriter;

import org.i3xx.util.ramdisk.File;

public class FileWriter extends StringWriter {

	protected File file;
	protected String encoding;
	
	public FileWriter(File file) {
		super();
		
		this.file = file;
		this.encoding = null;
	}
	
	public FileWriter(File file, String encoding) {
		super();
		
		this.file = file;
		this.encoding = encoding;
	}
	
	
	@Override
	public void close() throws IOException {
		super.close();
		
		if( ! file.exists())
			file.create();
		
		file.setContent( encoding==null ? 
				toString().getBytes() :
					toString().getBytes(encoding) );
	}
}
