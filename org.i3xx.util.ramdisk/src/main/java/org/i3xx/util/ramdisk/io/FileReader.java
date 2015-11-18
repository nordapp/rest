package org.i3xx.util.ramdisk.io;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import org.i3xx.util.ramdisk.File;

public class FileReader extends StringReader {

	public FileReader(File file) {
		super( new String(file.getContent()) );
	}

	public FileReader(File file, String encoding) throws UnsupportedEncodingException {
		super( new String(file.getContent(), encoding) );
	}
}
