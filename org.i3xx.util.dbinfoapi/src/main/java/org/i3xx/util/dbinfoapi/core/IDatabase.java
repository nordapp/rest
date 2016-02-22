package org.i3xx.util.dbinfoapi.core;

public interface IDatabase extends IAttribute {

	/**
	 * @return the db_name
	 */
	String getDb_name();

	/**
	 * @param db_name the db_name to set
	 */
	void setDb_name(String db_name);

	/**
	 * @return the db_type
	 */
	String getDb_type();

	/**
	 * @param db_type the db_type to set
	 */
	void setDb_type(String db_type);

	/**
	 * @return the lang_name
	 */
	String getLang_name();

	/**
	 * @param lang_name the lang_name to set
	 */
	void setLang_name(String lang_name);

	/**
	 * @return the lang_version
	 */
	String getLang_version();

	/**
	 * @param lang_version the lang_version to set
	 */
	void setLang_version(String lang_version);

	/**
	 * @return the roots
	 */
	String[] getRoots();

	/**
	 * @param roots the roots to set
	 */
	void setRoots(String[] roots);

}
