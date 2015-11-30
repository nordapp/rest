package org.i3xx.util.context.impl;

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
public class SimpleContext<K, V> implements IContext<K, V> {
	
	/* The logger */
	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(SimpleContext.class);
	
	private static final Map<String, Map<?, ?>> instances = new HashMap<String, Map<?,?>>();
	
	/** The data map*/
	private final Map<K, V> data;
	
	/** The session identifier */
	private final BigInteger ikey;
	
	@SuppressWarnings("unchecked")
	public SimpleContext(String region) {
		synchronized(instances) {
			if(instances.containsKey(region)) {
				data = (Map<K, V>) instances.get(region);
			} else {
				data = Collections.synchronizedMap(new HashMap<K, V>());
				instances.put(region, data);
			}//fi
		}//
		ikey = BigInteger.ZERO;
	}

	@Override
	public BigInteger logon(String user, Map<String, Object> credentials) {
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
		synchronized(data) {
			data.put(key, value);
		}//
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
		synchronized(data) {
			data.remove(key);
		}//
	}
	
}
