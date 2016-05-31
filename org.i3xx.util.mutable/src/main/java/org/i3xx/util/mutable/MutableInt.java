package org.i3xx.util.mutable;

@Mutable
public final class MutableInt extends Number {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int value;
	
	public MutableInt() {
		value = 0;
	}
	
	public MutableInt(int v) {
		value = v;
	}
	
	/**
	 * Sets the value
	 * 
	 * @param value
	 */
	public void intValue(int value) {
		this.value = value;
	}
	
	/**
	 * Increases the value
	 * 
	 * @param value
	 */
	public void incValue(int value) {
		this.value += value;
	}
	
	/**
	 * Decreases the value
	 * 
	 * @param value
	 */
	public void decValue(int value) {
		this.value -= value;
	}
	
	@Override
	public double doubleValue() {
		return (double)value;
	}

	@Override
	public float floatValue() {
		return (float)value;
	}

	@Override
	public int intValue() {
		return value;
	}

	@Override
	public long longValue() {
		return (long)value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof MutableInt) {
		    return value == ((MutableInt)obj).intValue();
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return Integer.toString(value);
	}
}
