package org.i3xx.util.rna.core;
import java.io.Serializable;
import java.util.*;

/**
 *	Belegung individueller Flags. Zul�ssig ist der Bereich von
 *	0x10000 - 0x2000000 (14 Flags)
 *
 */
public interface IBrick extends Cloneable, Serializable {
	
	//abstract elements
	public static final String classdef = "org.i3xx.util.rna.impl.CBrick";
	//concrete elements
	public static final String createdef = "org.i3xx.util.rna.impl.CDocument";
	//Elements to be searched (mbl::/cdata/cdata[ID==0L])
	public static final String querydef = "cdata";
	//Separator RegExp for airclassname
	public static final String airclassname = "[\\s,]{1,}";
	//Separator for group roles
	public static final String roleSeparator = "|";
	
	//Temporary Hilfskonstanten fuer den Uebergang vom Klassenkonzept
	//zu com.i3xx.ob.proc.db.CData fuer alle Datenobjekte
	//I don't know what's wrong, FIXME
	public static final String QUERY_UNDEFINED = "QUERY_IS-UNDEFINED";
	//Query stmt
	public static final String QUERYDEF = "QUERY";
	//The query is "ID() == [stmt]L;
	public static final String QUERYID = "QUERYID";
	
	//Temporary classname
	public static final String TEMP_CLASS = "temporare";
	public static final String GENERIC_CLASS = "generic";
	
	//
	public static final long ID_CREATEID = 0; //Create a new id (persistent numbers)
	public static final long ID_TEMPNODE = 1; //Create a new id (1)
	public static final long ID_CREATECID = 2; //Create a new id (cache numbers)
	public static final long ID_CREATETID = 3; //Create a new id (temp numbers)
	public static final long ID_CREATESID = 4; //Create a new id (search set numbers)
	
	//State of the object. The flags are: 1|S|C|T|X|0000
	//S=search(0x80), C=cache(0x40), T=transient(0x20), X=created(0x10)
	public static final int IS_PERSISTENT_NOT_CREATED = 0x100|0x1;
	public static final int IS_PERSISTENT = 0x100|0x10|0x2;
	public static final int IS_TRANSIENT_CACHED_NOT_CREATED = 0x100|0x40|0x20|0x3;
	public static final int IS_TRANSIENT_CACHED = 0x100|0x40|0x20|0x10|0x4;
	public static final int IS_TRANSIENT_NOT_CREATED = 0x100|0x20|0x5;
	public static final int IS_TRANSIENT = 0x100|0x20|0x10|0x6;
	public static final int IS_TRANSIENT_WITHOUT_ID = 0x100|0x20|0x7;
	public static final int IS_PERSISTENT_SEARCH_NOT_CREATED = 0x100|0x80|0x8;
	public static final int IS_PERSISTENT_SEARCH = 0x100|0x80|0x10|0x9;
	public static final int IS_UNDEFINED = 0;
	
	public static final int ALL_FLAGS = -1; // All flags are set
	public static final int HIDDEN = 0x1;	// Hidden data
	public static final int CHILDREN = 0x2; // Has children
	public static final int LINK = 0x4; 	//
	public static final int DOCUMENT = 0x8; // Contains a document
	public static final int REMOVE = 0x10;	// Ready to be deleted
	public static final int NOT_IN_ROOT = 0x20; // The element is not member of the root collection
	public static final int AVOID_CLONE_CHILDREN = 0x40;   // Do not clone the children
	public static final int UPDATE_IN_STRUCTURE = 0x80; // Changes in the tree structure are made
	public static final int IS_CLONED = 0x100; // 0x100
	public static final int IS_MUTATED = 0x200; //0x200
    public static final int HISTORY = 0x400; // 0x400 This is a history
    public static final int READONLY = 0x800; // 0x800 This dataset is readonly
    public static final int WRITABLE = 0x1000; // 0x1000 Used to update a readonly dataset
    public static final int CLONE_DOC_TEMPLATE = 0x2000;  // 0x2000
    public static final int CLONE_FOR_HISTORY = 0x4000;  // 0x4000
	public static final int TRANSIENT = 0x8000; // 0x8000 Do not store this dataset.
	public static final int GARBAGE = 0x10000; // 0x10000 This dataset can be garbagecollected.
	// 0x20000
	// 0x40000
	// 0x80000
	// 0x100000;
	// 0x200000;
	// 0x400000;
	// 0x800000;
	// 0x1000000
	// 0x2000000
	public static final int EXTERN = 0x4000000;    // 0x4000000 The document is stored in normal data
    public static final int MARKED = 0x8000000;    // ??? unused ?
    /** @deprecated */ public static final int DETAIL = 0x10000000;   // 
    /** @deprecated */ public static final int SPARTE = 0x20000000;   // 
	public static final int DIRTY = 0x40000000;    // available changes 
	// 0x80000000  Used to add/remove flags
	
	public static final String sendToHost = "*** Host update. ***";
	
	public static final int MAX_ROOT_DEPTH = 100;
	
	public static final String FIELD_ID = "id";
	public static final String FIELD_KEY = "key";
	public static final String FIELD_MAIN = "main";
	public static final String FIELD_PARENT = "parent";
	public static final String FIELD_NAME = "name";
	public static final String FIELD_CLASSNAME = "classname";
	public static final String FIELD_AIRNAME = "airname";
	public static final String FIELD_ASPECT = "aspect";
	public static final String FIELD_INTERFACES	= "interfaces";
	public static final String FIELD_SUPERCLASSES	= "superclasses";
	public static final String FIELD_USER = "user";
    public static final String FIELD_OWNER = "owner";
    public static final String FIELD_GROUP = "group";
	public static final String FIELD_CHILDFACTORY = "childfactory";
	public static final String FIELD_OBTIMESTAMP = "obtimestamp";
	public static final String FIELD_CREATETIMESTAMP = "createtimestamp";
	public static final String FIELD_INDEX_N = "index";
	public static final String FIELD_TRANSID = "transid";
	public static final String FIELD_VALIDITYBEGINNING = "validitybeginning";
	public static final String FIELD_FILERECORD = "filerecord";

	public static final String METHOD_INTERN = "intern";
	public static final String METHOD_INTERN_BLOCKED = METHOD_INTERN; //future use to block operations
	public static final String METHOD_STRUCT = "struct";
	public static final String METHOD_COMMAND = "command";
	public static final String METHOD_HISTORY = "history";
	public static final String METHOD_DOCUMENT = "document";
	
	public static final String RESERVED_ATTRIBUTES_PREFIX = "@";   //reserved prefix for attributes
	public static final String RESERVED_ATTRIBUTES = "attributes"; //reserved fieldname for attributes
	public static final String RESERVED_REFERENCES = "references"; //reserved fieldname for references
	
	/**
	 *	Returns the OfficeBase4 GUID of this node.
	 *	Note: Every node has its own global unique identifiern independent of the java id
	 */
	long ID();

	/**
	 *	Returns the parent instance of this node
	 *	Note: Every node can have one parent ore none. In this case the method returns null.
	 */
	IBrick parent();

	/**
	 *	Here you can set the parent node.
	 *	Note: Don't use this method. The worst, you can get loops while iterating.
	 */
	void parent(IBrick brick);

	//---
	
	/**
	 *	Returns the iterator of child nodes. Children are used for 1:n relations
	 */
	@SuppressWarnings("rawtypes")
	Iterator getIterator();

	/**
	 *	Returns the iterator of proxy nodes. Proxies are used for n:1 relations
	 */
	@SuppressWarnings("rawtypes")
	Iterator getBackpropIterator();

	/**
	 *	Returns the iterator of updated property names (transient map)
	 */
	@SuppressWarnings("rawtypes")
	Iterator getUpdateIterator();
	
	//---
	
	/**
	 * Returns the origin and the updated value of the specified field if available.
	 * 
	 * @param key
	 * @return
	 */
	Object[] getUpdateValue(String key);
	
	//---
	
	/**
	 *	Set the name property.
	 *	Note: This property is used for node personalizing.
	 */
	void name(String n);

	/**
	 *	Get the name property.
	 *	Note: This property is used for node personalizing.
	 */
	String name();

	/**
	 *	Set the classname property.
	 *	Note: This property is the representation for what a node stands for.
	 *		  The same classtype can represent several types of node in OfficeBase4.
	 */
	void classname(String cn);

	/**
	 *	Get the classname property.
	 *	Note: This property is the representation for what a node stands for.
	 *		  The same classtype can represent several types of node in OfficeBase4.
	 */
	String classname();
	
	/**
	 * Air name, classname or current aspect (transient field)
	 * Note: Use airclassname only for field writing methods or classes or corresponding read. 
	 *       Do not use for search, match, common read fields or similar purposes.
	 *       Airclassname can be a list of classnames, the first one is used.
	 * 
	 * @param cn
	 */
	void airclassname(String cn);
	
	/**
	 * Air name, classname or current aspect (transient field)
	 * Note: Use airclassname only for field writing methods or classes or corresponding read. 
	 *       Do not use for search, match, common read fields or similar purposes.
	 *       Airclassname can be a list of classnames, the first one is used.
	 * 
	 * @return
	 */
	String airclassname();
	
	/**
	 * Set the aspect property.
	 * Note: The aspect contains a list of aspect names. An aspect is a further
	 *       classname similar to the classname property
	 * 
	 * @param cn
	 */
	void aspect(String as);
	
	/**
	 * Get the aspect property.
	 * Note: The aspect contains a list of aspect names. An aspect is a further
	 *       classname similar to the classname property
	 * 
	 * @return
	 */
	String aspect();

	/**
	 *	Set the ostimestamp property
	 *	Note: This property is changed to the actual time, everytime a change occurs.
	 */
	void obtimestamp(String ts);
	/**
	 * Get the obtimestamp property
	 * @return
	 */
	String obtimestamp();
	
	//This is announced for M4
	//
	///**
	// *	Set the createtimestamp property
	// *	Note: This property is changed to the actual time, only when an object is created.
	// */
	//void createtimestamp(String ts);
	///**
	// * Get the createtimestamp property
	// * @return
	// */
	//String createtimestamp();
	//
	
	/**
	 *	Set the user property
	 *	Note: This property is changed to the actual user, everytime a change occurs.
	 */
	void user(String value);
	
	/**
	 * Get the user property
	 * @return
	 */
	String user();
    
    /**
     *  Set the owner property
     */
    void owner(String value);
    
    /**
     * Get the owner property
     * 
     * @return
     */
    String owner();
    
    /**
     *  Set the group property
     */
    void group(String value);
    
    /**
     *  Get the group property
     * 
     * @return
     */
    String group();
    
	/**
	 *	Test to the transid property
	 *	Note: You get the TransactionException every time the parameter is less than
	 *		  the intern counter.
	 *  Note: Treat the value as a long, in future it will change to a long.
	 */
	void transid(int id) throws org.i3xx.util.rna.core.TransactionException;

	/**
	 *	Get the transid property
	 *  Note: Treat the value as a long, in future it will change to a long.
	 */
	int transid();

	/**
	 *	Set the transid (timeline) property
	 *  Note: Treat the value as a long, in future it will change to a long.
	 */
	void setTransID(int id);
	
	/**
	 * Returns the value of a function
	 */
	Object getValueF(String symbol);
	
	/**
	 * Set the child factory property
	 */
	void childFactory(String value);
	
	/**
	 * Get the child factory property
	 */
	String childFactory();
	
	/**
	 * Appends a file record entry 
	 */
	void appendFileRecord(String value);
	
	/**
	 * get all file record entries
	 */
	String[] fileRecord();
	
	/**
	 *	Returns a list of all OfficeBase4 properties.
	 */
	String[] getProperties();
	
	//---
	
	/**
	 * Return true if superclasses or interfaces contain the value.
	 * @param classname
	 * @return
	 */
	boolean isInstance(IType type);
	
	//---
	
	/**
	 *	The intern iterator of the command pattern.
	 *	Note: If pre is true, first comes the iteration and then the call to
	 *		  execute(this), otherwise the other way round.
	 */
	void iterate(CCommand cmd, boolean pre);

	/**
	 *	The interface for the command pattern
	 */
	void execute(CCommand cmd);

	/**
	 *	The interface for the visitor pattern
	 */
	void visit(IVisitee visitor) throws Exception;
	
	//---
	
	/**
	 *	Returns the clone (interface Cloneable)
	 */
	Object clone() throws CloneNotSupportedException;

	/**
	 *	After this call, the next call to the clone method results in a clone without children.
	 */
	void setAvoidCloneChildren();

	/**
	 *	Add one node as a children
	 */
	void add(IBrick brick);

	/**
	 *	This node becomes a children of the given node.
	 */
	void move(IBrick brick);

	/**
	 *	Remove the given node from the children collection of this node.
	 */
	void removeReference(IBrick brick);

	/**
	 *	Does nothing.
	 */
	void remove();

	/**
	 *	Deletes the node
	 */
	void delete();

	//---
	
	/**
	 *	Marks this node as hidden
	 */
	void hide();

	/**
	 *	Marks this node as visible
	 */
	void show();

	//---
	
	/**
	 *	Returns the proxy bound to the given field
	 */
	IProxy getProxy(String feld);

	/**
	 *	Removes the given proxy from the collection
	 */
	void removeProxy(IProxy proxy);

	/**
	 *	Updates the value of the proxy bound to the given field.
	 */
	void update(String feld);

	//---
	
	/**
	 *	The factory have to call this method before changing properties.
	 */
	void edit() throws XMLException;

	/**
	 *	The factory have to call this method after changing properties
	 */
	void verify() throws XMLException;

	/**
	 *	The factory have to call this method after the last change has taken place.
	 */
	void clean() throws XMLException;
	
	/**
	 *	Returns the dirty flag (IBrick.DIRTY) or 0
	 *	Note: This method is used for indexing purposes
	 */
	int dirty();
	
    /**
     *  Returns true if the readonly flag is set and the time passed 
     */
    boolean readonly();
    
	//---
	
	/**
	 *	Sets a string value build from several fields
	 */
	void setIndex(String value, int type);

	/**
	 *	Returns a string value build from several fields
	 */
	String getIndex(int type);

	//---
	
	/**
	 *	Returns the given flag if set or 0
	 */
	int getFlag(int flag);

	/**
	 *	Returns true if the given flag is set.
	 */
	boolean isFlag(int flag);

	/**
	 *	Returns all flags as an integer value
	 */
	int getFlags();
	
	/**
	 * removes References to remove a Clone not used in History
	 */
	void clear();
}
