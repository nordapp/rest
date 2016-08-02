package org.i3xx.util.client.io;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

public abstract class ResultImpl implements Result {
	
	private final int returnCode;
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob4.statistic.prj01.client.Resource.Result#getResult()
	 */
	public abstract Object getResult();
	
	/**
	 * 
	 */
	public ResultImpl() {
		returnCode = -1;
	}
	
	/**
	 * @param returnCode
	 */
	public ResultImpl(int returnCode) {
		this.returnCode = returnCode;
	}
	
	/**
	 * @return
	 */
	public boolean isNull() {
		return (getResult()==null);
	}
	
	/**
	 * @return
	 */
	public boolean isJson() {
		Object obj = getResult();
		return (obj instanceof JsonElement);
	}
	
	/**
	 * @return
	 */
	public boolean isBoolean() {
		Object obj = getResult();
		return (obj instanceof Boolean);
	}
	
	/**
	 * @return
	 */
	public boolean isNumber() {
		Object obj = getResult();
		return (obj instanceof Number);
	}
	
	/**
	 * @return
	 */
	public boolean isString() {
		Object obj = getResult();
		return (obj instanceof String);
	}
	
	/**
	 * @return
	 */
	public JsonElement toJson() {
		Object obj = getResult();
		if(obj instanceof JsonElement)
			return (JsonElement)obj;
		else if (obj instanceof String)
			return new JsonPrimitive((String)obj);
		else if (obj instanceof Number)
			return new JsonPrimitive((Number)obj);
				
		return obj==null ? JsonNull.INSTANCE : new JsonPrimitive(obj.toString());
	}
	
	/**
	 * @return
	 */
	public JsonResult toJsonResult() {
		return new JsonResultImpl(toJson(), getReturnCode());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		Object obj = getResult();
		return obj==null ? (String)obj : obj.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.i3xx.util.client.io.Result#getReturnCode()
	 */
	public int getReturnCode() {
		return returnCode;
	}
}
