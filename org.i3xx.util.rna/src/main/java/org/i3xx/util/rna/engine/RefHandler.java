package org.i3xx.util.rna.engine;
import java.util.Map;

import org.i3xx.util.rna.core.IBrick;
import org.i3xx.util.rna.core.IBrickData;
import org.i3xx.util.rna.core.IProxy;

public class RefHandler {
	
	/**
	 * 
	 */
	public RefHandler(){
	}

	/**
	 * Updates the reference
	 * 
	 * @param proxy current proxy e.g proxy to gesellschaft
	 * @param brick the brick e.g vertrag
	 * @param newValue the new value to set a new proxy
	 * @param cmd the installer
	 * @return
	 * 
	 * sh, 20.11.2012 Needs to test the value before return without action. Otherwise
	 * the reorganization by the rna package don't work properly.
	 */
	public IProxy updateReference(IProxy proxy, IBrick brick, String newValue, CCommandInstallProxy cmd) {
	    
        //Keine Wert�nderung, return
        if(proxy!=null && proxy.isEqual(newValue)) {
        	if(brick instanceof IBrickData){
        		@SuppressWarnings("unchecked")
				Map<String, Object> map = ((IBrickData)brick).getMap();
        		Object tmpValue = map.get( proxy.getField() );
        		
        		if( (tmpValue instanceof Float) || (tmpValue instanceof Double) ){
        			//Assume decimal number
        			try{
	        			if(tmpValue!=null && newValue!=null){
	        				if(Double.parseDouble(newValue) == ((Number)tmpValue).doubleValue())
	        					return proxy;
	        			}
        			}catch(NumberFormatException e){}
        		}else if( tmpValue instanceof Number ){
        			//Assume integer
        			try{
	        			if(tmpValue!=null && newValue!=null){
	        				if(Long.parseLong(newValue) == ((Number)tmpValue).longValue())
	        					return proxy;
	        			}
        			}catch(NumberFormatException e){}
        		}else if( tmpValue instanceof String ){
        			//Assume string
	        		String oldValue = (String)tmpValue;
	        		if(oldValue!=null && oldValue.equals(newValue))
	        			return proxy;
        		}else{
        			//Assume object
	        		Object oldValue = tmpValue;
	        		if(oldValue!=null && oldValue.toString().equals(newValue))
	        			return proxy;
        		}
        	}
        }
        
        //Referenz auf neuen Proxy beschaffen
        cmd.execute(brick); //finds a proxy or creates a new one
        IProxy newproxy = cmd.getProxy();
        if(newproxy == null)       // keine Referenz gefunden, return
            return null;
        
        //FIXME
        //if proxy == newproxy:
        // => newproxy is not new
        // => don't do anything
        if(equalsA(proxy, newproxy))
        	return proxy;
        
		// Gegenst�ck ist vom Typ int.
		if(proxy!=null)	{
			//referenziertes Objekt muss Verweise auf das Proxy l�sen
			proxy.removeProxy();	// Referenzz�hler verringern
			brick.removeReference((IBrick)proxy); // Referenz aus Auflistung entfernen
			proxy = null;			// Variable zur�cksetzen
		}
        
		newproxy.addProxy();		 // Referenzz�hler erh�hen
		brick.add((IBrick)newproxy); // Referenz der Auflistung hinzuf�gen
		
		// ### Fixed in CBrick.add(IBrick) ###
		//proxy.parent(null)	// brick.add(proxy) setzt proxy.parent(brick)
								// alternativ mit instanceof verhindern

		return newproxy;
	}
	
    // condition0: proxy == newproxy
    // condition1: proxy.getReference().ID() == newproxy.getReference().ID() != 0
    // condition2: proxy.lastReference() == newproxy.lastReference() != 0
    // condition3: proxy.getField() == newproxy.getField() != (null || "")
	private boolean equalsA(IProxy a, IProxy b) {
		if(a==null || b==null)
			return false; //not comparable
		
		//object equality
        if(a.equals(b))
        	return true; //equal
        
        IBrick aR = a.getReference();
        IBrick bR = b.getReference();
        String aF = a.getField();
        String bF = b.getField();
        
        if(aR==null || bR==null || aF==null || bF==null)
        	return false; //not comparable
        
        if(aF.equals("") || bF.equals(""))
        	return false; //not comparable
        
        long aI = aR.ID();
        long bI = bR.ID();
        
        //the same reference target from the same field
        if(aI==bI && aF.equals(bF))
        	return true; //equal
        
        //the same reference target from another field is not the same proxy
        //excemple:
        //FIELD_VR1 => Gesellschaft A (Proxy FIELD_VR1)
        //FIELD_VR2 => Gesellschaft A (Proxy FIELD_VR2)
        
		return false; //not equal
	}
}
