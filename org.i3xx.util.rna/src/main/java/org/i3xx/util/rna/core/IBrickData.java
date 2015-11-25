/*
 * Created on 14.03.2005
 */
package org.i3xx.util.rna.core;

import java.util.List;
import java.util.Map;

/**
 * @author S. Hauptmann
 */
public interface IBrickData extends IBrick {
	
	public static final String classdef = "com.i3xx.ob.proc.db.CData";
	
	/**
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	Map getMap();
	
	/**
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	Map results();
	
	/**
	 * @return list of chunk roots for path operations
	 */
	List<IBrick> chunks();
	
	/**
	 * @return Return the version of the chunks list
	 */
	int getChunkVerion();
	
	/**
	 * @param chunkVersion Set the version of the chunks list
	 */
	void setChunkVerion(int chunkVersion);
	
	/**
	 * @deprecated Use IBrickInterna instead; sh 01.08.2010
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	Map updates();
	
	/**
	 * @param name
	 * @return
	 */
	Object getValue(String name);
	
	/**
	 * @param name
	 * @param deflt
	 * @return
	 */
	Object getValue(String name, Object deflt);
	
	/**
	 * @param name
	 * @param deflt
	 * @return
	 */
	Integer getValue(String name, Integer deflt);
	
	/**
	 * @param name
	 * @param deflt
	 * @return
	 */
	Long getValue(String name, Long deflt);
	
	/**
	 * @param name
	 * @param deflt
	 * @return
	 */
	Float getValue(String name, Float deflt);
	
	/**
	 * @param name
	 * @param deflt
	 * @return
	 */
	Double getValue(String name, Double deflt);
	
	/**
	 * @param name
	 * @param deflt
	 * @return
	 */
	String getValue(String name, String deflt);
	
	/**
	 * @param name
	 * @param deflt
	 * @return
	 */
	IProxy getValue(String name, IProxy deflt);
	
	/**
	 * @param name
	 * @param value
	 */
	void setValue(String name, Object value) throws UserContentException;	
	
	/**
	 * @param name
	 * @return
	 */
	String getValueS(String name);
}
