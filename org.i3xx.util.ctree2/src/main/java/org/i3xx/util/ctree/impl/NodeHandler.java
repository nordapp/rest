package org.i3xx.util.ctree.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.i3xx.util.ctree.IConfNode;
import org.i3xx.util.ctree.core.IResolverFactory;
import org.i3xx.util.ctree.linker.LinkedResolver;
import org.i3xx.util.ctree.linker.Linker;


public class NodeHandler {
	
	private final IConfNode root;
	private final NodeParser parser;
	
	public NodeHandler(IConfNode root, IResolverFactory factory) {
		this.root = root;
		this.parser = factory==null ? null : new NodeParser(factory);
	}
	
	/**
	 * @param k
	 * @param v
	 * @throws IOException
	 */
	public void addconf(String k, String v) throws IOException
	{
		root.create(k).value( v );
	}

	/**
	 * @param props
	 * @param prefix
	 * @throws IOException
	 */
	public void setProperties(Properties props, String prefix) throws IOException
	{
    	Iterator<Object> iter = props.keySet().iterator();
    	while(iter.hasNext()){
    		String key = (String)iter.next();
    		addconf(prefix+"."+key, props.getProperty(key));
    		//root.create(prefix+"."+key).value( props.getProperty(key) );
    	}
	}

	/**
	 * @param props
	 * @param prefix
	 * @throws IOException
	 */
	public void setPropertiesD(Properties props, String prefix) throws IOException
	{
    	Iterator<Object> iter = props.keySet().iterator();
    	while(iter.hasNext()){
    		String key = (String)iter.next();
    		addconf(prefix+"."+key, props.getProperty(key).replace("\\", "\\\\"));
    		//root.create(prefix+"."+key).value( props.getProperty(key) );
    	}
	}
	
	/**
	 * Returns a configured string
	 * 
	 * @param path The path of the configuration
	 * @return
	 */
	public String getParam(String path) {
		return root.get(path).value();
	}
	
	/**
	 * Sets a configured string
	 * 
	 * @param path The path of the configuration
	 * @param value The new value
	 */
	public void setParam(String path, String value) {
		IConfNode node = root.get(path);
		node.value(value);
		node.resolver(null);
		
		if(parser!=null){
			parser.process(node);
			Linker linker = new Linker(node);
			linker.process();
		}
	}
	
	/**
	 * Changes a configured link to another position
	 * 
	 * If the old link is null, the first linked item will be changed.
	 * 
	 * @param path The path of the configuration
	 * @param oldLink The old link
	 * @param newLink The new link
	 */
	public void editLink(String path, String oldLink, String newLink) {
		IConfNode node = root.get(path);
		IConfNode dest = root.get(newLink);
		try{
			LinkedResolver resolver = (LinkedResolver)node.resolver();
			resolver.changePath(oldLink, dest);
		}catch(ClassCastException e){
			throw new IllegalArgumentException("The change of the path '"+path+"' results in an exception.", e);
		}catch(NoSuchElementException e){
			throw new IllegalArgumentException("The change of the path '"+path+"' results in an exception.", e);
		}
	}
}
