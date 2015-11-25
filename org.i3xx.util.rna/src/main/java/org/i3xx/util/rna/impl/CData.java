/*
 * Created on 14.03.2005
 */
package org.i3xx.util.rna.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.i3xx.util.rna.core.IBrick;
import org.i3xx.util.rna.core.IBrickData;
import org.i3xx.util.rna.core.IProxy;
import org.i3xx.util.rna.core.IRole;
import org.i3xx.util.rna.core.UserContentException;

/**
 * @author S. Hauptmann
 */
public class CData extends CBrickHistory implements IBrickData {
	
	//never use the keyword final in object store storeable objects
	
	private static final long serialVersionUID = 1L;
	//m_map beinhaltet die Nutzdaten
	private Map<String, Object> m_map;
	
	// m_result sammelt Zwischenergebnisse und wird nicht dauerhaft gespeichert
	@SuppressWarnings("rawtypes")
	private transient Map m_results;
	
	// m_chunks sammelt Masterknoten aus der Abfrage und wird nicht dauerhaft gespeichert
	@SuppressWarnings("rawtypes")
	private transient List m_chunks;
	private transient int chunkVersion;
	
	/**
	 * @param name
	 * @param classname
	 * @param id
	 */
	public CData(String name, String classname, Long id) {
		super(name, classname, id);
		
		m_map = new HashMap<String, Object>();
		
		m_results = null;
		m_chunks = null;
		chunkVersion = 0;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrickData#getMap()
	 */
	public Map<String, Object> getMap(){
        if(rejectRight(IRole.RIGHT_READFIELD, this, IBrick.METHOD_INTERN))
            return new HashMap<String, Object>();
        
		return m_map;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrickData#results()
	 */
	@SuppressWarnings("rawtypes")
	public Map results() {
        if(rejectRight(IRole.RIGHT_READFIELD, this, IBrick.METHOD_INTERN))
            return new HashMap();
        
		if(m_results==null)
			m_results = new HashMap();
		
		return m_results;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrickData#chunks()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List chunks() {
        if(rejectRight(IRole.RIGHT_READFIELD, this, IBrick.METHOD_INTERN))
            return new ArrayList<IBrick>();
        
		if(m_chunks==null)
			m_chunks = new ArrayList<IBrick>();
		
		return m_chunks;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrickData#getChunkVerion()
	 */
	public int getChunkVerion() {
		return chunkVersion;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrickData#setChunkVerion(int)
	 */
	public void setChunkVerion(int chunkVersion){
		this.chunkVersion = chunkVersion;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrickData#getValue(java.lang.String)
	 */
	public Object getValue(String name) {
		
        if(rejectRight(IRole.RIGHT_READFIELD, this, name))
            return null;
        
        Object result = null;
        try{
        	result = m_map.get(name);
        }catch(RuntimeException e){}
		
		if(result != null){
			return result;
		}else if(name.equals(FIELD_PARENT)){
			return new Long(parent()==null?0:parent().ID());
		}else if(name.equals(FIELD_ID)){
			return new Long(ID());
		}else if(name.equals(FIELD_NAME)){
			return name();
		}else if(name.equals(FIELD_CLASSNAME)){
			return classname();
		}else if(name.equals(FIELD_AIRNAME)){
			return airclassname();
		}else if(name.equals(FIELD_ASPECT)){
			return aspect();
		}else if(name.equals(FIELD_USER)){
			return user();
        }else if(name.equals(FIELD_OWNER)){
            return owner();
        }else if(name.equals(FIELD_GROUP)){
            return group();
        }else if(name.equals(FIELD_CHILDFACTORY)){
            return childFactory();
		}else if(name.equals(FIELD_OBTIMESTAMP)){
			return new Long(obtimestamp());
		//
		//This is announced for M4
		//}else if(name.equals(FIELD_CREATETIMESTAMP)){
		//	return new Long(createtimestamp());
		//
		}else if(name.startsWith(FIELD_INDEX_N)){
			return getIndex( Integer.parseInt( name.substring(5) ) );
		}else if(name.equals(FIELD_TRANSID)){
			return new Integer(transid());
		}else if(name.equals(FIELD_VALIDITYBEGINNING)){
			return validityBeginning();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrickData#getValue(java.lang.String, java.lang.Object)
	 */
	public Object getValue(String name, Object deflt) {
		Object ret = getValue(name);
		
		if(deflt==null)
			throw new IllegalArgumentException("Invalid default value 'null'.");
		if( deflt.getClass().isInstance(ret) )
			return (ret==null?deflt:ret);
		
		//Number conversion
		if(ret instanceof Number) {
			Number num = (Number)ret;
			
			if( deflt instanceof Long )
				return new Long( num.longValue() );
			else if( deflt instanceof Integer )
				return new Integer( num.intValue() );
			else if( deflt instanceof Double )
				return new Double( num.doubleValue() );
			else if( deflt instanceof Float )
				return new Float( num.floatValue() );
		}
		
		return deflt;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrickData#getValue(java.lang.String, java.lang.Integer)
	 */
	public Integer getValue(String name, Integer deflt) {
		return (Integer)getValue(name, (Object)deflt);
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrickData#getValue(java.lang.String, java.lang.Long)
	 */
	public Long getValue(String name, Long deflt) {
		return (Long)getValue(name, (Object)deflt);
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrickData#getValue(java.lang.String, java.lang.Float)
	 */
	public Float getValue(String name, Float deflt) {
		return (Float)getValue(name, (Object)deflt);
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrickData#getValue(java.lang.String, java.lang.Double)
	 */
	public Double getValue(String name, Double deflt) {
		return (Double)getValue(name, (Object)deflt);
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrickData#getValue(java.lang.String, java.lang.String)
	 */
	public String getValue(String name, String deflt) {
		return (String)getValue(name, (Object)deflt);
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrickData#getValue(java.lang.String, com.i3xx.ob.core.IProxy)
	 */
	public IProxy getValue(String name, IProxy deflt) {
		return (IProxy)getValue(name, (Object)deflt);
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrickData#setValue(java.lang.String, java.lang.Object)
	 */
	public void setValue(String name, Object value) throws UserContentException {
		
        if(rejectRight(IRole.RIGHT_WRITEFIELD, this, name))
            throw new UserContentException( "The field '"+name+"' cannot be changed because of insufficient rights.");
        if(readonly())
            throw new UserContentException( "The field '"+name+"' is readonly.");
        if( value == null )
            throw new UserContentException( "The field '"+name+"' cannot be set to the value null." );
        
        //Get old values. Be care that the origin old value
        //is never updated by another update of the field.
        //The origin value is stored at index 0 the new value at index 1
        Object[] raw = (Object[])updates().get(name);
        
        //Do the update if the field should change. Be care of null values.
        if(raw==null){
            //The object get to the list only if it's value has changed.
            Object org = getValue(name);
            //A null value means there is no property. The given value is always
            //different to null.
            if(org!=null && org.equals(value)){
                //does nothing
            }else{
                //changed
                updates().put(name, new Object[]{org, value});
            }
        }else{
            //There is another change e.g the update from blogic or a
            //calculation result. Store the origin and the latest value.
            Object org = raw[0];
            //A value is set to the list after it has changed and then a calculation
            //got the same result as the origin value, remove the entry from the list
            //because there is no change. Otherwise put the value to the list (may
            //overwrite an existing entry)
            if(org!=null && org.equals(value)){
                updates().remove(name);
            }else{
                updates().put(name, new Object[]{org, value});
            }
        }
        
        //change the data field
        m_map.put(name, value);
	}
	
	// For query engine only
	
	/* (non-Javadoc)
	 * @see org.i3xx.util.rna.core.IBrickData#getValueS(java.lang.String)
	 */
	public String getValueS(String name) {
		Object result = getValue(name);
		return (result==null)?"":(result instanceof IProxy)?((IProxy)result).getString():result.toString();
	}
	public double getValueD(String name) {
		Object result = getValue(name);
		return (result==null)?0.0:(result instanceof IProxy)?((IProxy)result).getDouble():((Double)result).doubleValue();
	}
	public int getValueI(String name) {
		Object result = getValue(name);
		return (result==null)?0:(result instanceof IProxy)?((IProxy)result).getInt():((Integer)result).intValue();
	}
	public long getValueL(String name) {
		Object result = getValue(name);
		return (result==null)?0:(result instanceof IProxy)?((IProxy)result).getLong():((Long)result).longValue();
	}
	
    /* (non-Javadoc)
     * @see com.i3xx.ob.core.IBrick#delete()
     */
    public void delete(){
        if(readonly() || rejectRight(IRole.RIGHT_DELETE, this, IBrick.METHOD_INTERN))
            return;
        
        super.delete();
        m_map.clear();
    }
    
	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.ITimeSlice#clear()
	 */
	public void clear() {
        if(readonly() || rejectRight(IRole.RIGHT_WRITE, this, IBrick.METHOD_INTERN))
            return;
        
		super.clear();
		m_map.clear();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object clone() throws CloneNotSupportedException {
		// Creates a copy of this Object
		CData clone = (CData)super.clone();
		clone.m_map = (Map<String, Object>)((HashMap)this.m_map).clone();
		
		return clone;
	}
}
