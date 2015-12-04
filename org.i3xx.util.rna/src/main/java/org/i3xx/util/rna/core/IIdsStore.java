/*
 * Created on 18.11.2004
 *
 */
package org.i3xx.util.rna.core;


/**
 * @author S. Hauptmann
 *
 */
public interface IIdsStore  {
	
	public static final String classdef = "org.i3xx.util.rna.impl.IdsStore";
	
	void delete();
	byte[] persist(int index) throws ConcurrentAccessException;
	void persist(byte[] cbuf) throws ConcurrentAccessException;
	void fix();
	void destroy();
	void length(int value);
	int length();
	int size();
	long TS();
	String mimetype();
	void mimetype(String mimetype);
}
