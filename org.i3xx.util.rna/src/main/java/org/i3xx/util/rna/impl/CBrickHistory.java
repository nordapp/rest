/*
 * Created on 30.03.2005
 *
 */
package org.i3xx.util.rna.impl;

import org.i3xx.util.rna.core.IBrick;
import org.i3xx.util.rna.core.IBrickHistory;
import org.i3xx.util.rna.core.ITimeSlice;
import org.i3xx.util.rna.core.ITimeSliceIterator;


/**
 * �1. Eine Historie wird nur erstellt, wenn sich Daten �ndern. Link�nderungen (add, remove, etc.)
 * verursachen keine Historie.
 * 
 * �2. Damit ist sichergestellt, dass jede Historie eine eigene TransID hat.
 * �3. Ein Schl�ssel aus ID und TransID ist eindeutig.
 * 
 * @author MBollmann
 *
 */
public abstract class CBrickHistory extends CBrick implements ITimeSlice, IBrickHistory {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Variablen f�r ITimeSlice
	 */
	// G�ltigkeitsbeginndatum des Objektes das Ende ist implizit durch das Beginndatum der n�chsten Zeitscheibe gesetzt
	private String sValidityBeginning;
	// n�chste (sp�tere) Zeitscheibe auf der G�ltig-Von-Achse
	private ITimeSlice nextTimeSlice;
	// vorherige (fr�here) Zeitscheibe auf der G�ltig-Von-Achse
	private ITimeSlice prevTimeSlice;
	// N�chste (sp�tere) Zeitscheibe auf der �nderungsdatum-Achse 
	private ITimeSlice nextChangeTimeSlice;
	// vorherige (fr�here) Zeitscheibe auf der �nderungsdatum-Achse
	private ITimeSlice prevChangeTimeSlice;

	/*
	 *	Konstruktor
	 *
	 *	Der erste Adressraum 0-4294967295 bleibt internen Sonderadressen
	 *	vorbehalten.
	 *	0 - erstellt eine neue ID aus dem g�ltigen Adressraum
	 *	1 - kennzeichnet transiente, tempor�re Knoten
	 */
	public CBrickHistory(String name, String classname, Long id) {
		super(name, classname, id);
		sValidityBeginning = "";
	}

	/*
	 * �berschriebene Methoden
	 */
	public void clear() {
		super.clear();
        
		nextTimeSlice = null;
		prevTimeSlice = null;
		nextChangeTimeSlice = null;
		prevChangeTimeSlice = null;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrick#delete()
	 */
	public void delete() {
		super.delete();
		//TODO Historie ggf. Loeschen
        System.err.println("Achtung Historien werden nicht geloescht!!!!!");
	}

	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrick#remove()
	 */
	public void remove(){
		super.remove();
	}
	
	/**
	 *	Eigenschaft validityBeginning
	 */
	public void validityBeginning(String value){
		if( sValidityBeginning == null){
			updates().put("validitybeginning", new Object[]{sValidityBeginning, value});
			sValidityBeginning = value;
		}else if( ! sValidityBeginning.equals(value)){
			updates().put("validitybeginning", new Object[]{sValidityBeginning, value});
			sValidityBeginning = value;
		}else{
			//does nothing
		}
	}
	public String validityBeginning(){
		//da f�r alte Bricks vor Historienstart und Bricks ohne Felder in der updates Map
		//sValidityBeginning nicht gesetzt ist, wird das �nderungsdatum zur�ckgegeben
		if(sValidityBeginning == null || 
				sValidityBeginning.equals("") )
			return obtimestamp();
		
		return sValidityBeginning;
	}

	
	/**
	 * @return Returns the nextChangeTimeSlice.
	 */
	public ITimeSlice getNextChangeTimeSlice() {
		return nextChangeTimeSlice;
	}
	/**
	 * @param nextChangeTimeSlice The nextChangeTimeSlice to set.
	 */
	public void setNextChangeTimeSlice(ITimeSlice nextChangeTimeSlice) {
		this.nextChangeTimeSlice = nextChangeTimeSlice;
	}
	/**
	 * @return Returns the nextTimeSlice.
	 */
	public ITimeSlice getNextTimeSlice() {
		return nextTimeSlice;
	}
	/**
	 * @param nextTimeSlice The nextTimeSlice to set.
	 */
	public void setNextTimeSlice(ITimeSlice nextTimeSlice) {
		this.nextTimeSlice = nextTimeSlice;
	}
	/**
	 * @return Returns the prevChangeTimeSlice.
	 */
	public ITimeSlice getPrevChangeTimeSlice() {
		return prevChangeTimeSlice;
	}
	/**
	 * @param prevChangeTimeSlice The prevChangeTimeSlice to set.
	 */
	public void setPrevChangeTimeSlice(ITimeSlice prevChangeTimeSlice) {
		this.prevChangeTimeSlice = prevChangeTimeSlice;
	}
	/**
	 * @return Returns the prevTimeSlice.
	 */
	public ITimeSlice getPrevTimeSlice() {
		return prevTimeSlice;
	}
	/**
	 * @param prevTimeSlice The prevTimeSlice to set.
	 */
	public void setPrevTimeSlice(ITimeSlice prevTimeSlice) {
		this.prevTimeSlice = prevTimeSlice;
	}
	
	/**
	 * @author MBollmann
	 * Methoden aus ITimeSlice
	 */

	/**
	 * @see com.i3xx.ob.core.ITimeSlice#getTimeSliceChangeDate()
	 */
	public String getTimeSliceChangeDate() {
		return obtimestamp();
	}
	
	/**
	 * @see com.i3xx.ob.core.ITimeSlice#getTimeSliceValidityBeginning()
	 */
	public String getTimeSliceValidityBeginning() {
		return validityBeginning();
	}
	
	/**
	 * @see com.i3xx.ob.core.ITimeSlice#getTimeSliceIterator()
	 */
	public ITimeSliceIterator getTimeSliceIterator() {
		return new TimeSliceIterator(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@SuppressWarnings("unchecked")
	public Object clone() throws CloneNotSupportedException {
		
		CBrick clone = null;
		
		if(isFlag(IBrick.CLONE_FOR_HISTORY)){
			// Eine exakte Kopie dieses Objektes wird erstellt,
			avoidCloneChildren = true;
			clone = (CBrick)super.clone();
			
			// Die Verweise entsprechen denen des Originals
			clone.vector().addAll(this.vector());
			clone.proxies().addAll(this.proxies());
			clone.parent(parent());
			clone.setFlag(IBrick.HISTORY);
			removeFlag(IBrick.CLONE_FOR_HISTORY);
		}else{
			clone = (CBrick)super.clone();
		}
		
		return clone;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrick#cloneForHistorysation()
	 */
	/* Stefan Hauptmann 30.01.2007
	public Object cloneForHistorysation() throws CloneNotSupportedException {
		CBrickHistory clone=null;

		try {
			// Eine Kopie dieses Objektes wird erstellt,
			setFlag(IBrick.AVOID_CLONE_CHILDREN);
			setFlag(IBrick.RETAIN_CLONE_ID);
			clone = (CBrickHistory)super.clone();
			
			clone.vector().addAll(this.vector());
			clone.proxies().addAll(this.proxies());
			//
			clone.parent(parent());
		} catch (CloneNotSupportedException e) {
Log2.write("exception", new Log2Message("^^806", new Object[]{e}, Log2.ERROR));
			throw e;
		}
		return clone;
	}
	*/
}
