package org.i3xx.util.rna.engine;

import java.util.List;

public interface ISetLinks {

	/**
	 * @return The link list
	 */
	List<LinkInfo> getList();
	
	/**
	 * Adds a new Link
	 * 
	 * @param info
	 */
	void add(LinkInfo info);
}
