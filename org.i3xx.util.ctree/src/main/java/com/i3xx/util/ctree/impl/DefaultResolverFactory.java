package com.i3xx.util.ctree.impl;

import com.i3xx.util.ctree.core.IResolveRaw;
import com.i3xx.util.ctree.core.IResolverFactory;
import com.i3xx.util.ctree.core.SimpleResolver;


public class DefaultResolverFactory implements IResolverFactory {
	
	public IResolveRaw defaultResolver;
	
	public DefaultResolverFactory() {
		defaultResolver = new SimpleResolver();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.parser.IResolverFactory#getResolver(int)
	 */
	public IResolveRaw getResolver(int type) {
		return defaultResolver;
	}
}
