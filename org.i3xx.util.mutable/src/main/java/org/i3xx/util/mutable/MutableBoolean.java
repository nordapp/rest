package org.i3xx.util.mutable;

@Mutable
public final class MutableBoolean {
	
	private boolean value;
	
	public MutableBoolean() {
		value = false;
	}
	
	public MutableBoolean(boolean v) {
		value = v;
	}
	
	public void booleanValue(boolean v) {
		value = v;
	}
	
	public boolean booleanValue() {
		return value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof MutableBoolean) {
		    return value == ((MutableBoolean)obj).booleanValue();
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return value ? 2459 : 2467;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return Boolean.toString(value);
	}
}
