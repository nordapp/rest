package org.i3xx.util.factory;

import java.net.InetAddress;
import java.util.Properties;

import org.i3xx.util.ctree.IConfNode;

public interface AccessConfService {

	/**
	 * Reads the configuration using a cloud configuration service
	 * 
	 * @param addr The address of the service
	 * @param port The port the service uses
	 * @param props The properties or null if there are no properties
	 * @return
	 */
	IConfNode read(InetAddress addr, int port, Properties props);
}
