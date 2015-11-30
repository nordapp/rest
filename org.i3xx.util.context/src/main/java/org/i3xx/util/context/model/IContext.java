package org.i3xx.util.context.model;

import java.math.BigInteger;
import java.util.Map;

/**
 * @author Stefan
 * @since 30.11.2015
 *
 * @param <K>
 * @param <V>
 */
public interface IContext<K, V> {
	
	/**
	 * Returns a new instance of the JCS context
	 * 
	 * @param region The cache region
	 * @return
	 */
	BigInteger logon(String user, Map<String, Object> credentials);
	
	/**
	 * Returns an instance of the JCS context
	 * 
	 * @param session The session key
	 * @return
	 */
	void logoff();
	
	/**
	 * Gets an element from the cache
	 * 
	 * @param key
	 * @return
	 */
	V get(K key);
	
	/**
	 * Puts an element to the cache
	 * 
	 * @param key
	 * @param value
	 */
	void put(K key, V value);
	
	/**
	 * Puts an element to the cache if it doesn't exist.
	 * 
	 * @param key
	 * @param value
	 */
	void putSafe(K key, V value);
	
	/**
	 * Removes an object from the cache.
	 * 
	 * @param key
	 */
	void remove(K key);
}
