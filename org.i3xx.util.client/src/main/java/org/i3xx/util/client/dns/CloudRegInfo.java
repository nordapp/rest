package org.i3xx.util.client.dns;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;

import org.xbill.DNS.DClass;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class CloudRegInfo {
	
	/**  */
	private int port;
	
	/**  */
	private String host;
	
	/**  */
	private InetAddress addr;
	
	/**
	 * @param domain
	 * @throws TextParseException
	 * @throws UnknownHostException
	 * @throws NoSuchElementException
	 */
	public CloudRegInfo(String domain) throws TextParseException, UnknownHostException, NoSuchElementException {
		
		Lookup lookup = new Lookup("_cloudreg._tcp."+domain, Type.SRV, DClass.IN);
		lookup.setResolver(new SimpleResolver());
		
		lookup.run();
		Record[] answers = lookup.getAnswers();
		
		if(answers!=null && answers.length!=0) {
			Record r = answers[0];
			SRVRecord srv = (SRVRecord)r;
			
			port = srv.getPort();
			host = srv.getTarget().toString();
			
			while(host.endsWith("."))
				host = host.substring(0, host.length()-1);
			
			addr = InetAddress.getByName(host);
		}else{
			throw new NoSuchElementException("The service 'cloudreg._tcp."+domain+"' is not available in the DNS.");
		}
		
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * @return the address
	 */
	public InetAddress getAddr() {
		return addr;
	}
}
