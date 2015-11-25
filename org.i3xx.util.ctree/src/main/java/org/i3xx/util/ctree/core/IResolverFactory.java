package org.i3xx.util.ctree.core;


public interface IResolverFactory {

	public static final int TYPE_DEFAULT = 0;
	public static final int TYPE_LINK = 1;
	public static final int TYPE_COPY = 2;
	public static final int TYPE_ESCAPE = 3;
	
	/**
	 * @param type The type of the resolver
	 * @return The resolver
	 */
	IResolveRaw getResolver(int type);
}
