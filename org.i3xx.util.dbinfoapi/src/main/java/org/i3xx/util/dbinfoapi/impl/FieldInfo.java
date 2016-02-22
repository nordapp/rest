package org.i3xx.util.dbinfoapi.impl;

import org.i3xx.util.dbinfoapi.core.IAttribute;
import org.i3xx.util.dbinfoapi.core.IField;

public class FieldInfo extends AttributeInfo implements IField, IAttribute {

	/** The name of the field */
	private String field_name;
	
	/** The type definition of the field */
	private String field_type;
	
	public FieldInfo(String fieldName) {
		super();
		
		this.field_name = fieldName;
		this.field_type = null;
	}

	/**
	 * @return the field_name
	 */
	public String getField_name() {
		return field_name;
	}

	/**
	 * @param field_name the field_name to set
	 */
	public void setField_name(String field_name) {
		this.field_name = field_name;
	}

	/**
	 * @return the field_type
	 */
	public String getField_type() {
		return field_type;
	}

	/**
	 * @param field_type the field_type to set
	 */
	public void setField_type(String field_type) {
		this.field_type = field_type;
	}
}
