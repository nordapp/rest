package org.i3xx.util.ramdisk.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.i3xx.util.ramdisk.File;

public class FileOutputStream extends ByteArrayOutputStream {
	
	protected File file;
	
	public FileOutputStream(File file) {
		super();
		
		this.file = file;
	}
	
	@Override
	public void close() throws IOException {
		super.close();
		
		if( ! file.exists())
			file.create();
		
		file.setContent(toByteArray());
	}
}
