package org.i3xx.util.rna.core;

public interface ITimeSliceIterator {

	/**
	 * Liefert den heute g�ltigen TimeSlice zur�ck
	 * Der Iterator wird auf diesem Element positioniert
	 * @return TimeSlice der heute g�ltig ist
	 * 			wenn keiner gefunden wurde null
	 */
	IBrick getActualTimeSlice();
	
	/**
	 * Liefert den TimeSlice der zu dem �bergebenen Datumsstring g�ltig ist
	 * Der Iterator wird auf diesem Element positioniert
	 * @param date Datum f�r G�ltigkeitspr�fung
	 * @return TimeSlice der zum �bergebenen Datum g�ltig ist
	 * 			wenn keiner gefunden wurde null;
	 */
	IBrick getTimeSlice(String date);
	
	/**
	 * Liefert die fr�heste TimeSlice
	 * Der Iterator wird auf diesem Element positioniert
	 * @return Fr�heste TimeSlice
	 */
	IBrick getEarliestTimeSlice();
	
	/**
	 * Liefert die sp�testeste TimeSlice 
	 * Der Iterator wird auf diesem Element positioniert
	 * @return sp�teste TimeSlice
	 */
	IBrick getLatestTimeSlice();
	
	/**
	 * Pr�ft ob es eine weitere, sp�tere Zeitscheibe gibt
	 * @return True, wenn es noch eine sp�tere Zeitscheibe gibt
	 * 			False, sonst
	 */
	boolean hasNextTimeSlice();
	
	/**
	 * Liefert die n�chste, sp�tere Zeitscheibe.
	 * Wenn es keine weitere mehr gibt, wird null zur�ckgegeben
	 * Der Iterator wird auf dem Ergebniselement positioniert
	 * @return n�chste, sp�tere Zeitscheibe
	 */
	IBrick nextTimeSlice();
	
	/**
	 * Pr�ft, ob es eine fr�here Zeitscheibe gibt
	 * @return True, wenn es eine fr�here Zeitscheibe gibt
	 * 			False, sonst
	 */
	boolean hasPrevTimeSlice();
	
	/**
	 * Liefert die n�chste, fr�here Zeitscheibe.
	 * Wenn es keine weitere mehr gibt, wird null zur�ckgegeben
	 * Der Iterator wird auf dem Ergebniselement positioniert
	 * @return n�chste, fr�here Zeitscheibe
	 */
	IBrick prevTimeSlice();
	
	/**
	 * Pr�ft, ob es zu der aktuellen Zeitscheibe eine gleiche Zeitscheibe gibt, die
	 * sp�ter gilt
	 * @return True, wenn es zur der aktuellen Zeitscheibe, eine gleiche Zeitscheibe
	 * 		    gibt die sp�ter gilt
	 * 		   False, sonst
	 */
	boolean hasNextChange();
	
	/**
	 * Liefert zu der aktuellen Zeitscheibe eine gleiche Zeitscheibe, die sp�ter gilt
	 * @return
	 */
	IBrick nextChange();
	
	/**
	 * Pr�ft, ob es zu der aktuellen Zeitscheibe eine gleiche Zeitscheibe gibt, die
	 * fr�her gilt
	 * @return True, wenn es zur der aktuellen Zeitscheibe, eine gleiche Zeitscheibe
	 * 		    gibt die fr�her gilt
	 * 		   False, sonst
	 */
	boolean hasPrevChange();
	
	/**
	 * Liefert zu der aktuellen Zeitscheibe eine gleiche Zeitscheibe, die fr�her gilt
	 * @return
	 */
	IBrick prevChange();
}
