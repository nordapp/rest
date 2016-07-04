package org.i3xx.util.client.wk;

public class ResultImpl implements Result {
	
	/**  */
	private final Object elem;
	
	public ResultImpl(Object elem) {
		this.elem = elem;
	}
	
	/**  */
	public boolean isNull(){ return elem==null; }
	
	/**  */
	public boolean isNumber(){ return elem instanceof Number; }
	
	/**  */
	public boolean isString(){ return elem instanceof String; }
	
	/**  */
	public Object getResult() {	return elem; }
	
	/**  */
	public String getAsString() { return elem==null ? null : elem.toString(); }
	
	/**  */
	public long getAsLong() { return elem==null ? 0l : elem instanceof Number ? ((Number)elem).longValue() : 0l; }
	
	/**  */
	public int getAsInt() { return elem==null ? 0 : elem instanceof Number ? ((Number)elem).intValue() : 0; }
	
	/**  */
	public double getAsDouble() { return elem==null ? 0d : elem instanceof Number ? ((Number)elem).doubleValue() : 0d; }
	
	/**  */
	public float getAsFloat() { return elem==null ? 0f : elem instanceof Number ? ((Number)elem).floatValue() : 0f; }
	
	/**  */
	public boolean getAsBoolean() { return elem==null ? false : elem instanceof Boolean ? ((Boolean)elem).booleanValue() : false; }

}
