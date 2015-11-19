package org.i3xx.util.hateoas;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * The RspState is used to give us some informations
 * about the response we can expect if a query to
 * the server doesn't point to a single resource.
 * 
 * @author Stefan
 *
 */
public class RspState {
	
	public static final String MSG_NOT_FOUND = RspState.of(HttpState.NOT_FOUND).toString();
	public static final String MSG_ERR = RspState.of(HttpState.FORBIDDEN).toString();
	public static final String MSG_OK = RspState.of(HttpState.OK).toString();
	
	/** The HTTP response code is 1xx */
	public static final int TYPE_INFO = 1;
	
	/** The HTTP response code is 2xx */
	public static final int TYPE_OK = 2;
	
	/** The HTTP response code is 3xx */
	public static final int TYPE_REDIRECTION = 3;
	
	/** The HTTP response code is 4xx */
	public static final int TYPE_CLIENT_ERROR = 4;
	
	/** The HTTP response code is 5xx */
	public static final int TYPE_SERVER_ERROR = 5;
	
	private StringBuffer buffer;
	
	private int state;
	private int size;
	private long length;
	private String mime;
	
	public RspState() {
		buffer = new StringBuffer();
		
		state = 0;
		size = 0;
		length = 0;
		mime = null;
	}
	
	/**
	 * Sets the state field of the response.
	 * 
	 * The state gives us some information about
	 * the expected results.
	 * 
	 * @param state
	 */
	public void setState(int state) {
		
		this.state = state;
		
		buffer.append("state");
		buffer.append(':');
		buffer.append(state);
		buffer.append(',');
	}

	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}
	
	/**
	 * @return
	 */
	public int getType() {
		return (state / 100);
	}
	
	/**
	 * Sets the size field of the response.
	 * 
	 * The size is something countable like
	 * the number of records or documents.
	 * 
	 * @param state
	 */
	public void setSize(int size) {
		
		this.size = size;
		
		buffer.append("size");
		buffer.append(':');
		buffer.append(size);
		buffer.append(',');
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Sets the content length field of the response.
	 * 
	 * The length is an amount like the number of bytes
	 * of a document.
	 * 
	 * @param length
	 */
	public void setContentLength(long length) {
		
		this.length = length;
		
		buffer.append("content-length");
		buffer.append(':');
		buffer.append(length);
		buffer.append(',');
	}

	/**
	 * Gets the content length field of the response.
	 * 
	 * @return the content length
	 */
	public long getContentLength() {
		return length;
	}
	
	/**
	 * Sets the content type field of the response.
	 * 
	 * The content type is the mimetype of the response.
	 * 
	 * @param state
	 */
	public void setContentType(String mime) {
		
		this.mime = mime;
		
		buffer.append("content-type");
		buffer.append(':');
		buffer.append(mime);
		buffer.append(',');
	}

	/**
	 * Gets the content type field of the response.
	 * 
	 * @return the content type
	 */
	public String getContentType() {
		return mime;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		//
		if(buffer.charAt(0)!='{')
			buffer.insert(0, '{');
		
		//
		if(buffer.charAt(buffer.length()-1)==',')
			buffer.setCharAt(buffer.length()-1, '}');
		if(buffer.charAt(buffer.length()-1)!='}')
			buffer.append('}');
		
		return buffer.toString();
	}
	
	/**
	 * Parses a JSON String to a RspState Object
	 * 
	 * @param stmt The JSON String to parse
	 * @return The RspState Object
	 */
	public static final RspState parse(String stmt) {
		RspState state = new RspState();
		
		JsonParser parser = new JsonParser();
		JsonElement elem = parser.parse(stmt);
		JsonObject jobj = elem.getAsJsonObject();
		
		JsonElement fState = jobj.get("state");
		if(fState!=null)
			state.setState(fState.getAsInt());
		
		JsonElement fSize = jobj.get("size");
		if(fSize!=null)
			state.setSize(fSize.getAsInt());
		
		JsonElement fContLen = jobj.get("content-length");
		if(fContLen!=null)
			state.setContentLength(fContLen.getAsLong());
		
		JsonElement fContTyp = jobj.get("content-type");
		if(fContTyp!=null)
			state.setContentType(fContTyp.getAsString());
		
		return state;
	}
	
	/**
	 * Creates a response String using the state.
	 * 
	 * @param state The state
	 * @return
	 */
	public static final RspState of(int state) {
		RspState r = new RspState();
		r.setState(state);
		return r;
	}
}
