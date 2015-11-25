package org.i3xx.util.rna.engine.json;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

public class GsonHashMapDeserializer implements JsonDeserializer<Object> {
	  public Object deserialize(JsonElement json, Type typeOfT, 
	      JsonDeserializationContext context) throws JsonParseException {
    	 //System.out.println("GND deserialize: "+json.toString());
	    if(json.isJsonNull()) return null;
	    else if(json.isJsonPrimitive()) return handlePrimitive(json.getAsJsonPrimitive());
	    else if(json.isJsonArray()) return handleArray(json.getAsJsonArray(), context);
	    else return handleObject(json.getAsJsonObject(), context);
	  }
	  private Object handlePrimitive(JsonPrimitive json) {
	    if(json.isBoolean())
	      return json.getAsBoolean();
	    else if(json.isString())
	      return json.getAsString();
	    else {
	    	  //System.out.println("GND handlePrimitive: "+json.getAsString());
	      BigDecimal bigDec = json.getAsBigDecimal();
	      if(json.getAsString().indexOf('.')==-1){
		      // Find out if it is an int type
		      try {
		    	  //System.out.println("GND: "+bigDec.toPlainString());
		        /*bigDec.toBigIntegerExact();
		        try { 
		        	return bigDec.intValueExact(); }
		        catch(ArithmeticException e) {}
		        */
		    	return bigDec.longValue();
		      } catch(ArithmeticException e) {}	    	  
	      }
	      // Just return it as a double
	      return bigDec.doubleValue();
	    }
	  }
	  @SuppressWarnings({ "rawtypes", "unchecked" })
	private Object handleArray(JsonArray json, JsonDeserializationContext context) {
	    //Object[] array = new Object[json.size()];
	    ArrayList array = new ArrayList();
	    for(int i = 0; i < json.size(); i++)
	      array.add( deserialize(json.get(i), Object.class,context) );
	    return array;
	  }
	  private Object handleObject(JsonObject json, JsonDeserializationContext context) {
	    //System.out.println("handleObject");
		  Map<String, Object> map = new HashMap<String, Object>();
	    for(Map.Entry<String, JsonElement> entry : json.entrySet()){
	      map.put(entry.getKey(), deserialize(entry.getValue(), Object.class,context));
	      //System.out.println("key:"+entry.getKey());
	    }
	    return map;
	  }
	}