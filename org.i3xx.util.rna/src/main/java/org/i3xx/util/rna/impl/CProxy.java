package org.i3xx.util.rna.impl;

import org.i3xx.util.rna.core.CCommand;
import org.i3xx.util.rna.core.IBrick;
import org.i3xx.util.rna.core.IBrickData;
import org.i3xx.util.rna.core.IProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 *	CProxy ist ein Stellvertreterobjekt f�r einen querverbundenen Knoten. In- und externe
 *	Iteratoren d�rfen den durch das Proxy referenzierten Knoten nicht �berlaufen. Methoden
 *	wie hide() und show() sind ebenfalls nicht durchg�ngig.
 *
 *	Referenzierte Objekte m�ssen von der Referenz keine Kenntnis besitzen, da die Datenbank
 *	explizit �ber persistentGC bereinigt wird, und somit nur Objekte entfernt werden, die
 *	keine Referenzen mehr besitzen. Ein referenziertes Objekt wird daher nicht entfernt.
 *
 *	Alle Referenzen eines Objektes, das gel�scht werden soll, m�ssen aufgehoben werden. Der
 *	n�chste Durchlauf von persistentGC wird dieses Objekt (und seine Kindobjekte) ebenfalls
 *	l�schen (delete()).
 *
 *	Ein eventuell referenziertes Objekt bleibt erhalten. Es gen�gt daher, die Referenz zu
 *	einem Proxy aufzugeben, wird dessen Elternobjekt gel�scht. Der Proxy wird n�chstens
 *	vom persistentGC kassiert und ggf. dessen referenziertes Objekt.
 */
public class CProxy extends CBrick implements IProxy, IBrick {

	private static final Logger logger = LoggerFactory.getLogger(CProxy.class);
	
	private static final long serialVersionUID = 1L;

	private IBrick referenced;
	private long lastReferenced;

	private String value; /* Alle Werte sind Strings */
	private String feldName;
	private long count;

	public CProxy(String name, String classname, Long id) {
		super(name, classname, id);
		referenced = null;
		lastReferenced = 0;
		value = null;
		feldName = "";
		count = 0;
	}
	
	public void setReference(IBrick brick) {
	    if(brick!=null)
	        lastReferenced = brick.ID();
	    
		referenced = brick;
	}

	public IBrick getReference() {
		return referenced;
	}

	public long lastReference() {
		return lastReferenced;
	}

	/**
	 *	Ergebnisr�ckgabe
	 */
	public byte getByte() throws NumberFormatException {
		return value==null ? 0 : Byte.parseByte(value);
	}
	public short getShort() throws NumberFormatException {
		return value==null ? 0 : Short.parseShort(value);
	}
	public int getInt() throws NumberFormatException {
		return value==null ? 0 : Integer.parseInt(value);
	}
	public long getLong() throws NumberFormatException {
		return value==null ? 0 : Long.parseLong(value);
	}
	public float getFloat() throws NumberFormatException {
		return value==null ? 0 : Float.parseFloat(value);
	}
	public double getDouble() throws NumberFormatException {
		return value==null ? 0 : Double.parseDouble(value);
	}
	public String getString() {
		return value==null ? "" : value;
	}

	/**
	 *	Ergebnis�bergabe
	 */
	public void setByte(byte value) {
		this.value = String.valueOf(value);
	}
	public void setShort(short value) {
		this.value = String.valueOf(value);
	}
	public void setInt(int value) {
		this.value = String.valueOf(value);
	}
	public void setLong(long value) {
		this.value = String.valueOf(value);
	}
	public void setFloat(float value) {
		this.value = String.valueOf(value);
	}
	public void setDouble(double value) {
		this.value = String.valueOf(value);
	}
	public void setString(String value) {
		this.value = value;
	}

	/**
	 *	pr�fen auf Gleichheit
	 */
	public boolean isEqual(byte value) {
		return getByte() == value;
	}
	public boolean isEqual(short value) {
		return getShort() == value;
	}
	public boolean isEqual(int value) {
		return getInt() == value;
	}
	public boolean isEqual(long value) {
		return getLong() == value;
	}
	public boolean isEqual(float value) {
		return getFloat() == value;
	}
	public boolean isEqual(double value) {
		return getDouble() == value;
	}
	public boolean isEqual(String value) {
		return getString().equals(value);
	}

	// Das Proxyobjekt ist ein Verbindungsobjekt und kein Knoten in der
	// Baumstruktur

	/**
	 *	Verweis auf parent (does nothing)
	 */
	 public void parent(IBrick brick){}
	 public IBrick parent(){return null;}


	/**
	 *	�bergabepunkt f�r Internen Iterator
	 */
	public void iterate(CCommand cmd, boolean pre){
		super.execute(cmd);
	}

	// ------------------------------------------
	// Methoden III (Objektmanipulation)
	// ------------------------------------------

	/**
	 *	Methode zum Vervielf�ltigen
	 */
	public Object clone() throws CloneNotSupportedException {
		avoidCloneChildren = true;
		return super.clone();
	}

	/**
	 *	Standardmethode zum Hinzufuegen (does nothing)
	 *	!!! Elemente nicht von IBrick abgeleitet sind nicht zul�ssig !!!
	 */
	public void add(IBrick brick){}

	/**
	 *	Verschieben an das durch IBrick angegebene Element (does nothing!)
	 */
	public void move(IBrick brick) {}

	/**
	 *	Verweis entfernen (does nothing!)
	 */
	public void removeReference(IBrick brick){}

	/**
	 *	Loeschen, wird aufgerufen, wenn ein verweisender Knoten geloescht wird.
	 */
	public void delete() {
		//decrease the counter
		removeProxy();

		// remove from the referenced field
		logger.trace("CProxy.delete {}", ID());
	}

	/**
	 *	Standardmethode zum Verbergen
	 */
	public void hide(){
		setFlag(HIDDEN);
	}

	/**
	 *	Standardmethode zum Hervorholen
	 */
	public void show(){
		removeFlag(HIDDEN);
	}

	// Proxymethoden aus CBrick m�ssen �berschrieben sein

	/**
	 *	Proxy zur�ckgeben (does nothing)
	 */
	public IProxy getProxy(String field){return null;}

	/**
	 *	Proxy entfernen (does nothing)
	 */
	public void removeProxy(IProxy proxy){}

	/**
	 *	Standardmethode zum L�schen (does nothing!)
	 */
	public void remove(){}

	/**
	 *	Referenziertes Feld setzen
	 */
	public void setField(String feld){
		feldName = feld;
		
		//WORKAROUND
		//Dieser Code verursacht seit dem 20.07.2004 eine NullPointerException
		//da vorher edit() aufgerufen werden muss. Da ein Proxy nie indiziert
		//wird, ist der nachfolgende Aufwand in ServeIndex.commit() �berfl�ssig.
		//name(feld);
	}

	/**
	 *	Referenziertes Feld zur�ckgeben
	 */
	public String getField(){
		return feldName;
	}

	/**
	 *	Erh�ht den Referenzz�hler
	 */
	public void addProxy(){
		count++;
	}

	/**
	 *	Verringert den Referenzz�hler
	 */
	public void removeProxy(){
        if(referenced==null){
            return;
        }
        
	    if(lastReferenced==0)
	        lastReferenced = referenced.ID();
	        
		count--;
		count = count<0 ? 0 : count; //count must not be negative
		if(count<=0)
			referenced.removeProxy(this);
	}

	/**
	 *	Aktualisiert das referenzierte Feld (Alle Werte sind Strings)
	 */
	public void update() {
		try {
			if(referenced instanceof IBrickData)
				value = ((IBrickData)referenced).getValueS(feldName);
		}catch(Exception e){
			logger.trace("CProxy.update field: {}, exception {}", feldName, e);
		}
	}
}
