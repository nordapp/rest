package org.i3xx.util.client.wk;

import java.io.IOException;

public interface Action {

	/**
	 * @param connection
	 * @return
	 * @throws IOException
	 */
	Object run(Process connection) throws IOException;
}
