package org.i3xx.util.mutable;

@Mutable
public final class MutableText {

	private String value;
	
	public MutableText() {
		value = "";
	}
	
	public MutableText(String v) {
		value = v;
	}
	
	/**
	 * @param value
	 */
	public void stringValue(String value) {
		this.value = value;
	}
	
	/**
	 * @return
	 */
	public String stringValue() {
		return value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj){
		return value.equals(obj);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return value.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return value.toString();
	}
}
