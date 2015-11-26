/*
 * Created on 28.07.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.i3xx.util.rna.core;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class QueryException extends Exception {

	private static final long serialVersionUID = -2833394583258120973L;

	/**
	 * 
	 */
	public QueryException() {
		super();
	}

	/**
	 * @param s
	 */
	public QueryException(String s) {
		super(s);
	}

	/**
	 * @param cause
	 */
	public QueryException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param s
	 * @param cause
	 */
	public QueryException(String s, Throwable cause) {
		super(s, cause);
	}
}
