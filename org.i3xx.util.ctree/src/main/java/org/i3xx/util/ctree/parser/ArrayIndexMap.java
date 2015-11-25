package org.i3xx.util.ctree.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

/**
 * The map handles the index values used by the attribute array="true"
 *
 * @author Stefan
 * @since 11.01.2013
 */
public class ArrayIndexMap {
	
	/**
	 * The map
	 */
	private static final Map<Long, Integer> indexMap = new HashMap<Long, Integer>();
	
	/**
	 * Creates a map related to a filename
	 * 
	 * @param fileName
	 * @return
	 */
	protected static Integer get(String fileName) {
		
		//use lower case letter search to identify the root directory
		int p = fileName.toLowerCase().indexOf("i3xx");
		if(p>-1)
			fileName = fileName.substring(p);
		
		Long key = new Long(getHash(fileName));
		Integer value = indexMap.get(key);
		
		if(value==null){
			value = new Integer(getInitialValue(key));
			indexMap.put(key, value);
		}
		
		return value;
	}
	
	/**
	 * Returns the initial value for each filename
	 * 
	 * @param key The key related to the filename
	 * @return
	 */
	private static int getInitialValue(Long key) {
		
		int p = ( indexMap.size()+1 ) * 0x4000; //16384
		
		return p;
	}
	
	/**
	 * Returns the key related to the filename
	 * 
	 * @param fileName
	 * @return
	 */
	private static long getHash(String fileName) {
		
		CRC32 crc = new CRC32();
		crc.update(fileName.getBytes());
		
		return crc.getValue();
	}
	
}
