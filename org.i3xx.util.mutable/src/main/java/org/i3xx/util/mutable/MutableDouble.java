package org.i3xx.util.mutable;

@Mutable
public final class MutableDouble extends Number {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private double value;
	
	public MutableDouble() {
		value = 0.0;
	}
	
	public MutableDouble(double v) {
		value = v;
	}
	
	/**
	 * Sets the value
	 * 
	 * @param value
	 */
	public void doubleValue(double value) {
		this.value = value;
	}
	
	/**
	 * Increases the value
	 * 
	 * @param value
	 */
	public void incValue(double value) {
		this.value += value;
	}
	
	/**
	 * Decreases the value
	 * 
	 * @param value
	 */
	public void decValue(double value) {
		this.value -= value;
	}
	
	@Override
	public double doubleValue() {
		return value;
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
		return (long)value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof MutableDouble) {
		    return value == ((MutableDouble)obj).intValue();
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return Double.valueOf(value).hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return Double.toString(value);
	}
}
