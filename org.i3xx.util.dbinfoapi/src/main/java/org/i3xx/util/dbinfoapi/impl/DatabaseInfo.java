package org.i3xx.util.dbinfoapi.impl;

import org.i3xx.util.dbinfoapi.core.IAttribute;
import org.i3xx.util.dbinfoapi.core.IDatabase;

public class DatabaseInfo extends AttributeInfo implements IDatabase, IAttribute {
	
	/** The name of the database in the configuration */
	private String db_name;
	
	/** The type of the database [ sql | js | java ] */
	private String db_type;
	
	/** The name of the language (null) e.g SQL case insensitive */
	private String lang_name;
	
	/** The version of the language (null) e.g ANSI SQL-92 case insensitive */
	private String lang_version;
	
	/** 
	 * The names of the collections, tables, resources
	 * where datasets can be read from the database (case sensitive).
	 */
	private String[] roots;
	
	/**
	 * @param dbName The name of the database
	 */
	public DatabaseInfo(String dbName) {
		super();
		
		this.db_name = dbName;
		this.db_type = null;
		this.lang_name = null;
		this.lang_version = null;
		this.roots = new String[0];
	}

	/**
	 * @return the db_name
	 */
	public String getDb_name() {
		return db_name;
	}

	/**
	 * @param db_name the db_name to set
	 */
	public void setDb_name(String db_name) {
		this.db_name = db_name;
	}

	/**
	 * @return the db_type
	 */
	public String getDb_type() {
		return db_type;
	}

	/**
	 * @param db_type the db_type to set
	 */
	public void setDb_type(String db_type) {
		this.db_type = db_type;
	}

	/**
	 * @return the lang_name
	 */
	public String getLang_name() {
		return lang_name;
	}

	/**
	 * @param lang_name the lang_name to set
	 */
	public void setLang_name(String lang_name) {
		this.lang_name = lang_name;
	}

	/**
	 * @return the lang_version
	 */
	public String getLang_version() {
		return lang_version;
	}

	/**
	 * @param lang_version the lang_version to set
	 */
	public void setLang_version(String lang_version) {
		this.lang_version = lang_version;
	}

	/**
	 * @return the roots
	 */
	public String[] getRoots() {
		return roots;
	}

	/**
	 * @param roots the roots to set
	 */
	public void setRoots(String[] roots) {
		this.roots = roots;
	}

}
