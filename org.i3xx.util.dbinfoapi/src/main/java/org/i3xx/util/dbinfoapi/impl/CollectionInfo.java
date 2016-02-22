package org.i3xx.util.dbinfoapi.impl;

import org.i3xx.util.dbinfoapi.core.IAttribute;
import org.i3xx.util.dbinfoapi.core.ICollection;

public class CollectionInfo extends AttributeInfo implements ICollection, IAttribute {
	
	/** The name of the collection */
	private String col_name;
	
	/** The name of the type-definition */
	private String col_type;
	
	/** The size of the collection */
	private int size;
	
	/** The name of the fields */
	private String[] fields;
	
	public CollectionInfo(String colName) {
		super();
		
		this.col_name = colName;
		this.col_type = null;
		this.size = 0;
		this.fields = new String[0];
	}

	/**
	 * @return the col_name
	 */
	public String getCol_name() {
		return col_name;
	}

	/**
	 * @param col_name the col_name to set
	 */
	public void setCol_name(String col_name) {
		this.col_name = col_name;
	}

	/**
	 * @return the col_type
	 */
	public String getCol_type() {
		return col_type;
	}

	/**
	 * @param col_type the col_type to set
	 */
	public void setCol_type(String col_type) {
		this.col_type = col_type;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return the fields
	 */
	public String[] getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(String[] fields) {
		this.fields = fields;
	}

}
