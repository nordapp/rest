package org.i3xx.util.rna.engine.server;

import java.math.BigInteger;

import org.i3xx.util.rna.core.IBrick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is full static synchronized to avoid double id's. A
 * deadlock never occurs because all operations are atomic.
 *  
 * @author Stefan
 *
 */
public class CIdentifier
{
	private static final Logger logger = LoggerFactory.getLogger(CIdentifier.class);
	
	//persistent objects
	private static long m_id = 0;
	private static long systemid = 0;
	private static long idmaske = 0;
	private static long subid = 0;
	private static long idroot = 0;
	private static int count = 0;
	private static long range = 0;
	private static int shift = 0;
	//transient cached objects
	private static long c_id = 0;
	private static long cidmin = 0;
	private static long cidmax = 0;
	//transient unstored objects
	private static long t_id = 0;
	private static long tidmin = 0;
	private static long tidmax = 0;
	//transient unstored objects
	private static long s_id = 0;
	private static long sidmin = 0;
	private static long sidmax = 0;
	
	public CIdentifier(){}

	/**
	 * Set the current object id (from store) to the intern counter
	 * 
	 * @param id
	 */
	public synchronized static void ID(long id)
	{
		m_id=id&0xffffffffl;
	}

	/**
	 * Get the next object id (unique per system) from the intern counter
	 * 
	 * @return
	 */
	public synchronized static long ID()
	{
		m_id++;
		//Test m_id < 0x100000000
		if(m_id<=4294967295l)
		{
			return (systemid<<32)+m_id;
		}
		else
		{
			return 0;
		}
	}
	
	/**
	 * Get the next temporary cached object id.
	 * 
	 * @return
	 */
	public synchronized static long CID()
	{
		c_id++;
		if(c_id>cidmax || c_id<cidmin)
			c_id = cidmin;
		
		return c_id;
	}
	
	/**
	 * @param id The temporary cached object id to test.
	 * @return <tt><code>true</code></tt> if the number is in CID range,
	 * <tt><code>false</code></tt> otherwise.
	 */
	public synchronized static boolean isCID(long id) {
		return (id <= cidmax && id >= cidmin);
	}
	
	/**
	 * Get the next temporary object id.
	 * 
	 * @return
	 */
	public synchronized static long TID()
	{
		t_id++;
		if(t_id>tidmax || t_id<tidmin)
			t_id = tidmin;
		
		return t_id;
	}
	
	/**
	 * @param id The temporary object id to test.
	 * @return <tt><code>true</code></tt> if the number is in TID range,
	 * <tt><code>false</code></tt> otherwise.
	 */
	public synchronized static boolean isTID(long id) {
		return (id <= tidmax && id >= tidmin);
	}
	
	/**
	 * Set the current object id (from store) to the intern counter
	 */
	public synchronized static void SID(long id) {
		s_id = id;
	}
	
	/**
	 * Get the next search object id.
	 * 
	 * @return
	 */
	public synchronized static long SID()
	{
		s_id++;
		if(s_id>sidmax || s_id<sidmin)
			s_id = sidmin;
        
		return s_id;
	}
	
	/**
	 * @param id The temporary object id to test.
	 * @return <tt><code>true</code></tt> if the number is in SID range,
	 * <tt><code>false</code></tt> otherwise.
	 */
	public synchronized static boolean isSID(long id) {
		return (id <= sidmax && id >= sidmin);
	}

	/**
	 * Set the system id (unique per system)
	 * id = system-id | intern-counter
	 * 
	 * @param id
	 */
	public synchronized static void SystemID(long id)
	{
		if((id&idmaske)!=subid)
		{
			logger.info("System-ID doesn't match: {} & {} != {}", id, idmaske, subid);
            System.err.println("System-ID missmatch, Server stopped !!!");
			Runtime.getRuntime().exit(0);
		}//fi
		systemid = id;
	}

	/**
	 * Get the system id
	 * id = system-id | intern-counter
	 * 
	 * @return
	 */
	public synchronized static long SystemID()
	{
		return systemid;
	}

	/**
	 * Get the common id base for this system.
	 * system-id & mask = sub-id
	 * 
	 * @param id
	 */
	public synchronized static void SubID(long id)
	{
		subid = id;
	}

	/**
	 * Get the common id base for this system.
	 * system-id & mask = sub-id
	 * 
	 * @return
	 */
	public synchronized static long SubID()
	{
		return subid;
	}

	/**
	 * Set the mask
	 * 
	 * @param id
	 */
	public synchronized static void IdMask(long id)
	{
		idmaske = id;
	}

	/**
	 * Get the mask
	 * 
	 * @return
	 */
	public synchronized static long IdMask()
	{
		return idmaske;
	}

	/**
	 * Set the temporary id  mask
	 * 
	 * @param id
	 */
	public synchronized static void CidMask(long start, long count)
	{
		cidmin = start; //inclusive
		cidmax = cidmin + count; 
	}

	/**
	 * Set the temporary id  mask
	 * 
	 * @param id
	 */
	public synchronized static void TidMask(long start, long count)
	{
		tidmin = start; //inclusive
		tidmax = tidmin + count; 
	}

	/**
	 * Set the search id mask
	 * 
	 * @param sidmin
	 * @param sidmax
	 * @param sidmaske
	 */
	public synchronized static void SidMask(long start, long count)
	{
		sidmin = start; //inclusive
		sidmax = sidmin + count;
	}

	/**
	 * @param id
	 */
	public synchronized static void IdRoot(long id)
	{
		//Die Variable var gibt an, um wieviele Bit das Resultat nach
		//rechts verschoben werden soll. Aus der Angabe 00040000 wird
		//das Ergebnis um 18 Positionen nach rechts verschoben. 4 re-
		//pr�sentiert ein Bit damit sind 2 Roots m�glich (0 und 1).
		//18 Stellen ergeben 262144 Elemente pro Auflistung (Root).
		
		int var = 0;
		long temp = id;
		if(id != 0){
			while( ((temp >> var) & 0x1) == 0 )
				var += 1;
		}
		long elem = (var==0)?0x100000000l:2<<(var-1);
		int root = (int)((0xffffffff&id)>>var)+1;
		
		shift = var;
		count = root;
		range = elem;
		idroot = id;
	}

	/**
	 * @return
	 */
	public synchronized static long IdRoot()
	{
		return idroot;
	}

	/**
	 * Get the first valid id.
	 * 
	 * @return the first valid id (inclusive)
	 */
	public synchronized static long startID()
	{
		return (systemid<<32)+1;
	}

	/**
	 * Get the last valid id.
	 * 
	 * @return the last valid id (inclusive)
	 */
	public synchronized static long lastID()
	{
		return (systemid<<32)+4294967295l;
	}

	/**
	 * Get the first valid transient cached id.
	 * 
	 * @return the first valid id (inclusive)
	 */
	public synchronized static long startCID()
	{
		return cidmin;
	}

	/**
	 * Get the last valid transient cached id.
	 * 
	 * @return the last valid id (inclusive)
	 */
	public synchronized static long lastCID()
	{
		return cidmax;
	}

	/**
	 * Get the first valid transient id (not persistent, not cached).
	 * 
	 * @return the first valid id (inclusive)
	 */
	public synchronized static long startTID()
	{
		return tidmin;
	}

	/**
	 * Get the last valid transient id (not persistent, not cached).
	 * 
	 * @return the last valid id (inclusive)
	 */
	public synchronized static long lastTID()
	{
		return tidmax;
	}

	/**
	 * Get the first valid search id (persistent, not cached).
	 * 
	 * @return the first valid id (inclusive)
	 */
	public synchronized static long startSID()
	{
		return sidmin;
	}

	/**
	 * Get the last valid search id (persistent, not cached).
	 * 
	 * @return the last valid id (inclusive)
	 */
	public synchronized static long lastSID()
	{
		return sidmax;
	}
	
	/**
	 * @param id
	 * @return
	 */
	public synchronized static int getRoot(long id) {
		return (int)((id & idroot)>>shift);
	}
	
	/**
	 * Anzahl der Auflistungen
	 * @return
	 */
	public synchronized static int countRoots() {
		return count;
	}
	
	/**
	 * Anzahl der Elemente pro Auflistung und Durchlauf. Bei dem Erreichen
	 * des Bereichsendes wird mit der n�chsten Auflistung fortgefahren. Beim
	 * Erreichen des Bereichsendes der letzten Auflistung wird mit der ersten
	 * Auflistung fortgefahren.
	 * 
	 * @return
	 */
	public synchronized static long range() {
		return range;
	}
	
	/**
	 * Returns the store state of an object.
	 *  
	 * @param id
	 * @return
	 * @see com.i3xx.ob.core.IBrick
	 */
	public synchronized static int getType(long id) {
		if(id<=lastID() && id>=startID())
			return IBrick.IS_PERSISTENT;
		else if(isSID(id))
			return IBrick.IS_PERSISTENT_SEARCH;
		else if(isCID(id))
			return IBrick.IS_TRANSIENT_CACHED;
		else if(isTID(id))
			return IBrick.IS_TRANSIENT;
		else if(id==IBrick.ID_CREATEID)
			return IBrick.IS_PERSISTENT_NOT_CREATED;
		else if(id==IBrick.ID_TEMPNODE)
			return IBrick.IS_TRANSIENT_WITHOUT_ID;
		else if(id==IBrick.ID_CREATECID)
			return IBrick.IS_TRANSIENT_CACHED_NOT_CREATED;
		else if(id==IBrick.ID_CREATETID)
			return IBrick.IS_TRANSIENT_NOT_CREATED;
		else if(id==IBrick.ID_CREATESID)
			return IBrick.IS_PERSISTENT_SEARCH_NOT_CREATED;
		else
			return IBrick.IS_UNDEFINED;
	}
	
	/**
	 * Returns true if the object is always transient (cannot be stored persistent).
	 * 
	 * @param id
	 * @return
	 */
	public synchronized static boolean isTransient(long id) {
		switch(getType(id)){
		case IBrick.IS_TRANSIENT_CACHED:
		case IBrick.IS_TRANSIENT:
		case IBrick.IS_TRANSIENT_WITHOUT_ID:
		case IBrick.IS_TRANSIENT_CACHED_NOT_CREATED:
		case IBrick.IS_TRANSIENT_NOT_CREATED:
			return true;
		case IBrick.IS_PERSISTENT:
		case IBrick.IS_PERSISTENT_NOT_CREATED:
		case IBrick.IS_PERSISTENT_SEARCH:
		case IBrick.IS_PERSISTENT_SEARCH_NOT_CREATED:
		case IBrick.IS_UNDEFINED:
		default:
			return false;
		}
	}
	
	/**
	 * Returns true if the object is always search (cannot be stored persistent).
	 * 
	 * @param id
	 * @return
	 */
	public synchronized static boolean isSearch(long id){
		switch(getType(id)){
		case IBrick.IS_PERSISTENT_SEARCH:
		case IBrick.IS_PERSISTENT_SEARCH_NOT_CREATED:
			return true;
		case IBrick.IS_TRANSIENT_CACHED:
		case IBrick.IS_TRANSIENT:
		case IBrick.IS_TRANSIENT_WITHOUT_ID:
		case IBrick.IS_TRANSIENT_CACHED_NOT_CREATED:
		case IBrick.IS_TRANSIENT_NOT_CREATED:
		case IBrick.IS_PERSISTENT:
		case IBrick.IS_PERSISTENT_NOT_CREATED:
		case IBrick.IS_UNDEFINED:
		default:
			return false;
		}
	}
	
	/**
	 * Creates the id of a defined (immutable) point at the time line.
	 * 
	 * @param brick The brick
	 * @return
	 */
	public static BigInteger timelineID(IBrick brick) {
		return BigInteger.valueOf(brick.ID()).shiftLeft(64).or( BigInteger.valueOf(brick.transid()) );
	}
	
	/**
	 * Creates the id of a defined (immutable) point at the time line.
	 * 
	 * @param id the id of the brick
	 * @param timeline the current timeline of the brick (a transID)
	 * @return
	 */
	public static BigInteger timelineID(long id, long transID) {
		return BigInteger.valueOf(id).shiftLeft(64).or( BigInteger.valueOf(transID) );
	}
	
	/**
	 * Extract the fields from the defined (immutable) point at the time line.
	 * 
	 * @param id
	 * @return
	 */
	public static long[] timelineFields(BigInteger tPoint) {
		long[] fields = new long[2];
		fields[1] = tPoint.and(BigInteger.valueOf(0xFFFFFFFF)).longValue();
		fields[0] = tPoint.shiftRight(64).and(BigInteger.valueOf(0xFFFFFFFF)).longValue();
		return fields;
	}
	
	/**
	 * Print the result of TidMask and CidMask
	 * 
	 * @param id The mask as a binary long e.g 0x0L (!)
	 */
	public synchronized static void PrintMask(long id) {
		int var = 0;
		long temp = id;
		if(id != 0){
			while( ((temp >> var) & 0x1) == 0 )
				var += 1;
		}
		long span = (var==0)?0:((1<<var)-1);
		long root = (id>>(var+1));
		
		//inclusive tidmin, tidmax
		long tidmin = 1 + root; //inclusive
		long tidmax = tidmin + span; 
		
    	System.out.println(fmt("von "+Long.toString( tidmin )+" bis "+Long.toString( tidmax )+" ["+var+"]"));
    	System.out.println("byte   8       7       6       5       4       3       2       1");
    	System.out.println(".......|.......|.......|.......|.......|.......|.......|.......|");
    	System.out.println(fmt(Long.toBinaryString( id ))+" id   "+id);
    	System.out.println(fmt(Long.toBinaryString( span ))+" span "+span);
    	System.out.println(fmt(Long.toBinaryString( tidmin ))+" min  "+tidmin);
    	System.out.println(fmt(Long.toBinaryString( tidmax ))+" max  "+tidmax);
    	System.out.println("....|.........|.........|.........|.........|.........|.........");
    	System.out.println("   60        50        40        30        20        10         ");
	}
    private static String fmt(String stmt) {
    	return getRight(stmt, ' ', 64);
    }
	
	/**
	 * Returns the string of the length len, cut at the left side or append leading chars c
	 * 
	 * @param value
	 * @param c
	 * @param len
	 * @return
	 */
    private static String getRight(String value, char c, int len)
	{
		if ( len == 0 )
		{
			return "";
		}
		else if ( value == null )
		{
			return getString( c, len );
		}
		else if ( value.length() > len )
		{
			return value.substring( value.length()-len) ;
		}
		else if ( value.length() < len )
		{
			return getString( c, len - value.length() ) + value;
		}
		// else if ( value.length() == len )
		{
			return value;
		}
	}

	/**
	 * Returns a String of the length len, containing len occurrences of the char 'c'
	 * 
	 * @param c
	 * @param len
	 * @return
	 */
	private static String getString(char c, int len)
	{
		StringBuffer buf = new StringBuffer();
		for(int i=0;i<len;i++){
			buf.append(c);
		}
		return buf.toString();
	}
}
