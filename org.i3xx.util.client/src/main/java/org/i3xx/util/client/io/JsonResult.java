package org.i3xx.util.client.io;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface JsonResult extends Result {
	
	/**
	 * @param path
	 * @return
	 */
	JsonResult get(String path);
	
	/**
	 * @param path
	 * @return
	 */
	boolean match(String path);
	
	/**
	 * @param name
	 * @return
	 */
	String[] find(String name);
	
	/**
	 * @return
	 */
	boolean isDecimal();
	
	/**  */
	public String getAsString();
	
	/**  */
	public boolean getAsBoolean();
	
	/**  */
	public BigInteger getAsBigInteger();
	
	/**  */
	public long getAsLong();
	
	/**  */
	public int getAsInt();
	
	/**  */
	public BigDecimal getAsBigDecimal();
	
	/**  */
	public double getAsDouble();
	
	/**  */
	public float getAsFloat();

}
