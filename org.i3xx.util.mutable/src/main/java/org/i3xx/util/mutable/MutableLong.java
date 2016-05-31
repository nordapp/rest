package org.i3xx.util.mutable;

@Mutable
public final class MutableLong extends Number {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long value;
	
	public MutableLong() {
		value = 0;
	}
	
	public MutableLong(long v) {
		value = v;
	}
	
	/**
	 * Sets the value
	 * 
	 * @param value
	 */
	public void longValue(long value) {
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
		return (int)value;
	}

	@Override
	public long longValue() {
		return value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof MutableLong) {
		    return value == ((MutableLong)obj).intValue();
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return Long.valueOf(value).hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return Long.toString(value);
	}
}
