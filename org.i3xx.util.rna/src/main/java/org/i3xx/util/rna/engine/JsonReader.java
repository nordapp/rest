package org.i3xx.util.rna.engine;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.i3xx.util.rna.engine.json.JP;


public class JsonReader implements IDataReader {
	
	private Map<String, Object> source;
	private Reader in;
	
	@SuppressWarnings({ "unchecked" })
	private JsonReader(Object o) {
		source = (Map<String, Object>)JP.toObject(o, BrickRNA.FIELD_DATA);
		in = null;
	}
	 
	@SuppressWarnings("unchecked")
	public JsonReader(String stmt) {
		Object o = JP.parse(stmt);
		source = (Map<String, Object>)JP.toObject(o, BrickRNA.FIELD_DATA);
	}
	
	@SuppressWarnings("unchecked")
	public JsonReader(Reader in) throws IOException {
		this.in = in;
		
		int c=0;
		char[] buf = new char[1024];
		StringWriter out = new StringWriter();
		
		while((c=in.read(buf))>-1)
			out.write(buf,0,c);
		
		String stmt = out.toString();
		Object o = JP.parse(stmt);
		source = (Map<String, Object>)JP.toObject(o, BrickRNA.FIELD_DATA);
	}
	
	public int available() throws IOException {
		return source.size();
	}
	
	public void close() throws IOException {
		source = null;
		if(in!=null)
			in.close();
	}

	public byte[] readByteArray(String field) throws IOException,
			ClassNotFoundException {
		
		return Base64.decodeBase64( JP.toString(source.remove(field)) );
	}

	public IDataReader readDataReader(String field) throws IOException,
			ClassNotFoundException {
		
		return new JsonReader(source.remove(field));
	}

	public double readDouble(String field) throws IOException {
		
		return JP.toDoubleFloat(source.remove(field));
	}

	public int readInt(String field) throws IOException {
		
		return JP.toInteger(source.remove(field)).intValue();
	}

	public long readLong(String field) throws IOException {
		
		return JP.toLongInt(source.remove(field));
	}
	
	public boolean readBoolean(String field) throws IOException {
		return JP.toBoolean(source.remove(field));
	}

	public Object readObject(String field) throws IOException,
			ClassNotFoundException {
		
		return source.remove(field);
	}

	public String readString(String field) throws IOException {
		
		return JP.toString(source.remove(field));
	}

	@SuppressWarnings("unchecked")
	public String[] readStringArray(String field) throws IOException,
			ClassNotFoundException {
		
		//
		// This is an undocumented function for the export
		//
		if(field.equals("%%keys%%")) {
			return source.keySet().toArray( new String[source.size()] );
		}
		
		List<Object> list = JP.toArray(source.remove(field));
		String[] arr = new String[list.size()];
		
		for(int i=0;i<list.size();i++){
			arr[i] = JP.toString(list.get(i));
		}
		
		return arr;
	}

	@SuppressWarnings("unchecked")
	public long[] readLongArray(String field) throws IOException,
			ClassNotFoundException {
		
		List<Object> list = JP.toArray(source.remove(field));
		long[] arr = new long[list.size()];
		
		for(int i=0;i<list.size();i++){
			arr[i] = JP.toLongInt(list.get(i)).longValue();
		}
		
		return arr;
	}

	@SuppressWarnings("unchecked")
	public int[] readIntArray(String field) throws IOException,
			ClassNotFoundException {
		
		List<Object> list = JP.toArray(source.remove(field));
		int[] arr = new int[list.size()];
		
		for(int i=0;i<list.size();i++){
			arr[i] = JP.toInteger(list.get(i)).intValue();
		}
		
		return arr;
	}

	@SuppressWarnings("unchecked")
	public double[] readDoubleArray(String field) throws IOException,
			ClassNotFoundException {
		
		List<Object> list = JP.toArray(source.remove(field));
		double[] arr = new double[list.size()];
		
		for(int i=0;i<list.size();i++){
			arr[i] = JP.toDoubleFloat(list.get(i)).doubleValue();
		}
		
		return arr;
	}
	
	/**
	 * Use the same encoding as the readByteArray method.
	 * 
	 * @param stmt The base64 encoded array
	 * @return The byte array
	 */
	public static byte[] encodeBytesFromString(String stmt) {
		return Base64.decodeBase64( stmt );
	}
	
	/**
	 * Use a json array to get a byte array.
	 * 
	 * @param list The json array
	 * @return The byte array
	 */
	@SuppressWarnings("rawtypes")
	public static byte[] encodeBytesFromArray(List list) {
		byte[] arr = new byte[list.size()];
		
		for(int i=0;i<list.size();i++){
			arr[i] = JP.toLongInt(list.get(i)).byteValue();
		}
		
		return arr;
	}
}
