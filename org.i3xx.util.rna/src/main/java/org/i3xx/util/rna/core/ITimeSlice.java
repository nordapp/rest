/*
 * Created on 23.03.2005
 *
 */
package org.i3xx.util.rna.core;



/**
 * @author MBollmann
 * ITimeSlice, Interface f�r die Zeitscheibenverwaltung
 * 
 * Die Zeitscheiben werden mit Hilfe eines zweidimensionalen Konzeptes verwaltet
 * Auf der einen Achse ist der Zeitstrahl. Auf ihr werden die G�ltig-Von-Dat�mer
 * verwaltet.
 * Auf der zweiten Achse gibt es einen Zeitstrahl �ber das �nderungsdatum f�r ein
 * G�ltig-Von-Datum.
 * 
 *    01.01.04           01.03.04       01.10.04
 * -------+---------------------------------------------> G�ltig-Von-Achse
 *        | 01.10.03     01.03.04       01.10.04
 *        | 01.05.03
 * �nderungsdatum-Achse
 *    
 */
public interface ITimeSlice {
	
	/**
	 * Liefert einen Iterator �ber die Zeitscheiben
	 * @return
	 */
	ITimeSliceIterator getTimeSliceIterator();
	
	/**
	 * Liefert das Datum, ab dem die Zeitscheibe gilt
	 * @return G�ltigkeitsbeginn der Zeitscheibe
	 */
	String getTimeSliceValidityBeginning();
	
	/**
	 * Liefert das Datum an dem die Zeitscheibe angelegt wurde
	 * @return Anlagedatum der Zeitscheibe
	 */
	String getTimeSliceChangeDate();
	
	/**
	 * Returns a Clone for use in Historysation
	 * Removed by Stefan Hauptmann 30.01.2007
	 */
	//Object cloneForHistorysation() throws CloneNotSupportedException;
	
	/**
	 * Deletes all Data from this Brick
	 * Used to Destroy unused Clone in Historysation
	 */
	void clear();
	
	/**
	 * @return Returns the nextTimeSlice.
	 */
	ITimeSlice getNextTimeSlice();
	
	/**
	 * @return Returns the prevChangeTimeSlice.
	 */
	ITimeSlice getPrevTimeSlice();
	
	/**
	 * @return Returns the nextChangeTimeSlice.
	 */
	ITimeSlice getNextChangeTimeSlice();
	
	/**
	 * @param nextTimeSlice The nextTimeSlice to set.
	 */
	ITimeSlice getPrevChangeTimeSlice();
}
