package org.i3xx.util.client.io;

import java.io.IOException;
import java.io.OutputStream;

public interface OutputStreamHandler {
	
	/**
	 * @param resource
	 * @param out The output stream
	 */
	void stream(Resource resource, OutputStream out) throws IOException;

}
