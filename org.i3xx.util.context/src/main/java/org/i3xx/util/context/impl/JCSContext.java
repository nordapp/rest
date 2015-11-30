package org.i3xx.util.context.impl;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Map;

import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.apache.commons.jcs.access.exception.CacheException;
import org.i3xx.util.context.model.IContext;

/**
 * @author Stefan
 *
 * @param <K>
 * @param <V>
 */
public class JCSContext<K, V extends Serializable> implements IContext<K, V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6282382430615341263L;
	
	/** The data cache */
	private final CacheAccess<K, V> cache;
	
	/** The session identifier */
	private final BigInteger ikey;
	
	/**
	 * Returns a new instance of the JCS context
	 * 
	 * @param region The cache region
	 * @return
	 */
	public static <K, V extends Serializable> IContext<K, V> getInstance(String region) {
		return new JCSContext<K, V>(region);
	}
	
	/**
	 * Returns an instance of the JCS context
	 * 
	 * @param session The session key
	 * @return
	 */
	public static <K, V extends Serializable> IContext<K, V> getInstance(BigInteger session) {
		return null;
	}
	
	/**
	 * Creates a new JCSContext
	 * 
	 * @param region The cache region
	 * @throws CacheException
	 */
	private JCSContext(String region) throws CacheException {
		cache = JCS.getInstance( region );
		ikey = BigInteger.ZERO;
	}

	@Override
	public BigInteger logon(String user, Map<String, Serializable> credentials) {
		return ikey;
	}
	
	@Override
	public void logoff() {
		//does nothing
	}
	
	@Override
	public V get(K key) {
		return cache.get(key);
	}

	@Override
	public void put(K key, V value) {
		cache.put(key, value);
	}

	@Override
	public void putSafe(K key, V value) {
		cache.putSafe(key, value);
	}

	@Override
	public void remove(K key) {
		cache.remove(key);
	}
}
