package org.i3xx.util.ctree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.i3xx.util.ctree.core.IResolveRaw;
import org.i3xx.util.ctree.core.IUpdateListener;
import org.i3xx.util.ctree.core.ResolverException;

public final class FinalConfNode implements IConfNode, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4211279825037959349L;
	
	/** The name index of the child nodes */
	protected final Map<String, IConfNode> nodes;
	/** The array of the child nodes */
	protected final List<IConfNode> nodix;
	
	/** The root of the node tree */
	protected final IConfNode root;
	/** The parent of the node */
	protected final IConfNode parent;
	/** The name of the node */
	protected final String name;
	/** The value of the node (raw value) */
	protected final String value;
	
	/** True if the raw value is resolved */
	protected final boolean resolved;
	/** The resolver for this node */
	private final IResolveRaw resolver;
	
	/**
	 * Constructor to build the new tree of final nodes.
	 *  
	 * @param node The IConfNode node to be used to create the final node
	 * @param parent The final node created before
	 * @throws ResolverException
	 */
	protected FinalConfNode(IConfNode node, IConfNode parent) throws ResolverException {
		
		this.root = parent==null ? this : parent.root();
		this.parent = parent;
		this.name = node.name();
		this.value = node.rawValue();
		this.resolved = true;
		this.resolver = node.resolver()!=null ? new MyResolver( node.resolver().resolve(this) ) : null;
		
		//create the child list
		List<IConfNode> list = new ArrayList<IConfNode>();
		Iterator<IConfNode> iter = node.getChildNodes();
		while(iter.hasNext()) {
			IConfNode child = iter.next();
			list.add( new FinalConfNode(child, this) );
		}//while
		
		Map<String, IConfNode> t_nodes = new TreeMap<String, IConfNode>();
		List<IConfNode> t_nodix = new ArrayList<IConfNode>();
		for(IConfNode n : list) {
			t_nodix.add(n);
			t_nodes.put(n.name(), n);
		}
		
		this.nodes = Collections.unmodifiableMap(t_nodes);
		this.nodix = Collections.unmodifiableList(t_nodix);
	}

	public void name(String newValue) {
		throw new UnsupportedOperationException("The update of this final node is not supported.");
	}


	public String name() {
		return name;
	}


	public void value(String newValue) {
		throw new UnsupportedOperationException("The update of this final node is not supported.");
	}


	public String rawValue() {
		return value;
	}


	public String value() {
		try {
			return resolver==null ? value : resolver.resolve(this);
		} catch (ResolverException e) {
			return value;
		}
	}


	public boolean value(boolean def) {
		String value = value();
		if(value==null)
			return def;
		if(value.equals(""))
			return def;

		return Boolean.valueOf(value).booleanValue();
	}


	public byte value(byte def) {
		String value = value();
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


	public int value(int def) {
		String value = value(); 
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


	public long value(long def) {
		String value = value(); 
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


	public float value(float def) {
		String value = value(); 
		try {
			return Float.parseFloat(value);
		}catch( Exception e ) {
			return def;
		}
	}


	public double value(double def) {
		String value = value(); 
		try {
			return Double.parseDouble(value);
		}catch( Exception e ) {
			return def;
		}
	}


	public void parent(IConfNode newValue) {
		throw new UnsupportedOperationException("The update of this final node is not supported.");
	}


	public IConfNode parent() {
		return parent;
	}


	public void root(IConfNode newValue) {
		throw new UnsupportedOperationException("The update of this final node is not supported.");
	}


	public IConfNode root() {
		return root;
	}


	public String getPath() {
		if(parent == null){
			return "";
		}else if(parent == root){
			return "";
		}else{
			String temp = parent.getPath()+"."+parent.name();
			if(temp.startsWith("."))
				temp = temp.substring(1);
			
			return temp;
		}
	}


	public String getFullName() {
		String temp = getPath();

		if(temp.equals(""))
			return name;
		
		return temp + "." + name;
	}


	public void add(IConfNode node) {
		throw new UnsupportedOperationException("The update of this final node is not supported.");
	}


	public IConfNode create(String path) {
		throw new UnsupportedOperationException("The update of this final node is not supported.");
	}


	public IConfNode copy(IConfNode dest) {
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


	public IConfNode update(IConfNode dest) {
		throw new UnsupportedOperationException("The update of this final node is not supported.");
	}


	public IConfNode get(String path) throws NoSuchElementException {
		int depth = 0;
		String[] pathElems = null;
		IConfNode rt = root;

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
			refer = nodes.get(pathElems[i++]);

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


	public IConfNode child(String name) throws NoSuchElementException {
		IConfNode node = nodes.get(name);
		
		if(node==null){
			String path = getFullName() + "." + name;
			throw new NoSuchElementException("'"+path+"'");
		}
		return node;
	}


	public boolean isLeafNode() {
		return (nodix.size() == 0);
	}
	

	public boolean hasChildNode(String name) {
		return nodes.containsKey(name);
	}


	public int size() {
		return nodix.size();
	}


	public void remove(IConfNode node) throws NoSuchElementException {
		throw new UnsupportedOperationException("The update of this final node is not supported.");
	}


	public IConfNode getChildNode(String name) {
		return nodes.get(name);
	}


	public IConfNode getChildNode(int index) {
		return nodix.get(index);
	}


	public Iterator<String> getChildNodeNames() {
		return nodes.keySet().iterator();
	}


	public Iterator<IConfNode> getChildNodes() {
		return nodix.iterator();
	}


	public IConfNode[] getChildArray() {
		IConfNode[] nodes = new ConfNode[nodix.size()];
		nodix.toArray(nodes);
		return nodes;
	}


	public String[] getChildValues() {
		ArrayList<String> v = new ArrayList<String>();
		
		IConfNode[] nodes = getChildArray();
		for(int i=0;i<nodes.length;i++)
			if(nodes[i].isLeafNode())
				v.add(nodes[i].value());
		
		String[] values = new String[v.size()];
		v.toArray(values);
		return values;
	}


	public void reconf() throws ResolverException {
		throw new UnsupportedOperationException("The update of this final node is not supported.");
	}


	public void resolved(boolean resolved) {
		throw new UnsupportedOperationException("The update of this final node is not supported.");
	}


	public boolean resolved() {
		return resolved;
	}


	public void resolver(IResolveRaw resolver) {
		throw new UnsupportedOperationException("The update of this final node is not supported.");
	}


	public IResolveRaw resolver() {
		return resolver;
	}


	public void addListener(IUpdateListener listener) {
		throw new UnsupportedOperationException("The update of this final node is not supported.");
	}


	public void removeListener(IUpdateListener listener) {
		throw new UnsupportedOperationException("The update of this final node is not supported.");
	}
	
	private class MyResolver implements IResolveRaw {
		
		private final String _value;
		
		public MyResolver(String value) {
			_value = value;
		}
		
	
		public Object clone() throws CloneNotSupportedException {
			return super.clone();
		}
	
		public String resolve(IConfNode node) throws ResolverException {
			return _value;
		}		
	} /* CLASS */
}
