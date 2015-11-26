package org.i3xx.util.rna.engine;

import org.i3xx.util.rna.core.IBrick;
import org.i3xx.util.rna.core.QueryException;

public interface ILinkHandler {
	
	/**
	 * Returns a brick searched by it's id
	 * 
	 * @param id The id of the brick
	 * @return
	 */
	IBrick get(long id) throws QueryException;
	
	/**
	 * Returns a brick searched by it's id and transid
	 * 
	 * @param id
	 * @param transid
	 * @return
	 */
	IBrick get(long id, long transid) throws QueryException;
}
