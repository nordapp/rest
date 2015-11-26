package org.i3xx.util.rna.engine;

import java.util.Iterator;

import org.i3xx.util.rna.core.IBrick;

/**
 * The interface of a generic factory to produce IBricks
 * 
 * @author Stefan
 *
 */
public interface IDbQuery {

	/**
	 * Pick one IBrick from the data source
	 * 
	 * @param stmt The query statement
	 * @return
	 */
	IBrick pick(String stmt);
	
	/**
	 * Select a collection of IBricks from the data source and
	 * return the iterator on the collection.
	 * 
	 * @param stmt The query statement
	 * @return
	 */
	Iterator<IBrick> select(String stmt);
	
}
