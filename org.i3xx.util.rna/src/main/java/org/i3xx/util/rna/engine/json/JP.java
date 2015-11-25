package org.i3xx.util.rna.engine.json;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This is a copy of the parser JsonTools available to all OfficeBase projects
 * 
 * @author Stefan
 *
 */
public class JP {

	/**
	 * The JSON reader returns the following objects:
	 * ArrayList ::= [], HashMap ::= {}, String ::= "", Long/BigInteger ::= 12345,
	 * Double/BigDecimal ::= 1234.5, Boolean ::= true|false, null ::= null
	 * 
	 * @return
	 */
	public static Object parse(String stmt) {
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(HashMap.class, new GsonHashMapDeserializer());
		Gson gson = gsonBuilder.create();
		
		return gson.fromJson(stmt, HashMap.class);
	}
	
	/**
	 * @param json
	 * @return
	 */
	public static String print(Object object) {
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(HashMap.class,new GsonHashMapDeserializer());
		Gson gson = gsonBuilder.create();
		
		return gson.toJson(object);
	}
	
	/**
	 * Reads the statement from a file
	 * 
	 * @param url The url to the file
	 * @throws IOException
	 */
	public static String readStmt(String url) throws IOException {
		
		URL u = new URL(url);
		
		Reader in = new InputStreamReader( u.openStream() );
		StringWriter out = new StringWriter();
		char[] cbuf = new char[1024];
		int c=0;
		try{
			while((c=in.read(cbuf))>-1)
				out.write(cbuf, 0, c);
		}catch(IOException e){
			throw e;
		}finally{
			in.close();
			out.close();
		}
		return out.toString();
	}
	
	/**
	 * Indicates wether the param is a json array.
	 * 
	 * @param param The param result object
	 * @return true if the param is a json array, false otherwise
	 */
	public static boolean isArray(Object param) {
		return (param instanceof ArrayList);
	}
	
	/**
	 * Casts the param to an ArrayList
	 * 
	 * @param param The param result object
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ArrayList toArray(Object param) {
		ArrayList result = null;
		if(isArray(param)){
			result = (ArrayList)param;
		}else{
			result = new ArrayList();
			result.add(param);
		}
		return result;
	}
	
	/**
	 * Indicates wether the param is a json object.
	 * 
	 * @param param The param result object
	 * @return true if the param is a json object, false otherwise
	 */
	public static boolean isObject(Object param) {
		return (param instanceof HashMap);
	}
	
	/**
	 * Casts the param to a HashMap
	 * 
	 * @param param The param result object
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static HashMap toObject(Object param, String defaultName) {
		HashMap result = null;
		if(isObject(param)){
			result = (HashMap)param;
		}else{
			result = new HashMap();
			result.put(defaultName, param);
		}
		return result;
	}
	
	/**
	 * Indicates wether the param is a json value (no object or array).
	 * 
	 * @param param The param result object
	 * @return true if the param is a json value, false otherwise
	 */
	public static boolean isValue(Object param) {
		return !(isArray(param) || isObject(param));
	}
	
	/**
	 * Casts the param to a value (if the value is
	 * an array or a map, recursion is used to resolve
	 * to a value)
	 * 
	 * @param param The param result object
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Object toValue(Object param) {
		if(isArray(param)){
			ArrayList a = (ArrayList)param;
			if(a.size()==0)
				return null;
			
			return toValue( a.get(0) );
		}else if(isObject(param)){
			HashMap m = (HashMap)param;
			Iterator iter = m.keySet().iterator();
			if(iter.hasNext()){
				return toValue( m.get( iter.next() ) );
			}else
				return null;
		}else
			return param;
	}
	
	/**
	 * Indicates wether the param is a json string.
	 * 
	 * @param param The param result object
	 * @return true if the param is a json string, false otherwise
	 */
	public static boolean isString(Object param) {
		return (param instanceof String);
	}
	
	/**
	 * Casts the param to a String
	 * 
	 * @param param The param result object
	 * @return
	 */
	public static String toString(Object param) {
		String result = null;
		if(isNull(param)){
			result = "";
		}else if(isString(param)){
			result = (String)param;
		}else{
			result = param.toString();
		}
		return result;
	}
	
	/**
	 * Indicates wether the param is a json int.
	 * 
	 * @param param The param result object
	 * @return true if the param is a json int, false otherwise
	 */
	public static boolean isInteger(Object param) {
		return (param instanceof Long || param instanceof BigInteger);
	}
	
	/**
	 * Casts the param to a BigInteger
	 * 
	 * @param param The param result object
	 * @return
	 */
	public static BigInteger toInteger(Object param) {
		BigInteger result = null;
		if(param instanceof BigInteger){
			result = (BigInteger)param;
		}else if(param instanceof Long){
			long n = ((Long)param).longValue();
			result = BigInteger.valueOf( n );
		}else if(param instanceof Double){
			long n = ((Double)param).longValue();
			result = BigInteger.valueOf( n );
		}else if(param instanceof BigDecimal){
			/*
			long n = ((BigDecimal)param).longValue();
			result = BigInteger.valueOf( n );
			*/
			result = ((BigDecimal)param).toBigInteger();
		}else if(param instanceof String){
			result = new BigInteger( (String)param );
		}else{
			result = BigInteger.valueOf( 0 );
		}
		return result;
	}
	
	/**
	 * Casts the param to a BigInteger
	 * 
	 * @param param The param result object
	 * @return
	 */
	public static Long toLongInt(Object param) {
		Long result = null;
		if(param instanceof Long){
			result = (Long)param;
		}else if(param instanceof BigInteger){
			long n = ((BigInteger)param).longValue();
			result = new Long( n );
		}else if(param instanceof Double){
			long n = ((Double)param).longValue();
			result = new Long( n );
		}else if(param instanceof BigDecimal){
			long n = ((BigDecimal)param).longValue();
			result = new Long( n );
		}else if(param instanceof String){
			result = new Long( (String)param );
		}else{
			result = new Long( 0 );
		}
		return result;
	}
	
	/**
	 * Indicates wether the param is a json float.
	 * 
	 * @param param The param result object
	 * @return true if the param is a json float, false otherwise
	 */
	public static boolean isFloat(Object param) {
		return (param instanceof Double || param instanceof BigDecimal);
	}
	
	/**
	 * Casts the param to a BigDecimal
	 * 
	 * @param param The param result object
	 * @return
	 */
	public static BigDecimal toFloat(Object param) {
		BigDecimal result = null;
		if(param instanceof BigDecimal){
			result = (BigDecimal)param;
		}else if(param instanceof Long){
			long n = ((Long)param).longValue();
			result = BigDecimal.valueOf( n );
		}else if(param instanceof Double){
			double n = ((Double)param).doubleValue();
			result = BigDecimal.valueOf( n );
		}else if(param instanceof BigInteger){
			result = new BigDecimal( (BigInteger)param );
		}else if(param instanceof String){
			result = new BigDecimal( (String)param );
		}else{
			result = BigDecimal.valueOf( 0 );
		}
		return result;
	}
	
	/**
	 * Casts the param to a BigInteger
	 * 
	 * @param param The param result object
	 * @return
	 */
	public static Double toDoubleFloat(Object param) {
		Double result = null;
		if(param instanceof Double){
			result = (Double)param;
		}else if(param instanceof BigDecimal){
			double n = ((BigDecimal)param).doubleValue();
			result = new Double( n );
		}else if(param instanceof Long){
			double n = ((Long)param).doubleValue();
			result = new Double( n );
		}else if(param instanceof BigInteger){
			double n = ((BigInteger)param).doubleValue();
			result = new Double( n );
		}else if(param instanceof String){
			result = new Double( (String)param );
		}else{
			result = new Double( 0 );
		}
		return result;
	}
	
	/**
	 * Indicates wether the param is a json boolean.
	 * 
	 * @param param The param result object
	 * @return true if the param is a json boolean, false otherwise
	 */
	public static boolean isBoolean(Object param) {
		return (param instanceof Boolean);
	}
	
	/**
	 * Casts the param to a Boolean
	 * 
	 * @param param The param result object
	 * @return
	 */
	public static Boolean toBoolean(Object param) {
		if(param instanceof Boolean){
			return (Boolean) param;
		}else if(param instanceof String){
			return new Boolean( Boolean.parseBoolean((String)param) );
		}
		return false;
	}
	
	/**
	 * Indicates wether the param is a json null.
	 * 
	 * @param param The param result object
	 * @return true if the param is a json null, false otherwise
	 */
	public static boolean isNull(Object param) {
		return param==null;
	}
}
