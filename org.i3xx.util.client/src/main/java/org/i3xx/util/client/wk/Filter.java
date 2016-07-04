package org.i3xx.util.client.wk;

public interface Filter {

	/**
	 * @param connection
	 * @return
	 */
	boolean match(Process connection);
}
