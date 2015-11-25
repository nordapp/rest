package com.i3xx.util.ctree.core;

import com.i3xx.util.ctree.IConfNode;

public interface IUpdateListener {

	/**
	 * Listen to the update of the configuration
	 * 
	 * @param oldValue The oldValue
	 * @param node The node with the new value
	 */
	void update(String oldValue, IConfNode node);
}
