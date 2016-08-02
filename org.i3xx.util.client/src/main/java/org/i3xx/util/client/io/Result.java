package org.i3xx.util.client.io;

import com.google.gson.JsonElement;

public interface Result {
	
	/**
	 * @return
	 */
	boolean isNull();
	
	/**
	 * @return
	 */
	boolean isJson();
	
	/**
	 * @return
	 */
	boolean isBoolean();
	
	/**
	 * @return
	 */
	boolean isNumber();
	
	/**
	 * @return
	 */
	boolean isString();
	
	/**
	 * @return
	 */
	Object getResult();
	
	/**
	 * @return
	 */
	JsonElement toJson();
	
	/**
	 * @return
	 */
	JsonResult toJsonResult();

	/**
	 * @return
	 */
	int getReturnCode();
}
