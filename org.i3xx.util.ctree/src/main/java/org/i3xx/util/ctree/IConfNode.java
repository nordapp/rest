package org.i3xx.util.ctree;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.i3xx.util.ctree.core.IResolveRaw;
import org.i3xx.util.ctree.core.IUpdateListener;
import org.i3xx.util.ctree.core.ResolverException;


public interface IConfNode
{
	/**
	 * @param newValue The name of the node
	 */
	void name(String newValue);
	
	/**
	 * @return The name of the node
	 */
	String name();
	
	/**
	 * Sets the value.
	 * Note: Set the resolver to null and run the NodeParser with a valid factory.
	 * 
	 * @param newValue The value of the node
	 */
	void value(String newValue);
	
	/**
	 * @return The value without using a resolver
	 */
	String rawValue();
	
	/**
	 * @return The value of the node
	 */
	String value();
	
	/**
	 * @param def The default value
	 * @return The boolean representation of the value
	 */
	boolean value(boolean def);
	
	/**
	 * @param def The default value
	 * @return The byte representation of the value
	 */
	byte value(byte def);
	
	/**
	 * @param def The default value
	 * @return The int representation of the value
	 */
	int value(int def);
	
	/**
	 * @param def The default value
	 * @return The long representation of the value
	 */
	long value(long def);
	
	/**
	 * @param def The default value
	 * @return The float representation of the value
	 */
	float value(float def);
	
	/**
	 * @param def The default value
	 * @return The double representation of the value
	 */
	double value(double def);
	
	/**
	 * @param newValue The parent of the node
	 */
	void parent(IConfNode newValue);
	
	/**
	 * @return The parent of the node
	 */
	IConfNode parent();
	
	/**
	 * @param newValue The root of the tree
	 */
	void root(IConfNode newValue);
	
	/**
	 * @return The root of the tree
	 */
	IConfNode root();
	
	/**
	 * @return The path of the node
	 */
	String getPath();
	
	/**
	 * @return The path and name of the node
	 */
	String getFullName();
	
	/**
	 * @param node Adds a node
	 */
	void add(IConfNode node);
	
	/**
	 * Creates a new node by a given path
	 * @param path The path to create
	 * @return
	 */
	IConfNode create(String path);
	
	/**
	 * Builds a new copy (clone) of the branch (descendant)
	 * @param dest The destination
	 * @return The copy of the current node
	 */
	IConfNode copy(IConfNode dest);
	
	/**
	 * Updates the destination node by this branch (descendant).
	 * @param dest
	 * @return The dest node
	 */
	IConfNode update(IConfNode dest);
	
	/**
	 * @param path The path
	 * @return Search a node by a given path
	 * @throws NoSuchElementException
	 */
	IConfNode get(String path) throws NoSuchElementException;
	
	/**
	 * @param name The name of the child node
	 * @return The child node
	 * @throws NoSuchElementException
	 */
	IConfNode child(String name) throws NoSuchElementException;
	
	/**
	 * @return True if a node has no children (size()==0)
	 */
	boolean isLeafNode();
	
	/**
	 * @return The number of children
	 */
	int size();
	
	/**
	 * Removes a child node
	 * @param node The node to remove
	 * @throws NoSuchElementException
	 */
	void remove(IConfNode node) throws NoSuchElementException;
	
	/**
	 * Returns a child node by it's name
	 * @param name The name of the child node
	 * @return The child node or null
	 */
	IConfNode getChildNode(String name);
	
	/**
	 * Returns a child node by it's index
	 * @param index The index of the child node
	 * @return The child node
	 */
	IConfNode getChildNode(int index);
	
	/**
	 * @return The child names iterator
	 */
	Iterator<String> getChildNodes(); // Iterator Ueber Node-Namen !
	
	/**
	 * @return The child nodes iterator
	 */
	Iterator<IConfNode> getChildNodes2();
	
	/**
	 * @return The child nodes as an array
	 */
	IConfNode[] getChildNodes3();
	
	/**
	 * @return The child values as an array
	 */
	String[] getChildValues();
	
	/**
	 * The reconfigure event
	 */
	void reconf() throws ResolverException;

	/**
	 * @param resolved the resolved to set
	 */
	void resolved(boolean resolved);

	/**
	 * @return the resolved
	 */
	boolean resolved();

	/**
	 * @param resolver the resolver to set
	 */
	void resolver(IResolveRaw resolver);

	/**
	 * @return the resolver
	 */
	IResolveRaw resolver();
	
	/**
	 * @param listener The listener to add
	 */
	void addListener(IUpdateListener listener);
	
	/**
	 * @param listener The listener to remove
	 */
	void removeListener(IUpdateListener listener);
}
