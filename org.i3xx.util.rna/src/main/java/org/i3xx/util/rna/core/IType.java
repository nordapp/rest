/*
 * Created on 11.04.2005
 */
package org.i3xx.util.rna.core;

/**
 * @author S. Hauptmann
 */
public interface IType {
	boolean equals(IBrick brick);
	boolean equalsIgnoreCase(IBrick brick);
	boolean isInstance(IBrick brick);
}
