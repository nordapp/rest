package org.i3xx.util.client.io;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamHandler {
	
	/**
	 * @param resource
	 * @param in The input stream
	 */
	void stream(Resource resource, InputStream in) throws IOException;

}
