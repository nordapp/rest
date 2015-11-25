package org.i3xx.util.rna.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.i3xx.util.rna.core.AccessViolationException;
import org.i3xx.util.rna.core.CCommand;
import org.i3xx.util.rna.core.IBrick;
import org.i3xx.util.rna.core.IBrickInterna;
import org.i3xx.util.rna.core.IProxy;
import org.i3xx.util.rna.core.IRole;
import org.i3xx.util.rna.core.IType;
import org.i3xx.util.rna.core.IVisitee;
import org.i3xx.util.rna.core.TransactionException;
import org.i3xx.util.rna.core.XMLException;
import org.i3xx.util.rna.engine.server.CIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 *	CBrick ist die Oberklasse aller Knoten. Im Konstruktor wird das
 *	Segment angegeben, in dem das Objekt gespeichert werden soll.
 *
 *	Um ausreichend Platz f�r sp�ter hinzukommende Objekte bestehender
 *	Strukturen (Kunden, Akten, o.�.) zu erhalten, m�ssen rechtzeitig die
 *	hinzukommenden Wurzelknoten in neuen Segmenten abgelegt werden.
 *
 *	Die Ablage von Knoten eines Teilbaumes in unterschiedlichen Segmenten
 *	ist zu vermeiden.
 *
 *	IN CBRICK UND ALLEN UNTERKLASSEN M�SSEN DIEJENIGEN METHODEN UND
 *	EIGENSCHAFTEN, DIE �BER OBJECT STORE ABGEFRAGT WERDEN SOLLEN
 *	ALS  P U B L I C  DEKLARIERT SEIN.
 */
@SuppressWarnings({ "unchecked", "serial" })
public abstract class CBrick implements IBrick, IBrickInterna {
	
	private static final Logger logger = LoggerFactory.getLogger(CBrick.class);
	
	// m_osvector speichert die Referenzen der Kindobjekte
	private List<IBrick> m_osvector;
	// m_proxies speichert die Referenzen der Proxies
	private List<IProxy> m_proxies;
	// m_rights speichert die privaten Rechte
	@SuppressWarnings("rawtypes")
	private Map m_rights;
	// m_parent enth�lt den Verweis auf das Eltern-Objekt
	private IBrick m_parent;
	// m_ID dient der eindeutigen Identifizierung eines Objektes.
	private long m_ID;
	// sClassname speichert die "Klasse" des realen Objektes (z.B. Vertrag, Kunde, etc.)
	private String sClassname;
	// sAspect speichert die Aspekte des realen Objekts (statische Schnittstellen)
	private String sAspect;
	// sUser speichert den Namen des Users der letzten �nderung
	private String sName;
    // sOwner speichert die ID des Users der den Knoten angelegt hat
    private long lOwner;
	// sObtimestamp speichert ein �nderungsdatum der Objekte
	private String sObtimestamp;
	//
	//This is announced for M4
	// sCreatetimestamp speichert das Erstelldatum der Objekte
	//private String sCreatetimestamp;
	//
	// sUser speichert die Benuzerdaten der letzten �nderung
	private String sUser;
    // sGroup speichert die zugeordneten Gruppen (ID's)
    private long[] lGroup;
    // sChildfactory speichert die Childfactory
	private String sChildfactory;
    // transID speichert den Transaktionsstand
	private int transID;
	// u.a. dirty speichert den �nderungsstatus
	private int flags;
	// indices
	private String[] indices;
	// Aktenspiegel auf Objektebene
	private String[] sFileRecord;
	// aspect; comma separated classnames
	private String sAirclassname = null;
	
	// m_update sammelt die aktuellen �nderungen und wird nicht dauerhaft gespeichert
	private transient Map<String, Object[]> m_updates; //D�rfen gem. der Updatepolicy nur im Update 
	@SuppressWarnings("rawtypes")
	private transient Map m_interna; //gesetzt sein. Zum Lesen m�ssen beide null sein.
	
	//Interne Mechanismen zur Umgehung von Beschr�nkungen und/oder Mechanismen
	protected transient volatile boolean createNoId;
	protected transient volatile boolean openAllRights;
	protected transient volatile boolean avoidCloneChildren;
	
	/*
	 *	Konstruktor
	 *
	 *	Der erste Adressraum 0-4294967295 bleibt internen Sonderadressen
	 *	vorbehalten.
	 *	0 - erstellt eine neue ID aus dem g�ltigen Adressraum
	 *	1 - kennzeichnet transiente, tempor�re Knoten ohne eigene ID (=1)
	 *	2 - kennzeichnet gecachte transiente Knoten
	 *  3 - kennzeichnet transiente, tempor�re Knoten
	 */
	@SuppressWarnings("rawtypes")
	public CBrick(String name, String classname, Long id) {
		m_osvector = new ArrayList<IBrick>();
		m_proxies = new ArrayList<IProxy>();
		m_rights = new HashMap();
		m_parent = null;
		
		if(id.longValue()==ID_CREATEID){
			m_ID = CIdentifier.ID();
		}else if(id.longValue()==ID_CREATECID){
			m_ID = CIdentifier.CID();
		}else if(id.longValue()==ID_CREATETID){
			m_ID = CIdentifier.TID();
		}else if(id.longValue()==ID_CREATESID){
			m_ID = CIdentifier.SID();
		}else{
			m_ID = id.longValue();
		}
		
		sClassname = classname;
		sName = name;
		sObtimestamp = "";
		//
		//This is announced for M4
		//sCreatetimestamp = String.valueOf( System.currentTimeMillis() );
		//
		sAspect = "";
		sUser = "";
        lOwner = 0L;
        lGroup = new long[0];
        sChildfactory = "";
		transID = 0;
		flags = IBrick.NOT_IN_ROOT;
		indices = new String[0];
		sFileRecord = new String[0];
		
		createNoId = false;
		openAllRights = false;
		avoidCloneChildren = false;
		
		m_updates = null;
		m_interna = null;
	}
	
	// ------------------------------------------
	// Eigenschaften
	// ------------------------------------------
	
	/**
	 *	Verweis auf ID
	 */
	public long ID(){
		//WICHTIGER HINWEIS: Besteht Zugriff auf einen Knoten,
		//so darf immer die ID gelesen werden.
		ensureAvailable();
		return m_ID;
	}
	// ! ! !   WICHTIGER HINWEIS   ! ! !
	// Die Funktion ID(long l) soll nicht dokumentiert werden.
	// Sie darf nur in der Funktion clone() verwendet werden um die ID
	// zu setzen.
	protected void ID(long lid){
		if(rejectRight(IRole.RIGHT_WRITE, this, IBrick.FIELD_ID))
			return;
			
        m_ID = lid;
    }

	/**
	 *	Verweis auf parent
	 */
	public IBrick parent(){
		if(rejectRight(IRole.RIGHT_LIST, this, IBrick.FIELD_PARENT))
			return null;
			
		return m_parent;
	}
	public void parent(IBrick brick){
		if(rejectRight(IRole.RIGHT_WRITE, this, IBrick.FIELD_PARENT))
			return;
			
		m_parent = brick;
	}

	// ! ! !   WICHTIGER HINWEIS:	! ! !
	// Die Funktion OSVector vector()/proxies() soll nicht dokumentiert werden.
	// Sie darf nur in Unterklassen von CBrick verwendet werden.
	@SuppressWarnings("rawtypes")
	public Collection vector(){
		if(rejectRight(IRole.RIGHT_READ, this, IBrick.METHOD_INTERN))
			return new ArrayList<IBrick>();
			
		return m_osvector;
	}
	@SuppressWarnings("rawtypes")
	public Collection proxies(){
		if(rejectRight(IRole.RIGHT_READ, this, IBrick.METHOD_INTERN))
			return new ArrayList<IBrick>();
			
		return m_proxies;
	}
	@SuppressWarnings("rawtypes")
	public Map rights(){
		if(rejectRight(IRole.RIGHT_READ, this, IBrick.METHOD_INTERN))
			return new HashMap();
		
		return m_rights;
	}
	public Map<String, Object[]> updates(){
		if(rejectRight(IRole.RIGHT_READ, this, IBrick.METHOD_INTERN))
			return new HashMap<String, Object[]>();
			
		return m_updates;
	}
	@SuppressWarnings("rawtypes")
	public Map interna(){
		if(rejectRight(IRole.RIGHT_READ, this, IBrick.METHOD_INTERN))
			return new HashMap();
			
		return m_interna;
	}
    public long[] roles(){
    	if(lGroup==null)
    		lGroup=new long[0];
    	
    	long[] r = new long[lGroup.length+1];
    	r[0] = lOwner;
    	for(int i=0;i<lGroup.length;i++)
    		r[i+1]=lGroup[i];
    	
    	return r;
    }
	// ! ! !   WICHTIGER HINWEIS   ! ! !
	// Die Funktion vector(OSVector)/proxies(OSVector) soll nicht dokumentiert
	// werden. Sie darf nur in der Funktion clone() verwendet werden um
	// den Verweis auf den Originalvektor zu ersetzen.
	@SuppressWarnings("rawtypes")
	public void vector(Collection m){
		if(rejectRight(IRole.RIGHT_CREATE, this, IBrick.METHOD_INTERN))
			return;
			
		m_osvector = (List<IBrick>)m;
	}
	@SuppressWarnings("rawtypes")
	public void proxies(Collection m){
		if(rejectRight(IRole.RIGHT_CREATE, this, IBrick.METHOD_INTERN))
			return;
			
		m_proxies = (List<IProxy>)m;
	}
	@SuppressWarnings("rawtypes")
	public void rights(Map m){
		if(rejectRight(IRole.RIGHT_CHANGERIGHT, this, IBrick.METHOD_INTERN_BLOCKED))
			return;
			
		m_rights = (Map)m;
	}
	@SuppressWarnings("rawtypes")
	public void updates(Map m){
		if(rejectRight(IRole.RIGHT_READ, this, IBrick.METHOD_INTERN))
			return;
			
		m_updates = m;
	}
	@SuppressWarnings("rawtypes")
	public void interna(Map m){
		if(rejectRight(IRole.RIGHT_READ, this, IBrick.METHOD_INTERN))
			return;
			
		m_interna = m;
	}
	
	/**
	 * @param classname
	 * @return
	 */
	public boolean isInstance(IType type){
		return type.isInstance(this);
	}
	
	/**
	 *	Eigenschaft name
	 */
	public void name(String value){
        if(readonly() || rejectRight(IRole.RIGHT_WRITE, this, IBrick.FIELD_NAME))
            return;
        
        //get CData.setValue(String, Object) for description
        Object[] raw = (Object[])updates().get(FIELD_NAME);
        if(raw==null){
            Object org = sName;
            if(org!=null && org.equals(value)){
            }else{
                updates().put(FIELD_NAME, new Object[]{org, value});
            }
        }else{
            Object org = raw[0];
            if(org!=null && org.equals(value)){
                updates().remove(FIELD_NAME);
            }else{
                updates().put(FIELD_NAME, new Object[]{org, value});
            }
        }
        sName = value;
	}
	public String name(){
		if(rejectRight(IRole.RIGHT_READ, this, IBrick.FIELD_NAME))
			return "";
			
		return sName;
	}

	/**
	 *	Eigenschaft classname
	 */
	public void classname(String value){
        if(readonly() || rejectRight(IRole.RIGHT_WRITE, this, IBrick.FIELD_CLASSNAME))
            return;
        
        //get CData.setValue(String, Object) for description
        Object[] raw = (Object[])updates().get(FIELD_CLASSNAME);
        if(raw==null){
            Object org = sClassname;
            if(org!=null && org.equals(value)){
            }else{
                updates().put(FIELD_CLASSNAME, new Object[]{org, value});
            }
        }else{
            Object org = raw[0];
            if(org!=null && org.equals(value)){
                updates().remove(FIELD_CLASSNAME);
            }else{
                updates().put(FIELD_CLASSNAME, new Object[]{org, value});
            }
        }
        sClassname = value;
	}
	public String classname(){
		ensureAvailable();
		return sClassname;
	}
	
	/**
	 * Eigenschaft airclassname 
	 */
	public void airclassname(String cn) {
		sAirclassname = cn;
	}
	public String airclassname() {
		return sAirclassname==null?classname():sAirclassname;
	}

	/**
	 *	Eigenschaft aspect
	 */
	public void aspect(String value){
        if(readonly() || rejectRight(IRole.RIGHT_WRITE, this, IBrick.FIELD_ASPECT))
            return;
        
        //get CData.setValue(String, Object) for description
        Object[] raw = (Object[])updates().get(FIELD_ASPECT);
        if(raw==null){
            Object org = sAspect;
            if(org!=null && org.equals(value)){
            }else{
                updates().put(FIELD_ASPECT, new Object[]{org, value});
            }
        }else{
            Object org = raw[0];
            if(org!=null && org.equals(value)){
                updates().remove(FIELD_ASPECT);
            }else{
                updates().put(FIELD_ASPECT, new Object[]{org, value});
            }
        }
        sAspect = value;
	}
	public String aspect(){
		ensureAvailable();
		return sAspect==null?"":sAspect;
	}

	/**
	 *	Eigenschaft obtimestamp
	 */
	public void obtimestamp(String value){
        if(readonly() || rejectRight(IRole.RIGHT_WRITE, this, IBrick.FIELD_OBTIMESTAMP))
            return;
        
        //get CData.setValue(String, Object) for description
        Object[] raw = (Object[])updates().get(FIELD_OBTIMESTAMP);
        if(raw==null){
            Object org = sObtimestamp;
            if(org!=null && org.equals(value)){
            }else{
                updates().put(FIELD_OBTIMESTAMP, new Object[]{org, value});
            }
        }else{
            Object org = raw[0];
            if(org!=null && org.equals(value)){
                updates().remove(FIELD_OBTIMESTAMP);
            }else{
                updates().put(FIELD_OBTIMESTAMP, new Object[]{org, value});
            }
        }
        sObtimestamp = value;
	}
	public String obtimestamp(){
		if(rejectRight(IRole.RIGHT_READ, this, IBrick.FIELD_OBTIMESTAMP))
			return "";
			
		return sObtimestamp;
	}
	
	//
	//This is announced for M4
	///**
	// *	Eigenschaft createtimestamp
	// */
	//public void createtimestamp(String value){
    //    if(readonly() || rejectRight(IRole.RIGHT_WRITE, this, IBrick.FIELD_CREATETIMESTAMP))
    //        return;
    //    
    //    //get CData.setValue(String, Object) for description
    //    Object[] raw = (Object[])updates().get(FIELD_CREATETIMESTAMP);
    //    if(raw==null){
    //        Object org = sCreatetimestamp;
    //        if(org!=null && org.equals(value)){
    //        }else{
    //            updates().put(FIELD_CREATETIMESTAMP, new Object[]{org, value});
    //        }
    //    }else{
    //        Object org = raw[0];
    //        if(org!=null && org.equals(value)){
    //            updates().remove(FIELD_CREATETIMESTAMP);
    //        }else{
    //            updates().put(FIELD_CREATETIMESTAMP, new Object[]{org, value});
    //        }
    //    }
    //    sCreatetimestamp = value;
	//}
	//public String createtimestamp(){
	//	if(rejectRight(IRole.RIGHT_READ, this, IBrick.FIELD_CREATETIMESTAMP))
	//		return "";
	//		
	//	return sCreatetimestamp;
	//}
	//
	
	/**
	 *	Eigenschaft user
	 */
	public void user(String value){
        if(readonly() || rejectRight(IRole.RIGHT_WRITE, this, IBrick.FIELD_USER))
            return;
        
        //get CData.setValue(String, Object) for description
        Object[] raw = (Object[])updates().get(FIELD_USER);
        if(raw==null){
            Object org = sUser;
            if(org!=null && org.equals(value)){
            }else{
                updates().put(FIELD_USER, new Object[]{org, value});
            }
        }else{
            Object org = raw[0];
            if(org!=null && org.equals(value)){
                updates().remove(FIELD_USER);
            }else{
                updates().put(FIELD_USER, new Object[]{org, value});
            }
        }
        sUser = value;
	}
	public String user(){
		if(rejectRight(IRole.RIGHT_READ, this, IBrick.FIELD_USER))
			return "";
			
		return sUser;
	}

    /**
     *  Eigenschaft owner
     */
    public void owner(String value){
        if(rejectRight(IRole.RIGHT_CHANGERIGHT, this, IBrick.FIELD_OWNER))
            return;
        
        //READONLY GREIFT HIER NICHT - no readonly write deny
        
        //get CData.setValue(String, Object) for description
        Object[] raw = (Object[])updates().get(FIELD_OWNER);
        if(raw==null){
        	//TODO: Sollte dies nicht aus lOwner gebildet werden
            Object org = String.valueOf(lOwner);
            if(org!=null && org.equals(value)){
            }else{
                updates().put(FIELD_OWNER, new Object[]{org, value});
            }
        }else{
            Object org = raw[0];
            if(org!=null && org.equals(value)){
                updates().remove(FIELD_OWNER);
            }else{
                updates().put(FIELD_OWNER, new Object[]{org, value});
            }
        }
        lOwner = (value==null||value.equals("")) ? 0 : Long.parseLong( value );
    }
    public String owner(){
		if(rejectRight(IRole.RIGHT_READRIGHT, this, IBrick.FIELD_OWNER))
			return "";
		
		if(lOwner==0)
			return "";
		
        return String.valueOf(lOwner);
    }

    /**
     *  Eigenschaft group
     */
    public void group(String value){
        if(rejectRight(IRole.RIGHT_CHANGERIGHT, this, IBrick.FIELD_GROUP))
            return;
        
        //READONLY GREIFT HIER NICHT - no readonly write deny
        
        //get CData.setValue(String, Object) for description
        Object[] raw = (Object[])updates().get(FIELD_GROUP);
        if(raw==null){
            Object org = lGroup;
            if(org!=null && org.equals(value)){
            }else{
                updates().put(FIELD_GROUP, new Object[]{org, value});
            }
        }else{
            Object org = raw[0];
            if(org!=null && org.equals(value)){
                updates().remove(FIELD_GROUP);
            }else{
                updates().put(FIELD_GROUP, new Object[]{org, value});
            }
        }
        
        //Separator '|' IBrick.roleSeparator
        if(value!=null && !value.equals("")){
            if(value.startsWith(IBrick.roleSeparator))
                value = value.substring(1);
            if(value.endsWith(IBrick.roleSeparator))
                value = value.substring(0, value.length()-1);
            
            String[] r = value.split(IBrick.roleSeparator);
            lGroup = new long[r.length];
            for(int i=0;i<r.length;i++){
            	lGroup[i] = r[i].equals("") ? 0 : Long.parseLong(r[i]);
            }
        }else{
        	lGroup = new long[0];
        }
    }
    public String group(){
		if(rejectRight(IRole.RIGHT_READRIGHT, this, IBrick.FIELD_GROUP))
			return "";
		
		//kann bei Altdatenbanken null sein, BB 12.09.2008
		if( lGroup == null ){
			return "";
		}
		
		StringBuffer rvalue = new StringBuffer();
		if(lGroup.length>0)
			rvalue.append(lGroup[0]);
		for(int i=1;i<lGroup.length;i++){
			rvalue.append(IBrick.roleSeparator);
			rvalue.append(lGroup[i]);
		}
        return rvalue.toString();
    }
    
	/**
	 *	Eigenschaft transID
	 */
	public void transid(int id) throws TransactionException{
		if(rejectRight(IRole.RIGHT_WRITE, this, IBrick.FIELD_TRANSID))
			return;
			
		if(transID > id)
			throw new TransactionException();
	}

	public int transid(){
		return transID;
	}

	/*
	 * TransaktionsID bei Verarbeitung durch Hostsystem setzen.
	 */
	public void setTransID(int id){
		if(rejectRight(IRole.RIGHT_WRITE, this, IBrick.FIELD_TRANSID))
			return;
			
		transID = id;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrick#childFactory(java.lang.String)
	 */
	public void childFactory(String value) {
        if(readonly() || rejectRight(IRole.RIGHT_WRITE, this, IBrick.FIELD_CHILDFACTORY))
            return;
        
        //get CData.setValue(String, Object) for description
        Object[] raw = (Object[])updates().get(FIELD_CHILDFACTORY);
        if(raw==null){
            Object org = sChildfactory;
            if(org!=null && org.equals(value)){
            }else{
                updates().put(FIELD_CHILDFACTORY, new Object[]{org, value});
            }
        }else{
            Object org = raw[0];
            if(org!=null && org.equals(value)){
                updates().remove(FIELD_CHILDFACTORY);
            }else{
                updates().put(FIELD_CHILDFACTORY, new Object[]{org, value});
            }
        }
        
        //Null anstelle von leer
        value = (value!=null && value.equals(""))?null:value;
        
        sChildfactory = value;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrick#getValueF(java.lang.String)
	 */
	public Object getValueF(String symbol) {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrick#childFactory()
	 */
	public String childFactory() {
		if(rejectRight(IRole.RIGHT_READRIGHT, this, IBrick.FIELD_CHILDFACTORY))
			return "";
		
        return sChildfactory==null?"":sChildfactory;
	}
	
	// ------------------------------------------
	// Aktenspiegel
	// ------------------------------------------
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrick#fileRecord()
	 */
	public String[] fileRecord() {
		if(rejectRight(IRole.RIGHT_READRIGHT, this, IBrick.FIELD_FILERECORD))
			return new String[0];
		
		if(sFileRecord==null)
			return new String[0];
		else
			return sFileRecord;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrick#appendFileRecord(java.lang.String)
	 */
	public void appendFileRecord(String value) {
		
        //READONLY GREIFT HIER NICHT - no readonly write deny
        
        if(rejectRight(IRole.RIGHT_READRIGHT, this, IBrick.FIELD_FILERECORD))
            return;
        
		if(sFileRecord==null || sFileRecord.length==0)
			sFileRecord = new String[]{value};
		else{
			String[] tmp = new String[sFileRecord.length+1];
			System.arraycopy(sFileRecord, 0, tmp, 0, sFileRecord.length);
			tmp[sFileRecord.length] = value;
			sFileRecord = tmp;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrickInterna#resetFileRecord(java.lang.String[])
	 */
	public void resetFileRecord(String[] record) {
		if(readonly() || rejectRight(IRole.RIGHT_CREATE, this, IBrick.METHOD_INTERN))
			return;
			
		sFileRecord = record;
	}
	
	// ------------------------------------------
	// Methoden I (Eigenschaften)
	// ------------------------------------------

	/**
	 *	Eigenschaften abfragen
	 */
	public String[] getProperties() {
		//This is announced for M4
		return new String[]{FIELD_ID,FIELD_PARENT,FIELD_NAME,FIELD_CLASSNAME,FIELD_OBTIMESTAMP,/*FIELD_CREATETIMESTAMP,*/FIELD_USER,FIELD_OWNER,FIELD_GROUP,FIELD_TRANSID,FIELD_INDEX_N};
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrick#getUpdateValue(java.lang.String)
	 */
	public Object[] getUpdateValue(String key) {
		if(rejectRight(IRole.RIGHT_READ, this, IBrick.METHOD_INTERN))
			return new Object[0];
			
		Object[] result = (Object[])m_updates.get(key);
		if(result==null){
			result = new Object[0];
		}
		return result;
	}
	
	// ------------------------------------------
	// Methoden II (Besucher und Iteratoren)
	// ------------------------------------------

	/**
	 *	�bergabepunkt f�r Command-Objekte
	 */
	public void execute(CCommand cmd){
		if(rejectRight(IRole.RIGHT_LIST, this, IBrick.METHOD_COMMAND))
			return;
			
		cmd.execute(this);
	}

	/**
	 *	�bergabepunkt f�r Internen Iterator
	 */
	public void iterate(CCommand cmd, boolean pre){
		if(rejectRight(IRole.RIGHT_LIST, this, IBrick.METHOD_COMMAND))
			return;
			
		IBrick brick;
		@SuppressWarnings("rawtypes")
		Iterator iter;
		if(pre)
			execute(cmd);
		iter = internVector().iterator();
		while (iter.hasNext()) {
			brick = (IBrick)iter.next();
			brick.iterate(cmd, pre);
		}
		if(!pre)
			execute(cmd);
	}

	/**
	 *	�bergabepunkt der externen Iteratorschnittstelle
	 */
	public void visit(IVisitee visitor) throws Exception {
		if(rejectRight(IRole.RIGHT_LIST, this, IBrick.METHOD_COMMAND))
			return;
			
		visitor.visit(this);
	}

	/**
	 *	Iterator auf den internen Vector abfragen
	 */
	@SuppressWarnings("rawtypes")
	public Iterator getIterator(){
		if(rejectRight(IRole.RIGHT_LIST, this, IBrick.METHOD_STRUCT))
			return new ArrayList<IBrick>().iterator();
			
		return internVector().iterator();
	}
	@SuppressWarnings("rawtypes")
	public Iterator getBackpropIterator(){
		if(rejectRight(IRole.RIGHT_LIST, this, IBrick.METHOD_STRUCT))
			return new ArrayList<IBrick>().iterator();
			
		return m_proxies.iterator();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrick#getUpdateIterator()
	 */
	@SuppressWarnings("rawtypes")
	public Iterator getUpdateIterator(){
		if(rejectRight(IRole.RIGHT_WRITE, this, IBrick.METHOD_INTERN))
			return new ArrayList<IBrick>().iterator();
			
		return m_updates.keySet().iterator();
	}
	
	// ------------------------------------------
	// Methoden III (Objektmanipulation)
	// ------------------------------------------

	/**
	 *	Methode zum Vervielf�ltigen
	 */
	public Object clone() throws CloneNotSupportedException{
		/*
		 *	Wichtiger Hinweis
		 *
		 *	Diese Funktion arbeitet wie folgt:
		 *	Zuerst wird eine Kopie dieses Objektes erstellt. Dabei werden die Kindobjekte und der Speicher-
		 *	vektor der Kindobjekte nicht mitkopiert.
		 *	�ber die "interne" Funktion "vector(OSVector)" wird der Verweis durch einen neuen Vektor ersetzt.
		 *	In einer Schleife werden nun alle Kindobjekte ebenfalls kopiert und die Kopien dem neuen Vektor
		 *	�ber die Methode "add(IBrick)" hinzugef�gt. Beachten Sie, da� keinesfalls die Methode ADD der
		 *	Klasse OSVector benutzt werden darf sondern die Methode ADD der Klasse IBrick verwendet werden
		 *	mu�.
		 */
		if(isFlag(IBrick.CLONE_FOR_HISTORY) && rejectRight(IRole.RIGHT_WRITEFIELD, this, IBrick.METHOD_HISTORY))
			throw new CloneNotSupportedException("Missing rights to clone object.");
		else if(rejectRight(IRole.RIGHT_CREATE, this, IBrick.METHOD_STRUCT))
			throw new CloneNotSupportedException("Missing rights to clone object.");
		
		CBrick clone=null;
		IBrick brick=null;

		try {
			// Eine Kopie dieses Objektes wird erstellt,
			openAllRights = true; //Rechtepr�fung ausschalten => Der Clone wird ohne Rechtepr�fung erstellt
			clone = (CBrick)super.clone();
			// parent-Verweis wird gel�scht
			clone.parent(null);
            // Ein etwaiges Flag Readonly wird entfernt.
            clone.removeFlag(IBrick.READONLY);
			// -----------------------------------------------------------
			// AUFGEHOBEN: Da es v�llig unpraktikabel ist, wenn bei einem
			// Kopiervorgang wesentliche Daten (hier Verweise) wie VR,
			// Indices etc. gel�scht werden. Besser ist, diese Daten f�r
			// das Zielsystem mit zu kopieren.
			// Proxies werden gel�scht
			// clone.proxies(new OSVector(1,1));
			// -----------------------------------------------------------
			// Die ID wird gesetzt.
			if(createNoId) {
				//clone.ID(this.ID()); //Diese ist durch clone() bereits gesetzt.
				createNoId = false;
				clone.createNoId = false;
			}else{
				clone.ID(CIdentifier.ID());
			}
			// Der Vektor der Kopie wird durch einen neuen Vektor ersetzt.
			clone.vector(new ArrayList<IBrick>());
			clone.proxies(new ArrayList<IBrick>());
			
			// �nderungen der Kopie einschalten
			try{
				clone.edit();
				clone.setFlag(IBrick.NOT_IN_ROOT);
				// Struktur ver�ndert wird via ServeIndex und cleanup() zur�ckgesetzt. 
				// ServeIndex wird in CBrickProducer rekursiv �ber die gesamte Struktur
				// iteriert.
				clone.setFlag(IBrick.IS_CLONED);
				clone.setFlag(IBrick.UPDATE_IN_STRUCTURE);
				// Externe Knoten werden durch eine Kopie zu internen Knoten
				clone.removeFlag(IBrick.EXTERN);
			}catch(XMLException ee){
				throw new CloneNotSupportedException(ee.toString());
			}
			
			//init-code for old versions of CBrick stored in database
			if(indices==null)
				indices = new String[]{};
			
			// Index ver�ndern, da der Index einmalig sein muss
			for( int i=0; i < indices.length; i++ ){
				clone.setIndex( getIndex(i), i );
			}
			
			// Kindelemente nicht kopieren (Flag auch auf der Kopie zur�cksetzen)
			if(avoidCloneChildren || isFlag(IBrick.AVOID_CLONE_CHILDREN)) {
				avoidCloneChildren = false;
				clone.avoidCloneChildren = false;
				
				logger.trace("Brick.clone original: {}, clone: {}", m_ID, clone.ID());
				return clone;
			}
			
			// Der Iterator des Original-Vektors wird geholt
			@SuppressWarnings("rawtypes")
			Iterator iter = m_osvector.iterator();
			// Schleife
			while (iter.hasNext()){
				// Der Iterator liefert Verweise auf die Kindobjekte
				brick = (IBrick)iter.next();
				//
				if(brick instanceof IProxy) {
					// Proxies d�rfen nicht kopiert werden, der Verweis wird ben�tigt
					// ACHTUNG: n Elemente verweisen auf 1 Proxy (n:1)
					clone.add(brick);
				}else{
					// Kindobjekte werden kopiert und die Kopie wird zur�ckgegeben
					IBrick child = (IBrick)brick.clone();
					// Die kopierten Kindobjekte werden der Kopie dieses Objektes ("this") hinzugef�gt.
					clone.add( child );
				}
			}

			logger.trace("Brick.clone original: {}, clone: {}", m_ID, clone.ID());
		} catch (CloneNotSupportedException e) {
			logger.trace("Brick.clone failed" ,e);
			throw e;
		} finally {
			// Rechtepr�fung einschalten
			openAllRights = false; 
            // Rechtepr�fung im Clone einschalten
            clone.openAllRights = false;
		}
		return clone;
	}

	/**
	 *	Standardmethode zum Hinzuf�gen
	 *	!!! Elemente nicht von IBrick abgeleitet sind nicht zul�ssig !!!
	 */
	public void add(IBrick brick){
		
		//Vermeiden von Schleifen. Dieser Code pr�ft, ob das einzuf�gende
		//Kindelement bereits in der Vorfahrenreihe (zu Root hin) existiert.
		//Einschr�nkung:
		//  Es werden max. 100 Vorfahren gepr�ft
		//  Bestehende Schleifen werden ignoriert
		if(rejectRight(IRole.RIGHT_ADD, this, IBrick.METHOD_STRUCT))
			return;
			
		//Transient or search condition, exit
		if( CIdentifier.isTransient(m_ID) != CIdentifier.isTransient(brick.ID()) )
			return;
		if( CIdentifier.isSearch(m_ID) != CIdentifier.isSearch(brick.ID()) )
			return;
		
		int n = 0;
		IBrick temp = this;
		do{
			//Loop condition, exit
			if(brick.ID() == temp.ID()){
				logger.trace("Brick.add loop condition break; self: {}, child: {}", m_ID, brick.ID());
				return;
			}
			//Da die Pr�fung selbst anf�llig f�r Endlosschleifen ist, wird nach
			//100 Iterationen die Notbremse gezogen. Dadurch werden bestehende
			//Schleifen ignoriert.
			if(n > 100){
				break;
			}
			n++;
			temp = temp.parent();
		}while(temp != null);
		
		//Mu� im Zusammenhang mit der Pr�fung von Dubletten gesehen werden,
		//da parent() in diesem Falle bereits this sein mu� (sich also nichts
		//�ndert, auch wenn kein Element hinzugef�gt wird).
		if( ! (brick instanceof CProxy) )
			brick.parent(this);

		//Dubletten des selben Objekts sind nicht zul�ssig
		if( ! m_osvector.contains(brick))
			m_osvector.add(brick);
		
		//Flag f�r das Vorhandensein von Kindobjekten setzen
		if(!isFlag(CHILDREN))
			setFlag(CHILDREN);
		logger.trace("Brick.add self: {}, child: {}", m_ID, brick.ID());
	}

	/**
	 *	Verschieben an das durch IBrick angegebene Element (Wird als letztes Child angeh�ngt)
	 *  Das �bergebene Element wird PARENT !!!
	 */
	public void move(IBrick brick) {
		if(rejectRight(IRole.RIGHT_ADD, brick, IBrick.METHOD_STRUCT))
			return;
		if(this.parent() != null && rejectRight(IRole.RIGHT_REMOVE, this.parent(), IBrick.METHOD_STRUCT))
			return;
			
		// Struktur ver�ndert (wird via ServeIndex und cleanup() zur�ckgesetzt)
		setFlag(IBrick.UPDATE_IN_STRUCTURE);
		// Aus dem Parent-Element entfernen
		if (m_parent != null)
			m_parent.removeReference(this);
		// an neuem Knoten einf�gen
		if(brick != null)
			brick.add(this);
		logger.trace("Brick.move parent: {}, self: {}", brick==null?"root":String.valueOf(brick.ID()), m_ID);
	}

	/**
	 *	Verweis (Kindelement) entfernen
	 */
	public void removeReference(IBrick brick){
		if(rejectRight(IRole.RIGHT_REMOVE, this, IBrick.METHOD_STRUCT))
			return;
			
		if (brick!=null){
			m_osvector.remove(brick);

			if( ! ( brick instanceof CProxy) )
				brick.parent(null);
		}
		if(isFlag(CHILDREN)&&(m_osvector.size()==0))
			removeFlag(CHILDREN);
		
		logger.trace("Brick.remove reference self: {}, (former) child: {}", m_ID, brick==null?"root":String.valueOf(brick.ID()));
	}

	/**
	 *	L�schen
	 */
	public void delete() {
		if(rejectRight(IRole.RIGHT_DELETE, this, IBrick.METHOD_STRUCT))
			return;
			
		// Ggf. Inhalte l�schen
		remove();
		// Aus dem Parent-Element entfernen
		if (m_parent != null)
			m_parent.removeReference(this);
		//Flags deleted setzen
        setFlag(IBrick.REMOVE);
		//removeReference im parent-Brick setzt entsprechend die Flags dort zur�ck

		//Im Baum darf nur Absteigend gel�scht werden. Die Kindelemente sollten
		//bereits gel�scht sein. Werden diese nicht gel�scht, m�ssen deren
		//Referenzen auf dieses Objekt entfernt werden.
		if(m_osvector.size()>0){
			@SuppressWarnings("rawtypes")
			Iterator iter = getIterator();
			while(iter.hasNext()){
				IBrick brick = (IBrick)iter.next();
				brick.parent(null);
			}
		}
        m_osvector.clear();
        //m_osvector.destroy();

		//Der externe Iterator ruft auf CProxy delete() auf, das den Referenz�hler
		//zur�cksetzt.
		if(m_proxies.size()>0){
			logger.trace("Brick.delete self: {}, proxies count: {}", m_ID, m_proxies.size());
		}
        //ACHTUNG: Dies verletzt die referenzielle Integrit�t.
		//m_proxies.clear();
		//m_proxies.destroy();

		//Intern wird nicht iteriert, da in jedem Fall die Referenzen auf
		//die Auflistungen (Roots) entfernt werden m�ssen. Dies kann nur durch
		//einen externen Iterator geschehen.
		logger.trace("Brick.delete self: {}", m_ID);
	}

	/**
	 *	Standardmethode zum Verbergen
	 */
	public void hide(){
		if(rejectRight(IRole.RIGHT_WRITE, this, IBrick.METHOD_STRUCT))
			return;
			
		setFlag(HIDDEN);
	}

	/**
	 *	Standardmethode zum Hervorholen
	 */
	public void show(){
		if(rejectRight(IRole.RIGHT_WRITE, this, IBrick.METHOD_STRUCT))
			return;
			
		removeFlag(HIDDEN);
	}

	/**
	 *	Standardmethode zum L�schen (does nothing!)
	 */
	public void remove(){}

	/**
	 *	<p>Schaltet das Kopieren von Kindelementen aus.
	 *	Diese Eigenschaft ist nicht persistent (dauerhaft).</p>
	 */
	public void setAvoidCloneChildren() {
		avoidCloneChildren = true;
	}

	/**
	 *	Bereitet eine Daten�nderung vor
	 */
	@SuppressWarnings("rawtypes")
	public void edit() throws XMLException {
		m_updates = new HashMap();
		m_interna = new HashMap();
		
		setFlag(IBrick.DIRTY);
		
		//This doesn't run outside OfficeBase
        //investigate wether the readonly flag has to be set.
        //---------------------------------------------------
        //Das Flag WRITABLE 
        //if( !isFlag(IBrick.WRITABLE) &&
        //        !isFlag(IBrick.READONLY) &&
        //        ReadOnly.test(sClassname, sObtimestamp)){
        //    setFlag(IBrick.READONLY);
        //}
	}

	/**
	 *	Verifiziert die eingegebenen Daten
	 */
	public void verify() throws XMLException {
		//Transaktionsschutz
		if(transID >= (Integer.MAX_VALUE))
			transID = 0;
		
		transID++;
	}

	/**
	 *	Aufr�umen nach einer Daten�nderung
	 */
	public void clean() throws XMLException {
		m_updates = null;
		m_interna = null;
		
		removeFlag(IBrick.IS_CLONED);
		removeFlag(IBrick.UPDATE_IN_STRUCTURE);
		removeFlag(IBrick.DIRTY);
	}

	/**
	 *	Zusammengesetzte Indices schreiben
	 */
	public void setIndex(String value, int type) {
		//init-code for old versions of CBrick
		if(indices==null)
			indices = new String[]{};
		
		if( type >= indices.length){
			String[] arr = new String[type + 1];
			if(indices.length > 0)
				System.arraycopy(indices, 0, arr, 0, indices.length);

			indices = arr;
		}
		
        if(readonly())
            return;
        
        //get CData.setValue(String, Object) for description
        Object[] raw = (Object[])updates().get(FIELD_INDEX_N+String.valueOf(type));
        if(raw==null){
            Object org = indices[type];
            if(org!=null && org.equals(value)){
            }else{
                updates().put(FIELD_INDEX_N+String.valueOf(type), new Object[]{org, value});
            }
        }else{
            Object org = raw[0];
            if(org!=null && org.equals(value)){
                updates().remove(FIELD_INDEX_N+String.valueOf(type));
            }else{
                updates().put(FIELD_INDEX_N+String.valueOf(type), new Object[]{org, value});
            }
        }
        indices[type] = value;
	}

	/**
	 *	Zusammengesetzte Indices lesen
	 */
	public String getIndex(int type) {
		//get index size
		if(type==-1)
			return String.valueOf( indices==null ? 0 : indices.length );
		
		//init-code for old versions of CBrick
		if(indices==null)
			return String.valueOf( m_ID );
		
		//Fehlerbedingung
		if( type >= indices.length )
			return String.valueOf( m_ID );

		return indices[type];
	}

	/**
	 *	�nderungszustand
	 */
	public int dirty(){
		return getFlag(IBrick.DIRTY);
	}
    
    /**
     *  Readonly
     */
    public boolean readonly() {
		//interner Mechanismus um alle Beschr�nkungen (Rechte, Readonly) zu umgehen.
		if(openAllRights){
			return false;
		}else if(isFlag(IBrick.DIRTY)){
            //in edit() the flag readonly is set if necessary
            return isFlag(IBrick.READONLY);
        }else{
            if(isFlag(IBrick.WRITABLE)){
                //overwrites readonly
                return false;
            }else if(isFlag(IBrick.READONLY)){
                return true;
            }else{
            	//This doesn't run outside OfficeBase
                //investigate whether the flag would be set during
                //the node's change.
                //return ReadOnly.test(sClassname, sObtimestamp);
            	return false;
            }
        }
    }
    
	/**
	 *	Proxy zur�ckgeben (ggf. erstellen)
	 */
	public IProxy getProxy(String feld) {
		@SuppressWarnings("rawtypes")
		Iterator iter = m_proxies.iterator();
		IProxy proxy = null;
		while (iter.hasNext()) {
			proxy = (IProxy)iter.next();
			if(proxy.getField().equals(feld))
				return proxy;
		}
		proxy = new CProxy("", "cproxy", new Long(ID_CREATEID));
		proxy.setField(feld);
		proxy.setReference(this);
		proxy.update();
		m_proxies.add(proxy);
		return proxy;
	}

	/**
	 *	Proxy entfernen
	 */
	public void removeProxy(IProxy proxy) {
		proxy.setReference(null);
		m_proxies.remove(proxy);
	}

	// Ausl�ser f�r Update
	// if(!feld_INHALT.equals(newValue))
	//	   update("feld_INHALT");

	/**
	 *	Proxy benachrichtigen
	 */
	public void update(String feld) {
		@SuppressWarnings("rawtypes")
		Iterator iter = m_proxies.iterator();
		IProxy proxy = null;
		while (iter.hasNext()) {
			proxy = (IProxy)iter.next();
			if(proxy.getField().equals(feld)) {
				proxy.update();
				return;
			}
		}
	}

	/**
	 *	Standardmethode f�r Flags
	 */
	public void setFlag(int flag){
        this.flags |= flag;
    }
	public void removeFlag(int flag){
		//Verhindert das Entfernen des Readonly Flags
		if(!openAllRights)
			flag &= ~IBrick.READONLY;
        
        this.flags &= ~flag;
    }
	public int getFlag(int flag){
        return (this.flags & flag);
    }
	public boolean isFlag(int flag){
        return ((this.flags & flag)==flag);
    }
	public int getFlags(){
        return this.flags;
    }

	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrick#clear()
	 */
	public void clear() {
        if(readonly() || rejectRight(IRole.RIGHT_WRITE, this, IBrick.METHOD_INTERN))
            return;
        
        /* TODO Nicht zul�ssig, bei bestehender Persistenz */
        
		// Ggf. Inhalte l�schen
		remove();
		// Parent-Referenz entfernen
		this.parent(null);
		//Referenzen Kinder entfernen werden.
		this.vector().clear();
		//Referenzen auf Proxies l�schen
		this.proxies().clear();
	}
	
	/**
	 * 
	 */
	protected void ensureAvailable() {
		//does nothing here
	}
	
	/**
	 * @param askedRight
	 * @param brick
	 * @param type
	 * @return
	 */
	protected boolean rejectRight(int askedRight, IBrick brick, String type) {
		//interner Mechanismus um alle Beschr�nkungen (Rechte, Readonly) zu umgehen.
		if(openAllRights)
			return false;
		
		//does nothing here
		return false;
	}
	
	/**
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected Collection internVector() {
		throw new UnsupportedOperationException();
		//return ( sChildfactory==null || sChildfactory.equals("") ? m_osvector : 
		//			ChildFactoryFactory.get(sChildfactory).getChildren(this, m_osvector) );
	}
	
	/**
	 * Standardmethode f�r die Rechtepr�fung mit Exception und Zugriff �ber rejectRight().
	 * 
	 * Diese Methode darf prinzipbedingt nur innerhalb der CBrick-Klassen und der zu CBrick
	 * geh�renden Factory verwendet werden. Siehe auch die aquivalente, jedoch allgemeine
	 * Methode in RoleHandler.
	 * 
	 * @param askedRight
	 * @param brick
	 * @param type
	 */
	public static final void testAccess(int askedRight, CBrick brick, String type) {
		
		if( brick.rejectRight(askedRight, brick, type) ){
			
			AccessViolationException ave = new AccessViolationException("Missing right:"+
					askedRight+", classname:"+brick.classname()+", id:"+brick.ID()+", type:"+type);
			
			ave.setId( brick.ID() );
			ave.setClassname( brick.classname() );
			ave.setAskedRight(askedRight);
			ave.setType(type);
			
			throw ave;
		}
	}
}
