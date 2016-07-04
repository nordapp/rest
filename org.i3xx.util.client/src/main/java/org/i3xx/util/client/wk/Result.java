package org.i3xx.util.client.wk;

public interface Result {
	
	/**  */
	Object getResult();
	
	/**  */
	boolean isNull();
	
	/**  */
	boolean isNumber();
	
	/**  */
	boolean isString();
	
	/**  */
	String getAsString();
	
	/**  */
	long getAsLong();
	
	/**  */
	int getAsInt();
	
	/**  */
	double getAsDouble();
	
	/**  */
	float getAsFloat();
	
	/**  */
	boolean getAsBoolean();

}
