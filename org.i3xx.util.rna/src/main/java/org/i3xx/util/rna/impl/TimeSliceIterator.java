/*
 * Created on 23.03.2005
 *
 */
package org.i3xx.util.rna.impl;

import java.io.Serializable;

import org.i3xx.util.rna.core.IBrick;
import org.i3xx.util.rna.core.ITimeSliceIterator;

/**
 * @author MBollmann
 * TimeSliceIterator, dient zum iterieren �ber die Zeitscheiben (TimeSlice)
 */
public class TimeSliceIterator implements ITimeSliceIterator, Serializable {
	private static final long serialVersionUID = 1L;
	protected CData actual;
	
	/**
	 * Legt den Startpunkt f�r den Iterator fest
	 * @param timeSliceStart IBrick, von dem die Historie angezeigt werden soll
	 */
	public TimeSliceIterator(IBrick timeSliceStart) {
		super();
		if (timeSliceStart instanceof CData)
			actual = (CData) timeSliceStart;
		else
			actual = null;
	}
	
	/**
	 * Liefert den heute g�ltigen TimeSlice zur�ck
	 * Der Iterator wird auf diesem Element positioniert
	 * @return TimeSlice der heute g�ltig ist
	 * 			wenn keiner gefunden wurde null
	 */
	public IBrick getActualTimeSlice() {
		String today = String.valueOf( System.currentTimeMillis() );
		
		return getTimeSlice(today);
	}
	
	/**
	 * Liefert den TimeSlice der zu dem �bergebenen Datumsstring g�ltig ist
	 * Der Iterator wird auf diesem Element positioniert
	 * @param date Datum f�r G�ltigkeitspr�fung
	 * @return TimeSlice der zum �bergebenen Datum g�ltig ist
	 * 			wenn keiner gefunden wurde null;
	 */
	public IBrick getTimeSlice(String date) {
		IBrick result = null; 
		
		if (date==null)
			return result;
		
		if (actual==null)
			return result;

		int i = compareTimestamps( actual.getTimeSliceValidityBeginning(), date ); 
		
		if (i==0)
			result = actual;
		else
			if (i<0) {
				//Aktuelles G�ltigkeitsvondatum f�ngt vor dem Zugriffsdatum an
				if (actual.getNextTimeSlice()==null)
					//Es gibt aber kein n�chsten, also Ende
					result = actual;
				else {
					// F�ngt der n�chste auch fr�her an?
					if ( compareTimestamps(actual.getNextTimeSlice().getTimeSliceValidityBeginning(), date ) < 1 ) {
						//N�chste f�ngt auch fr�her an
						if (actual.getNextTimeSlice() instanceof CData) {
							actual = (CData) actual.getNextTimeSlice();
							result = getTimeSlice(date);
						}
					}
					else
						result = actual;
				}
			}
			else {
				//Aktuelles G�ltigkeitsdatum f�ngt nach dem Zugriffsdatum an
				if (actual.getPrevTimeSlice()!=null) {
					if (actual.getPrevTimeSlice() instanceof CData) {
						actual = (CData) actual.getPrevTimeSlice();
						result = getTimeSlice(date);
					}
				}
				
			}
		
		return result;
	}
	
	/**
	 * Liefert die fr�heste TimeSlice
	 * Der Iterator wird auf diesem Element positioniert
	 * @return Fr�heste TimeSlice
	 */
	public IBrick getEarliestTimeSlice() {
		IBrick result = null;
		boolean end = false;
		
		if (actual==null)
			return result;
		
		end = actual.getPrevTimeSlice() == null;

		while (!end) {
			if (actual.getPrevTimeSlice() instanceof CData)
				actual = (CData) actual.getPrevTimeSlice();
			else
				end = true;
			end |= actual.getPrevTimeSlice() == null;
		}

		if (actual.getPrevTimeSlice() == null)
			result = actual;
		
		return result;
	}
	
	/**
	 * Liefert die sp�testeste TimeSlice 
	 * Der Iterator wird auf diesem Element positioniert
	 * @return sp�teste TimeSlice
	 */
	public IBrick getLatestTimeSlice() {
		IBrick result = null;
		boolean end = false;
		
		if (actual==null)
			return result;
		
		end = actual.getNextTimeSlice() == null;

		while (!end) {
			if (actual.getNextTimeSlice() instanceof CData)
				actual = (CData) actual.getNextTimeSlice();
			else
				end = true;
			end |= actual.getNextTimeSlice() == null;
		}
		
		if (actual.getNextTimeSlice() == null)
			result = actual;

		return result;
	}
	
	/**
	 * Pr�ft ob es eine weitere, sp�tere Zeitscheibe gibt
	 * @return True, wenn es noch eine sp�tere Zeitscheibe gibt
	 * 			False, sonst
	 */
	public boolean hasNextTimeSlice() {
		boolean result = false;
		
		if (actual==null)
			return result;
		
		result = actual.getNextTimeSlice() != null;

		return result;
	}
	
	/**
	 * Liefert die n�chste, sp�tere Zeitscheibe.
	 * Wenn es keine weitere mehr gibt, wird null zur�ckgegeben
	 * Der Iterator wird auf dem Ergebniselement positioniert
	 * @return n�chste, sp�tere Zeitscheibe
	 */
	public IBrick nextTimeSlice() {
		IBrick result = null;
		
		if (actual==null)
			return result;
		
		if (actual.getNextTimeSlice() instanceof CData) {
			actual = (CData) actual.getNextTimeSlice();
			result = actual;
		}

		return result;
	}

	/**
	 * Pr�ft, ob es eine fr�here Zeitscheibe gibt
	 * @return True, wenn es eine fr�here Zeitscheibe gibt
	 * 			False, sonst
	 */
	public boolean hasPrevTimeSlice() {
		boolean result = false;
		
		if (actual==null)
			return result;
		
		result = actual.getPrevTimeSlice() != null;

		return result;
	}
	
	/**
	 * Liefert die n�chste, fr�here Zeitscheibe.
	 * Wenn es keine weitere mehr gibt, wird null zur�ckgegeben
	 * Der Iterator wird auf dem Ergebniselement positioniert
	 * @return n�chste, fr�here Zeitscheibe
	 */
	public IBrick prevTimeSlice() {
		IBrick result = null;
		
		if (actual==null)
			return result;
		
		if (actual.getPrevTimeSlice() instanceof CData) {
			actual = (CData) actual.getPrevTimeSlice();
			result = actual;
		}

		return result;
	}
	
	/**
	 * Pr�ft, ob es zu der aktuellen Zeitscheibe eine gleiche Zeitscheibe gibt, die
	 * sp�ter gilt
	 * @return True, wenn es zur der aktuellen Zeitscheibe, eine gleiche Zeitscheibe
	 * 		    gibt die sp�ter gilt
	 * 		   False, sonst
	 */
	public boolean hasNextChange() {
		boolean result = false;
		
		if (actual==null)
			return result;
		
		result = actual.getNextChangeTimeSlice() != null;
		
		return result;
	}
	
	/**
	 * Liefert zu der aktuellen Zeitscheibe eine gleiche Zeitscheibe, die sp�ter gilt
	 * @return
	 */
	public IBrick nextChange() {
		IBrick result = null;
		
		if (actual==null)
			return result;
		
		if (actual.getNextChangeTimeSlice() instanceof CData) {
			actual = (CData) actual.getNextChangeTimeSlice();
			result = actual;
		}

		return result;
	}
	
	/**
	 * Pr�ft, ob es zu der aktuellen Zeitscheibe eine gleiche Zeitscheibe gibt, die
	 * fr�her gilt
	 * @return True, wenn es zur der aktuellen Zeitscheibe, eine gleiche Zeitscheibe
	 * 		    gibt die fr�her gilt
	 * 		   False, sonst
	 */
	public boolean hasPrevChange() {
		boolean result = false;
		
		if (actual==null)
			return result;
		
		result = actual.getPrevChangeTimeSlice() != null;
		
		return result;
	}
	
	/**
	 * Liefert zu der aktuellen Zeitscheibe eine gleiche Zeitscheibe, die fr�her gilt
	 * @return
	 */
	public IBrick prevChange() {
		IBrick result = null;
		
		if (actual==null)
			return result;
		
		if (actual.getPrevChangeTimeSlice() instanceof CData) {
			actual = (CData) actual.getPrevChangeTimeSlice();
			result = actual;
		}

		return result;
	}


	/**
	 * Vergleicht Timestamp a mit Timestamp b
	 * @param a Timestamp als String
	 * @param b Timestamp als String
	 * @return
	 * 0, wenn a = b
	 * 1, wenn a > b
	 * -1, wenn a < b
	 */
	protected int compareTimestamps(String a, String b) {
		int result = 0;
		
		if ( ( a == null ) && ( b == null ) )
			return 0;
		
		if ( a == null )
			return 1;
		
		if ( b == null )
			return -1;
		
		long la = Long.parseLong( a );
		long lb = Long.parseLong( b );
		
		if ( la > lb )
			result = 1;
		else
		if ( lb > la )
			result = -1;

		return result;
	}
}
