package org.i3xx.util.ctree.impl;

import org.i3xx.util.ctree.core.EscapeResolver;
import org.i3xx.util.ctree.core.IResolveRaw;
import org.i3xx.util.ctree.core.IResolverFactory;
import org.i3xx.util.ctree.core.SimpleResolver;
import org.i3xx.util.ctree.linker.LinkedResolver;

public class LinkableResolverFactory implements IResolverFactory {
	
	public IResolveRaw defaultResolver;
	
	public LinkableResolverFactory() {
		defaultResolver = new SimpleResolver();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.parser.IResolverFactory#getResolver(int)
	 */
	public IResolveRaw getResolver(int type) {
		switch(type){
		case TYPE_LINK:
			return new LinkedResolver();
		case TYPE_COPY:
			return new LinkedResolver();
		case TYPE_ESCAPE:
			return new EscapeResolver();
		case TYPE_DEFAULT:
			default:
				return defaultResolver;
		}
	}
}