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
	
	public static final String classdef = "com.i3xx.ob.proc.db.IdsStore";
	
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
