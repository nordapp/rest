package com.i3xx.util.ctree.linker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.i3xx.util.ctree.IConfNode;
import com.i3xx.util.ctree.core.IResolveRaw;
import com.i3xx.util.ctree.core.IValueItem;
import com.i3xx.util.ctree.core.ResolverException;

public final class LinkedResolver implements IResolveRaw, Serializable, Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected List<String> paths;
	protected IValueItem[] values;
	
	public LinkedResolver() {
		paths = new ArrayList<String>();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		//
		LinkedResolver clone = (LinkedResolver)super.clone();
		
		//Note: The Strings are immutable
		clone.paths = new ArrayList<String>();
		clone.paths.addAll(this.paths);
		
		//Note: The Items are immutable
		clone.values = new IValueItem[this.values.length];
		for(int i=0;i<this.values.length;i++)
			clone.values[i] = (IValueItem)this.values[i].clone();
			
		return clone;
	}
	
	/**
	 * @param path the path to set
	 */
	public void addPath(String path) {
		this.paths.add(path);
	}

	/**
	 * @return the path
	 */
	public void removePath(String path) {
		this.paths.remove(path);
	}
	
	/**
	 * Changes the path of a link to a new destination
	 * @param path The path
	 * @param newPath The new destination node
	 */
	public void changePath(String path, IConfNode dest) {
		int i = 0;
		if(path==null){
			for(i=0;i<values.length;i++){
				if(values[i] instanceof LinkedItem)
					break;
			}
		}else{
			i = paths.indexOf(path);
			if(i<0)
				throw new NoSuchElementException("The name of the link '"+path+
						"' is not available at any item.");
			
			String fn = ((LinkedItem)values[i]).node.getFullName();
			if( ! fn.equals(path))
				throw new NoSuchElementException("The name of the link '"+fn+
						"' doesn't match the path '"+path+"'.");
		}
		
		values[i] = new LinkedItem(dest);
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IResolveRaw#resolve(com.i3xx.util.ctree.IConfNode)
	 */
	public String resolve(IConfNode node) throws ResolverException {
		if(values==null)
			throw new ResolverException("Illegal state: The resolver is not linked.");
		
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<values.length;i++){
			buffer.append(values[i].getValue());
		}
		String value = buffer.toString();
		return value;
	}
}
