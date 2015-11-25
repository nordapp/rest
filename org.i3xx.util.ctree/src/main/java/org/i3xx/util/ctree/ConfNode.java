/* robots=none */
package org.i3xx.util.ctree;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;

import org.i3xx.util.ctree.core.IResolveRaw;
import org.i3xx.util.ctree.core.IUpdateListener;
import org.i3xx.util.ctree.core.ResolverException;


/*
 *	The intern representation are two collections.
 *	The TreeMap contains a name sorted order and the
 *	Vector has an insertion order.
 *	
 *	Note: The initialization and the reconfiguration works similar.
 *  It is possible to have an initial value of null and do a
 *  reconfiguration later when the value is available.
 *	
 *  The reconfiguration is started by an update. The updated node
 *  fires the update event that the listener listens to. The listener
 *  than calls the reconf() method which get the value from the source
 *  node by the resolver.
 *  
 *  new value => node1.rawValue(String) => fire update event =>
 *  listener.update() => node2.reconf() => resolver.resolve(String) =>
 *  get the node (node1) and read the value from.
 */
public class ConfNode implements IConfNode, Serializable
{

	private static final long serialVersionUID = 7778871132743356650L;
	
	/** The name index of the child nodes */
	protected Map<String, IConfNode> nodes = null;
	/** The array of the child nodes */
	protected ArrayList<IConfNode> nodix = null;
	
	/** The root of the node tree */
	protected IConfNode root = null;
	/** The parent of the node */
	protected IConfNode parent = null;
	/** The name of the node */
	protected String name = null;
	/** The value of the node (raw value) */
	protected String value;
	
	/** True if the raw value is resolved */
	protected boolean resolved = true;
	/** The resolver for this node */
	private IResolveRaw resolver = null;
	
	/** The update listener */
	private List<IUpdateListener> listeners = null;
	
	public ConfNode()
	{
		nodes = new TreeMap<String, IConfNode>();
		nodix = new ArrayList<IConfNode>();
		name = "[ROOT]";
		root = this;
	}

	public ConfNode(String name)
	{
		nodes = new TreeMap<String, IConfNode>();
		nodix = new ArrayList<IConfNode>();
		this.name = name;
		root = this;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#add(com.i3xx.util.ctree.IConfNode)
	 */
	public void add(IConfNode node)
	{
		node.parent(this);
		node.root(root);
        
        synchronized(this) {
    		nodes.put(node.name(), node);
    		nodix.add(node);
        }
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#create(java.lang.String)
	 */
	public IConfNode create(String path)
	{
		int depth = 0;
		String[] pathElems = null;

		if(path==null)
			return null;
		if(path.equals(""))
			return null;

		StringTokenizer tokens = new StringTokenizer(path, ".", false);
		depth = tokens.countTokens();
		pathElems = new String[depth];

		if(depth <= 0)
			return null;

		int i=0;
		while(tokens.hasMoreTokens())			/* Array mit Namen aus dem Pfad erstellen */
		{
			pathElems[i] = tokens.nextToken();
			i++;
		}

		IConfNode refer = null; 			   /* Den Baum iterieren */
		IConfNode node = null;
		i=0;
        synchronized(this) {
			if(root==this)
			{
                node = getChildNode(pathElems[i]);
    			if(node==null)
    			{
    				node = new ConfNode(pathElems[i]);
    				root.add(node);
    			}
    			refer = node;
    			i++;
			}
			else
			{
				refer = root;
			}
        }

		while(i<depth)
		{
			node = refer.getChildNode(pathElems[i]);

			if(node==null)
			{
				node = new ConfNode(pathElems[i]);
				refer.add(node);
			}
			refer = node;
			i++;
		}

		return refer;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#copy(com.i3xx.util.ctree.IConfNode)
	 */
	public IConfNode copy(IConfNode dest)
	{
		IConfNode copy = new ConfNode(name);
		copy.value(value);
		copy.resolved(resolved);
		try {
			copy.resolver((IResolveRaw)resolver.clone());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace(System.err);
		}
		if(dest!=null)
			dest.add(copy);
		
		for(IConfNode child:nodix) {
			child.copy(copy);
		}
		
		return copy;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#update(com.i3xx.util.ctree.IConfNode)
	 */
	public IConfNode update(IConfNode dest)
	{
		if(dest==null)
			throw new NoSuchElementException("The update destination is null (illegal null value).");
		
		IConfNode node = dest.create( getFullName() );
		node.value(value);
		node.resolved(resolved);
		try {
			node.resolver((IResolveRaw)resolver.clone());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace(System.err);
		}
		
		for(IConfNode child:nodix) {
			child.update(dest);
		}
		
		return dest;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#child(java.lang.String)
	 */
	public IConfNode child(String cname) throws NoSuchElementException
	{
		IConfNode child = getChildNode(cname);

		if(child == null){
			//full name of this node
			String path = getFullName() + "." + cname;

			throw new NoSuchElementException("'"+path+"'");
		}

		return child;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#get(java.lang.String)
	 */
	public IConfNode get(String path) throws NoSuchElementException
	{
		int depth = 0;
		String[] pathElems = null;
		IConfNode rt = root();

		if(path==null)
			return null;
		if(path.equals(""))
			return null;

		StringTokenizer tokens = new StringTokenizer(path, ".", false);
		depth = tokens.countTokens();
		pathElems = new String[depth];

		if(depth <= 0)
			return null;

		int i=0;
		while(tokens.hasMoreTokens())			/* Array mit Namen aus dem Pfad erstellen */
		{
			pathElems[i] = tokens.nextToken();
			i++;
		}

		IConfNode refer = null; 			   /* Den Baum iterieren */
		i=0;
		if(rt==this)
		{
			refer = getChildNode(pathElems[i++]);

			if(refer==null)
				throw new NoSuchElementException("'"+path+"'");
		}
		else
		{
			refer = rt;

			if(refer==null)
				throw new NoSuchElementException("'"+path+"'");
		}

		while(i<depth)
		{
			refer = refer.getChildNode(pathElems[i++]);

			if(refer==null)
				throw new NoSuchElementException("'"+path+"'");
		}

		return refer;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#reconf()
	 */
	public void reconf() throws ResolverException
	{
		//Do not reconfigure.
		//The preferred way is to read the current value from the linked node.
		//
		//To skip this behavior, replace the linked item by a string item containing
		//the resolved value in the resolver.
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#remove(com.i3xx.util.ctree.IConfNode)
	 */
	public void remove(IConfNode node) throws NoSuchElementException
	{
        synchronized(getClass()) {
            node = nodes.remove(node.name());
            nodix.remove(node);
        }
        
        if(node==null)
            throw new NoSuchElementException();
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#isLeafNode()
	 */
	public boolean isLeafNode()
	{
		return (nodix.size() == 0);
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#size()
	 */
	public int size()
	{
		return nodix.size();
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#getChildNode(java.lang.String)
	 */
	public IConfNode getChildNode(String name)
	{
		return (IConfNode)nodes.get(name);
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#getChildNode(int)
	 */
	public IConfNode getChildNode(int idx)
	{
		return nodix.get(idx);
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#getChildNodes()
	 */
	public Iterator<String> getChildNodes()
	{
		return nodes.keySet().iterator();
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#getChildNodes2()
	 */
	public Iterator<IConfNode> getChildNodes2()
	{
		return nodix.iterator();
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#getChildNodes3()
	 */
	public IConfNode[] getChildNodes3()
	{
		IConfNode[] nodes = new ConfNode[nodix.size()];
		nodix.toArray(nodes);
		return nodes;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#getChildValues()
	 */
	public String[] getChildValues()
	{
		ArrayList<String> v = new ArrayList<String>();
		
		IConfNode[] nodes = getChildNodes3();
		for(int i=0;i<nodes.length;i++)
			if(nodes[i].isLeafNode())
				v.add(nodes[i].value());
		
		String[] values = new String[v.size()];
		v.toArray(values);
		return values;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#name(java.lang.String)
	 */
	public void name(String newValue)
	{
        synchronized(this) {
            this.name = newValue;
        }
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#name()
	 */
	public synchronized String name()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#value(java.lang.String)
	 */
	public void value(String newValue)
	{
		String oldValue = value();
		this.value = newValue;
		
		fireUpdateEvent(oldValue);
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#value()
	 */
	public String value()
	{
		try {
			return resolver==null ? value : resolver.resolve(this);
		} catch (ResolverException e) {
			return value;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#rawValue()
	 */
	public String rawValue() {
		return value;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#value(boolean)
	 */
	public boolean value(boolean def)
	{
		String value = value(); /* overwrites this.value to be thread safe */
		if(value==null)
			return def;
		if(value.equals(""))
			return def;

		return Boolean.valueOf(value).booleanValue();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#value(byte)
	 */
	public byte value(byte def)
	{
		String value = value(); /* overwrites this.value to be thread safe */
		try {
			if( value.startsWith( "0x" ) ) {
				return Byte.parseByte( value.substring( 2 ), 16 );
			}else if( value.startsWith( "0o" ) ) {
				return Byte.parseByte( value.substring( 2 ), 8 );
			}else if( value.startsWith( "0b" ) ) {
				return Byte.parseByte( value.substring( 2 ), 2 );
			}else{
				return Byte.parseByte(value);
			}
		}catch( Exception e ) {
			return def;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#value(int)
	 */
	public int value(int def)
	{
		String value = value(); /* overwrites this.value to be thread safe */
		try {
			if( value.startsWith( "0x" ) ) {
				return Integer.parseInt( value.substring( 2 ), 16 );
			}else if( value.startsWith( "0o" ) ) {
				return Integer.parseInt( value.substring( 2 ), 8 );
			}else if( value.startsWith( "0b" ) ) {
				return Integer.parseInt( value.substring( 2 ), 2 );
			}else{
				return Integer.parseInt(value);
			}
		}catch( Exception e ) {
			return def;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#value(long)
	 */
	public long value(long def)
	{
		String value = value(); /* overwrites this.value to be thread safe */
		try {
			if( value.startsWith( "0x" ) ) {
				return Long.parseLong( value.substring( 2 ), 16 );
			}else if( value.startsWith( "0o" ) ) {
				return Long.parseLong( value.substring( 2 ), 8 );
			}else if( value.startsWith( "0b" ) ) {
				return Long.parseLong( value.substring( 2 ), 2 );
			}else{
				return Long.parseLong(value);
			}
		}catch( Exception e ) {
			return def;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#value(float)
	 */
	public float value(float def)
	{
		String value = value(); /* overwrites this.value to be thread safe */
		try {
			return Float.parseFloat(value);
		}catch( Exception e ) {
			return def;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#value(double)
	 */
	public double value(double def)
	{
		String value = value(); /* overwrites this.value to be thread safe */
		try {
			return Double.parseDouble(value);
		}catch( Exception e ) {
			return def;
		}
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#parent(com.i3xx.util.ctree.IConfNode)
	 */
	public void parent(IConfNode newValue)
	{
        synchronized(this) {
            this.parent = newValue;
        }
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#parent()
	 */
	public synchronized IConfNode parent()
	{
        return parent;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#root(com.i3xx.util.ctree.IConfNode)
	 */
	public void root(IConfNode newValue)
	{
		synchronized(this) {
			this.root = newValue;
		}
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#root()
	 */
	public synchronized IConfNode root()
	{
		return root;
	}
	
	/** Der Name von root und der eigene Name fehlen */
	public String getPath()
	{
		IConfNode parent = parent(); /* overwrites this.parent to be thread safe */
		if(parent == null){
			return "";
		}else if(parent == root()){
			return "";
		}else{
			String temp = parent.getPath()+"."+parent.name();
			if(temp.startsWith("."))
				temp = temp.substring(1);
			
			return temp;
		}
	}
	
	/** Der Name von root fehlt */
	public String getFullName()
	{
		String temp = getPath();
		String name = name(); /* overwrites this.name to be thread safe */

		if(temp.equals(""))
			return name;
		
		return temp + "." + name;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#resolved(boolean)
	 */
	public void resolved(boolean resolved) {
		this.resolved = resolved;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#resolved()
	 */
	public boolean resolved() {
		return resolved;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#resolver(com.i3xx.util.ctree.IResolveRaw)
	 */
	public void resolver(IResolveRaw resolver) {
		this.resolver = resolver;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#resolver()
	 */
	public IResolveRaw resolver() {
		return resolver;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#addListener(com.i3xx.util.ctree.IUpdateListener)
	 */
	public void addListener(IUpdateListener listener) {
		synchronized(this) {
			if(this.listeners==null)
				this.listeners = new ArrayList<IUpdateListener>();
			
			this.listeners.add(listener);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.IConfNode#removeListener(com.i3xx.util.ctree.IUpdateListener)
	 */
	public void removeListener(IUpdateListener listener) {
		if(this.listeners==null)
			return;
		
		this.listeners.remove(listener);
	}
	
	/**
	 * Fires the update event to all update listeners
	 * 
	 * @param oldValue The old value
	 */
	private void fireUpdateEvent(String oldValue) {
		if(this.listeners==null)
			return;
		
		for(int i=0;i<listeners.size();i++){
			listeners.get(i).update(oldValue, this);
		}
	}
}
