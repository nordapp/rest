/**
 * 
 */
package com.i3xx.util.ctree.core;

/**
 * @author Stefan
 *
 */
public class ResolverException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public ResolverException() {
	}

	/**
	 * @param message
	 */
	public ResolverException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ResolverException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ResolverException(String message, Throwable cause) {
		super(message, cause);
	}

}
