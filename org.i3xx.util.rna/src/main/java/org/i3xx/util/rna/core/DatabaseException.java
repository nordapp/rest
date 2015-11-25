/*
 * Created on 09.08.2004
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
public class DatabaseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param arg0
	 */
	public DatabaseException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public DatabaseException(Exception arg0) {
		super(arg0);
	}
	
    public DatabaseException(Throwable cause){
    	super(cause);
    }
    
    public DatabaseException(String s, Throwable cause){
    	super(s, cause);
    }
}
