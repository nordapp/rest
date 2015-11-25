/*
 * Created on 18.11.2004
 *
 * Interne Funktionen auf CBrick (fr�her protected)
 */
package org.i3xx.util.rna.core;

import java.util.Collection;
import java.util.Map;

/**
 * @author S. Hauptmann
 *
 */
public interface IBrickInterna {
	@SuppressWarnings("rawtypes")
	void vector(Collection m);
	@SuppressWarnings("rawtypes")
	void proxies(Collection m);
	@SuppressWarnings("rawtypes")
	void rights(Map m);
	@SuppressWarnings("rawtypes")
	void updates(Map m);
	@SuppressWarnings("rawtypes")
	void interna(Map m);
	@SuppressWarnings("rawtypes")
	Collection vector();
	@SuppressWarnings("rawtypes")
	Collection proxies();
	@SuppressWarnings("rawtypes")
	Map rights();
	@SuppressWarnings("rawtypes")
	Map updates();
	@SuppressWarnings("rawtypes")
	Map interna();
	long[] roles();
	void setFlag(int flag);
	void removeFlag(int flag);
	void resetFileRecord(String[] record);
}
