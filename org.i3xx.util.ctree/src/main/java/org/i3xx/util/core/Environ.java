/*
 * Created on 06.12.2004
 *
 */
package org.i3xx.util.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author S. Hauptmann
 *
 */
public class Environ {

	private static final Logger logger = LoggerFactory.getLogger(Environ.class);
	
	/**
	 * @author Written and compiled by Real Gagnon (C)1998-2002
	 *         http://www.rgagnon.com/javadetails/java-0150.html
	 */
	public static Properties getEnvVars() throws Exception {
	 	Process p = null;
	 	Properties envVars = new Properties();
	 	Runtime r = Runtime.getRuntime();
	 	String OS = System.getProperty("os.name").toLowerCase();
	 	logger.debug("The os.name is '{}'.", OS);
	 	if (OS.indexOf("windows 9") > -1) {
	 	  	p = r.exec( "command.com /c set" );
	 	}
	 	else if ( (OS.indexOf("nt") > -1)
	 			 || (OS.indexOf("windows 20") > -1 )
	 	         || (OS.indexOf("windows server 20") > -1 )
	 	         || (OS.indexOf("windows xp") > -1) ) {
	 		// thanks to JuanFran for the xp fix!
	 		// thanks to Van Ly for the 2003 fix!
	 		p = r.exec( "cmd.exe /c set" );
	 	}
	 	else {
	 	    // our last hope, we assume Unix (thanks to H. Ware for the fix)
	 	    p = r.exec( "env" );
	 	}
	 	BufferedReader br = new BufferedReader
	 		( new InputStreamReader( p.getInputStream() ) );
	 	String line;
	 	while( (line = br.readLine()) != null ) {
	 	   int idx = line.indexOf( '=' );
	 	   String key = line.substring( 0, idx );
	 	   String value = line.substring( idx+1 );
	 	   envVars.setProperty( key, value );
	 	}
	 	return envVars;
	 }
}
