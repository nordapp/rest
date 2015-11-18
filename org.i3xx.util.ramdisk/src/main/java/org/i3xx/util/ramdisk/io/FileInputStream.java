package org.i3xx.util.ramdisk.io;

import java.io.ByteArrayInputStream;

import org.i3xx.util.ramdisk.File;

public class FileInputStream extends ByteArrayInputStream {

	public FileInputStream(File file) {
		super(file.getContent());
	}
}
