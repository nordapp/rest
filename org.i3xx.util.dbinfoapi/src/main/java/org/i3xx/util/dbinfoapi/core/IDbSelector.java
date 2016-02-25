package org.i3xx.util.dbinfoapi.core;

import java.util.Properties;

public interface IDbSelector {

	/**
	 * Select the database by a search using the properties.
	 * 
	 * The database selector usually depends on the implementation. The
	 * simplest case accept null as an argument and returns the single
	 * database information.
	 * 
	 * Other implementations may use algorithms to determine which
	 * database is the best to use. This may depend on the load, the
	 * actuality of the data or other parameter of the environment.
	 * 
	 * @param properties The properties to access the database
	 * @return One or more databases matching the properties.
	 */
	IDatabase[] findDatabase(Properties properties);
}
