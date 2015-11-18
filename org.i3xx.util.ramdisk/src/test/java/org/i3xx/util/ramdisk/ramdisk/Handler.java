package org.i3xx.util.ramdisk.ramdisk;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.i3xx.util.ramdisk.RamdiskImplURLConnection;

public class Handler extends URLStreamHandler {

	protected URLConnection openConnection(URL u) throws IOException {
		return new RamdiskImplURLConnection(u);
	}
}
