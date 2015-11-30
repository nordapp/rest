package org.i3xx.util.context.impl;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.i3xx.util.context.model.IContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stefan
 *
 * @param <K>
 * @param <V>
 */
public class SimpleContext<K, V extends Serializable> implements IContext<K, V> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7434582082908948064L;
	
	/* The logger */
	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(SimpleContext.class);
	
	/** The data map*/
	private final Map<K, V> data;
	
	/** The session identifier */
	private final BigInteger ikey;
	
	public SimpleContext() {
		data = Collections.synchronizedMap(new HashMap<K, V>());
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
		return data.get(key);
	}

	@Override
	public void put(K key, V value) {
		data.put(key, value);
	}

	@Override
	public void putSafe(K key, V value) {
		synchronized(data) {
			if( ! data.containsKey(key))
				data.put(key, value);
		}//
	}

	@Override
	public void remove(K key) {
		data.remove(key);
	}
	
}
