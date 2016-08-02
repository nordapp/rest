package org.i3xx.util.client.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import org.i3xx.util.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * @author Stefan
 *
 */
public class Resource {
	
	/**  */
	private static final Logger logger = LoggerFactory.getLogger(Resource.class);
	
	/**  */
	protected static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
	
	/**  */
	protected static final BigDecimal MAX_DOUBLE = BigDecimal.valueOf(Double.MAX_VALUE);
	
	/**
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static final Resource create() throws IOException {
		return new Resource(null);
	}
	
	/**
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static final Resource create(URL url) throws IOException {
		return new Resource(url);
	}
	
	/**
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static final Resource create(String url) throws IOException {
		return create(new URL(url));
	}
	
	/**  */
	private final MutableInt retCode;
	/**  */
	private URL url;
	/**  */
	private InputStreamHandler input;
	/**  */
	private OutputStreamHandler output;
	/**  */
	private Result result;
	
	/**
	 * @param url
	 */
	private Resource(URL url) {
		this.url = url;
		this.input = null;
		this.output = null;
		this.result = null;
		this.retCode = new MutableInt(-1);
	}
	
	/**
	 * @return
	 */
	public Resource setStringResult() {
		this.input = new InputStreamHandler() {
			@Override
			public void stream(Resource resource, InputStream in) throws IOException {
				final String r = resource.readToString(in, Charset.defaultCharset().toString());
				resource.result = new ResultImpl(retCode) {
					@Override
					public Object getResult() {
						return r;
					}
				};
			}
		};
		return this;
	}
	
	/**
	 * @return
	 */
	public Resource setJsonResult() {
		this.input = new InputStreamHandler() {
			@Override
			public void stream(Resource resource, InputStream in) throws IOException {
				final JsonElement r = resource.readToJson(in);
				resource.result = new ResultImpl(retCode) {
					@Override
					public Object getResult() {
						return r;
					}
				};
			}
		};
		return this;
	}
	
	/**
	 * @param result
	 * @return
	 */
	public Resource externResult(final Object result) {
		this.result = new ResultImpl(retCode){

			@Override
			public Object getResult() {
				return result;
			}};
		return this;
	}
	
	/**
	 * @param handler
	 * @return
	 */
	public Resource streamTo(OutputStreamHandler handler) {
		output = handler;
		return this;
	}
	
	/**
	 * @param handler
	 * @return
	 */
	public Resource streamFrom(InputStreamHandler handler) {
		input = handler;
		return this;
	}
	
	/**
	 * @param tuples The connection parameters as key value pairs
	 * @return
	 * @throws IOException
	 */
	public Resource operate(String ... tuples) throws IOException {
		
		String method = output!=null ? "POST" : "GET";
		
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setDoOutput( output!=null );
		con.setDoInput( input!=null || (input==null && output==null) );
		con.setInstanceFollowRedirects(false);
		con.setRequestMethod(method);
		for(int i=0;i<tuples.length-1;i+=2) {
			logger.trace("Set property name:{}, value:{}", tuples[0], tuples[1]);
			con.setRequestProperty(tuples[i], tuples[i+1]);
		}
		logger.debug("Open connection {} to: {}", method, url);
		//con.setRequestProperty("Content-Type", "text/plain");
		//con.setRequestProperty("charset", "utf-8");
		con.connect();
		
		try{
			if(input!=null && con.getResponseCode()<400)
				input.stream( this, con.getInputStream() );
			if(output!=null && con.getResponseCode()<400)
				output.stream( this, con.getOutputStream() );
			
			//If not string result or json result is set.
			retCode.intValue( con.getResponseCode() );
			logger.trace("Return code: {}", retCode.intValue());
			if(result==null){
				//result = () -> new Integer(rc);
				result = new ResultImpl(retCode){
					@Override
					public Object getResult() { return new Integer(retCode.intValue()); }
				};
			}
		}catch(IOException e) {
			logger.debug("An exception when operating the connection.", e);
			throw e;
		}finally{
			con.disconnect();
		}
		
		return this;
	}
	
	/**
	 * Gets the result object
	 * 
	 * @return
	 */
	public Result result() {
		return result;
	}
	
	/**
	 * Gets the result of the result object.<br>
	 * <br>
	 * This is the same as <code>result().getResult()</code>
	 * @return
	 */
	public Object getResult() {
		return result==null ? null : result.getResult();
	}
	
	/**
	 * Gets the result of the result object.<br>
	 * <br>
	 * This is the same as <code>result().toJsonResult()</code>
	 * @return
	 */
	public JsonResult toJsonResult() {
		return result==null ? new JsonResultImpl(null, retCode) : result.toJsonResult();
	}
	
	/**
	 * @param in The InputStream to read
	 * @param charsetName The name of the charset (or null to use the default charset)
	 * @return
	 * @throws IOException 
	 */
	protected String readToString(InputStream in, String charsetName) throws IOException {
		
		int c = 0;
		byte[] buf = new byte[256];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try {
			while((c=in.read(buf))>-1) {
				out.write(buf, 0, c);
			}
		}finally{
			in.close();
			out.close();
		}
		
		if(charsetName==null)
			charsetName = Charset.defaultCharset().toString();
		
		String stmt = out.toString(charsetName);
		logger.debug("String-response:", stmt);
		
		return stmt;
	}
	
	/**
	 * @param in The InputStream to read
	 * @param charsetName The name of the charset (or null to use the default charset)
	 * @return
	 * @throws IOException 
	 */
	protected JsonElement readToJson(InputStream in) throws IOException {
		
		JsonParser parser = new JsonParser();
		JsonElement elem = parser.parse(new InputStreamReader(in));
		logger.debug("Json-response:", elem);
		
		return elem;
	}
	
	/**
	 * Macro: setJsonResult().operate().toJsonResult()
	 * @throws IOException 
	 */
	public JsonResult processJson() throws IOException {
		if(input!=null || output!=null)
			throw new IllegalStateException("The macro must be the only operation of the resource.");
		
		return setJsonResult().operate().toJsonResult();
	}
	
	/**
	 * Macro: setStringResult().operate().result()
	 * @throws IOException 
	 */
	public Result processString() throws IOException {
		if(input!=null || output!=null)
			throw new IllegalStateException("The macro must be the only operation of the resource.");
		
		return setStringResult().operate().result();
	}
}
