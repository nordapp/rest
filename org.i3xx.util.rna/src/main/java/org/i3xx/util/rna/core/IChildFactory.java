package org.i3xx.util.rna.core;

import java.util.Collection;

public interface IChildFactory {
	
	/** returns the new created collection */
	@SuppressWarnings("rawtypes")
	Collection getChildren(IBrick brick, Collection c);
}
