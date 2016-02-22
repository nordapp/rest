package org.i3xx.util.dbinfoapi.core;

public interface ICollection extends IAttribute {

	/**
	 * @return the col_name
	 */
	String getCol_name();

	/**
	 * @param col_name the col_name to set
	 */
	void setCol_name(String col_name);

	/**
	 * @return the col_type
	 */
	String getCol_type();

	/**
	 * @param col_type the col_type to set
	 */
	void setCol_type(String col_type);

	/**
	 * @return the size
	 */
	int getSize();

	/**
	 * @param size the size to set
	 */
	void setSize(int size);

	/**
	 * @return the fields
	 */
	String[] getFields();

	/**
	 * @param fields the fields to set
	 */
	void setFields(String[] fields);

}
