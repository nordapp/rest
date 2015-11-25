package org.i3xx.util.rna.core;

/**
 * Used to set the validity beginning by intern processing.
 * Use the ITimeSlice interface to handle the history.
 * 
 * @author Stefan
 * @since 13.04.2012
 */
public interface IBrickHistory {

	/**
	 * @return
	 */
	String validityBeginning();
	
	/**
	 * @param value the new validity beginning
	 */
	void validityBeginning(String value);
	
	/**
	 * @param nextTimeSlice The nextTimeSlice to set.
	 */
	void setNextTimeSlice(ITimeSlice nextTimeSlice);
	
	/**
	 * @param prevTimeSlice The prevTimeSlice to set.
	 */
	void setPrevTimeSlice(ITimeSlice prevTimeSlice);
	
	/**
	 * @param nextChangeTimeSlice The nextChangeTimeSlice to set.
	 */
	void setNextChangeTimeSlice(ITimeSlice nextChangeTimeSlice);
	
	/**
	 * @param prevChangeTimeSlice The prevChangeTimeSlice to set.
	 */
	void setPrevChangeTimeSlice(ITimeSlice prevChangeTimeSlice);
}
