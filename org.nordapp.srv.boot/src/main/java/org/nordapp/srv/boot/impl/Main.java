package org.nordapp.srv.boot.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.PropertyConfigurator;
import org.i3xx.util.basic.io.FilePath;
import org.i3xx.util.hateoas.LinkImpl;
import org.i3xx.util.hateoas.RspState;
import org.nordapp.srv.boot.util.Println;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.i3xx.cloud.http.impl.Httpd;
import com.i3xx.cloud.http.impl.HttpdWrapper;
import com.i3xx.cloud.http.impl.HttpdWrapper.OB4Status;
import com.i3xx.cloud.http.impl.HttpdWrapper.Response;
import com.i3xx.cloud.http.state.Context;
import com.i3xx.cloud.http.state.ContextAction;
import com.i3xx.cloud.http.state.ContextFactory;
import com.i3xx.cloud.http.state.ContextMaker;
import com.i3xx.cloud.http.util.HeaderUtil;

public class Main {
	
	static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	static final String TEXT_MIME = "text/plain";
	static final String JSON_MIME = "application/json";
	static final String OCTET_MIME = "application/octet-stream";
	
	/**  */
	static Httpd httpd = null;
	
	/** False if the service has been stopped */
	static boolean _cont = true;
	
	public static void main(String[] args) throws Exception {
		
		/** The home directory */
		FilePath home = null;
		/** The data directory */
		FilePath datad = null;
		/**  */
		int port = 8085;
		/**  */
		String host = "localhost";
		
		for(int i=0;i<args.length;i++) {
			String arg = args[i];
			
			if(arg.equals("-h")) {
				home = FilePath.get( args[i+1] );
				i += 1;
			}
			else if(arg.equals("-p")) {
				port = Integer.parseInt( args[i+1] );
				i += 1;
			}
			else if(arg.equals("-d")) {
				datad = FilePath.get( args[i+1] );
				i += 1;
			}
			else if(arg.equals("-b")) {
				host = args[i+1];
				i += 1;
			}
			else if(arg.equals("-help")) {
				Println.console("usage:");
				Println.console("java -jar com.i3xx.cloud.config.jar");
				Println.console("     -h [Volume:]path");
				Println.console("     -d [Volume:]path");
				Println.console("     -b host");
				Println.console("     -p port");
				Println.console("");
				Println.console("-h [Volume:]path      Sets the local home directory");
				Println.console("-d [Volume:]path      Sets the local data directory");
				Println.console("-b host               The ip or name the server binds to");
				Println.console("-p port               The port the server is listening");
				
				return;
			}//fi
		}//for
		
		PropertyConfigurator.configure(home.add("Log4j.properties").toString());
		
		logger.info("The HTTPD starts at {}:{}.", host, port);
		logger.info("Logging is set to {} home:{}.", logger.isTraceEnabled()?"TRACE":
			logger.isDebugEnabled()?"DEBUG":logger.isInfoEnabled()?"INFO":
				logger.isWarnEnabled()?"WARN":"ERROR", home.toString());
		
		//start the local store
		//create the directory if not present
		datad.toFile().mkdirs();
		
		ContextFactory factory = getContext(datad, host, port);
		
		httpd = new Httpd(host, port, factory);
		httpd.start();
		
		addShutdownHook();
		logger.info("The HTTPD has been started.");
		
		factory.setAppState(ContextFactory.AppState.RUNNING);
		
		//keep thread running until close
		if(_cont) {
			while(_cont) {
				Thread.sleep(1000);
			}//for
		}//fi
		
	}/*method::main*/
	
	/**
	 * 
	 */
	private static void addShutdownHook() {
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
			public void run() {
				close();
			}//run
		}));
	}
	
	/**
	 * 
	 */
	public static void close() {
		//avoid 
		if(!_cont)
			return;
		
		logger.info("The HTTPD has reached the stop signal.");
		
		httpd.getFactory().setAppState(ContextFactory.AppState.UNDEFINED);
		
		//cleanup
		httpd.stop();
		
		//wait to cleanup
		logger.info("The HTTPD has been stopped.");
		
		_cont = false;
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
	}/*method::close*/
	
	/**
	 * @param trans
	 * @return
	 * @throws IOException
	 */
	private static ContextFactory getContext(final FilePath datad, final String host, final int port) throws IOException {
		
		Map<String,String> map = new HashMap<String, String>();
		map.put("protocol", "http");
		map.put("server", host+":"+String.valueOf(port));
		//map.put("session", "$10");
		//map.put("query", "{query}");
		final Map<String,String> params = Collections.unmodifiableMap(map); //Fix the content
		final Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.serializeNulls()
				.create();
		
		ContextFactory factory = new ContextFactory(){
			
			@Override
			public Context getContext() {
				
				Context c = new ContextMaker(this).setRelationContext().allowSpaces().make();
				
				for(String t : getActions()) {
					c.addAction(t, getAction(t));
				}
				
				//
				// The transition must be used to set the possible links
				//
				
				c.addLink(new LinkImpl("</boot/get/<product>/<type>/<version>>; rel=get; verb=get;"));
				c.addLink(new LinkImpl("</boot/info/>; rel=info; verb=get;"));
				c.addLink(new LinkImpl("</boot/shutdown/<timeout>>; rel=shutdown; verb=get;"));
				
				return c;
			}
			
		};
		
		//get, resource=serviceId (store name)
		factory.addAction("/boot/get/arduino/uno", new ContextAction(){
			
			@Override
			public Response call(String uri, Context context, Map<String, String> parameters) throws Exception {
				
				String[] paths = uri.substring(1).split("/");
				Response r = testURI(paths, 5, context, params, gson);
				if(r!=null)
					return r;
				
				String product = paths[2];
				String type = paths[3];
				String version = paths[4];
				
				if(version.equalsIgnoreCase("show")) {
					//show data
					JsonObject data = new JsonObject();
					data.addProperty("product", product);
					data.addProperty("type", type);
					data.addProperty("version", version);
					
					return HttpdWrapper.response(OB4Status.OK, HeaderUtil.create().put("link",
							context.getLinkHeader(params)).get(), JSON_MIME, gson.toJson(data));
				}
				else{
					File file = new File(datad.toFile(), version);
					
					if( ! file.exists()) {
						return HttpdWrapper.response(OB4Status.NOT_FOUND, null, "text/html", Httpd.notfound);
					}
					
					InputStream in = new FileInputStream(file);
					
					return HttpdWrapper.response(OB4Status.OK, HeaderUtil.create().put("link",
							context.getLinkHeader(params)).get(), OCTET_MIME, in);
				}
				//fi
			}
		});
		
		//reset, resource=serviceId (store name)
		factory.addAction("/boot/info", new ContextAction(){
			
			@Override
			public Response call(String uri, Context context, Map<String, String> parameters) throws Exception {
				
				StringBuffer buffer = new StringBuffer();
				buffer.append('{');
				buffer.append('"');
				buffer.append("app-state");
				buffer.append('"');
				buffer.append(':');
				
				if(context.getFactory().getAppState()==ContextFactory.AppState.RUNNING) {
					buffer.append(ContextFactory.APP_OK);
				}else{
					buffer.append(ContextFactory.APP_NOOK);
				}
				buffer.append('}');
				String json = buffer.toString();
				
				return HttpdWrapper.response(OB4Status.OK, HeaderUtil.create().put("link",
						context.getLinkHeader(params) ).get(), JSON_MIME, json);
			}
		});
		
		//reset, resource=serviceId (store name)
		factory.addAction("/boot/shutdown", (uri,context,parameters)->{
			
			close();
			
			return HttpdWrapper.response(OB4Status.OK, HeaderUtil.create().put("link",
					context.getLinkHeader(params) ).get(), TEXT_MIME, RspState.MSG_OK);
		});
		
		return factory;
	}
	
	/**
	 * Tests the path length
	 * 
	 * @param paths
	 * @param minLen
	 * @param context
	 * @param params
	 * @param gson
	 * @return
	 */
	private static Response testURI(String[] paths, int minLen, Context context, final Map<String,String> params, Gson gson) {
		if(paths.length<minLen) {
			JsonObject data = new JsonObject();
			data.addProperty("exception", "java.lang.ArrayIndexOutOfBoundsException");
			data.addProperty("description", "The path has not enough parts (missing data).");
			data.addProperty("PathArrayLength", paths.length);
			data.addProperty("MinimumPathLength", minLen);
			for(int i=0;i<paths.length;i++) {
				data.addProperty("pathElement_"+String.valueOf(i), paths[i]);
			}
			
			return HttpdWrapper.response(OB4Status.OK, HeaderUtil.create().put("link",
					context.getLinkHeader(params)).get(), JSON_MIME, gson.toJson(data));
		}else{
			return null;
		}
	}
	
}
