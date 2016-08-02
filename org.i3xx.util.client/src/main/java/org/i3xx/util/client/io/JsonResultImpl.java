package org.i3xx.util.client.io;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonResultImpl extends ResultImpl implements JsonResult {
	
	private final JsonElement elem;
	
	protected JsonResultImpl(JsonElement elem) {
		this.elem = elem;
	}
	
	protected JsonResultImpl(JsonElement elem, int returnCode) {
		super(returnCode);
		this.elem = elem;
	}
	
	/**
	 * @return
	 */
	@Override
	public JsonElement toJson() {
		return elem;
	}

	@Override
	public Object getResult() {
		
		if(elem.isJsonPrimitive()) {
			JsonPrimitive p = (JsonPrimitive)elem;
			if(p.isBoolean()){
				return new Boolean(elem.getAsBoolean());
			}
			else if(p.isNumber()){
				String s = elem.getAsString();
				if(s.contains(".")) {
					BigDecimal d = elem.getAsBigDecimal();
					return d.compareTo(Resource.MAX_DOUBLE)<=0 ? new Double(d.doubleValue()) : d;
				}else{
					BigInteger i = elem.getAsBigInteger();
					return i.compareTo(Resource.MAX_LONG)<=0 ? new Long(i.longValue()) : i;
				}//fi
			}
			else if(p.isString()){
				return elem.getAsString();
			}
			else if(p.isJsonNull()){
				return null;
			}
		}//fi
		
		return elem;
	}
	
	/**  */
	public boolean isDecimal() {
		if(elem.isJsonPrimitive() && ((JsonPrimitive)elem).isNumber()) {
			String s = elem.getAsString();
			return (s.contains("."));
		}//fi
		return false;
	}
	
	/**  */
	public String getAsString() { return elem.getAsString(); }
	
	/**  */
	public boolean getAsBoolean() { return elem.getAsBoolean(); }
	
	/**  */
	public BigInteger getAsBigInteger() { return elem.getAsBigInteger(); }
	
	/**  */
	public long getAsLong() { return elem.getAsLong(); }
	
	/**  */
	public int getAsInt() { return elem.getAsInt(); }
	
	/**  */
	public BigDecimal getAsBigDecimal() { return elem.getAsBigDecimal(); }
	
	/**  */
	public double getAsDouble() { return elem.getAsDouble(); }
	
	/**  */
	public float getAsFloat() { return elem.getAsFloat(); }
	
	/**
	 * Returns true if the element exists.
	 * 
	 * @param path The path to the element
	 * @return
	 */
	public boolean match(String path) {
		
		String[] name = splitPath(path); 
		JsonElement jcur = elem;
		
		for(int i=0;i<name.length;i++){
			if(jcur.isJsonObject()) {
				JsonObject jobj = jcur.getAsJsonObject();
				if(jobj.has( name[i] )) {
					jcur = jobj.get(name[i]);
				}else{
					//no match
					jcur = null;
					break;
				}//fi
			}else{
				//no match
				jcur = null;
				break;
			}//fi
		}//for
		
		return jcur!=null;
	}
	
	/**
	 * Returns the element as JsonResult
	 * 
	 * @param path The path to the element
	 * @return
	 */
	public JsonResult get(String path) {
		
		String[] name = splitPath(path); 
		JsonResult resl = null;
		JsonElement jcur = elem;
		
		for(int i=0;i<name.length;i++){
			if(jcur.isJsonObject()) {
				JsonObject jobj = jcur.getAsJsonObject();
				if(jobj.has( name[i] )) {
					jcur = jobj.get(name[i]);
				}else{
					//no match
					jcur = null;
					break;
				}//fi
			}else{
				//no match
				jcur = null;
				break;
			}//fi
		}//for
		
		resl = new JsonResultImpl(jcur);
		return resl;
	}
	
	/**
	 * Returns the paths to every occurrence of an element with the name.
	 * 
	 * @param name
	 * @return
	 */
	public String[] find(String name) {
		
		Map<String, JsonElement> list = new HashMap<String, JsonElement>();
		rfind(elem, "", list, name);
		
		return list.keySet().toArray(new String[list.size()]);
	}
	
	/**
	 * @param current
	 * @param list
	 * @param name
	 */
	private void rfind(JsonElement current, String path, Map<String, JsonElement> list, String name) {
		
		//Primitive or Null
		if(current.isJsonPrimitive() || current.isJsonNull())
			return;
		
		//Array
		if(current.isJsonArray()) {
			JsonArray array = current.getAsJsonArray();
			for(int i=0;i<array.size();i++) {
				rfind(array.get(i), path, list, name);
			}//for
		}//fi
		
		//Object
		if(current.isJsonObject()) {
			JsonObject object = current.getAsJsonObject();
			for(Map.Entry<String, JsonElement> e : object.entrySet()){
				if(e.getKey().equals(name)) {
					list.put(path+"/"+e.getKey(), e.getValue());
				}//fi
				if(e.getValue().isJsonArray() || e.getValue().isJsonObject()) {
					rfind(e.getValue(), path+"/"+e.getKey(), list, name);
				}//fi
			}//for
		}//fi
	}
	
	/**
	 * Splits a path at '/'
	 * 
	 * @param path
	 * @return
	 */
	private String[] splitPath(String path) {
		//Trims the path and remove unnecessary '/'
		path = path.trim();
		if(path.startsWith("/"))
			path = path.substring(1);
		if(path.endsWith("/"))
			path = path.substring(0, path.length()-1);
		
		return path.split("/");
	}

}
