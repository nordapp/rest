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
	private final int[] port;
	
	/**  */
	private final String[] host;
	
	/**  */
	private final InetAddress[][] addr;
	
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
			port = new int[answers.length];
			host = new String[answers.length];
			addr = new InetAddress[answers.length][];
			
			for(int i=0;i<answers.length;i++) {
				Record r = answers[0];
				SRVRecord srv = (SRVRecord)r;
				
				port[i] = srv.getPort();
				String h = srv.getTarget().toString();
				
				while(h.endsWith("."))
					h = h.substring(0, h.length()-1);
				
				host[i] = h;
				addr[i] = InetAddress.getAllByName(h);
			}//for
		}else{
			throw new NoSuchElementException("The service 'cloudreg._tcp."+domain+"' is not available in the DNS.");
		}
		
	}
	
	/**
	 * @return The number of records
	 */
	public int length() {
		return host.length;
	}
	
	/**
	 * @param i the index of the record (0 to length-1)
	 * @return the port
	 */
	public int getPort(int i) {
		return port[i];
	}

	/**
	 * @param i the index of the record (0 to length-1)
	 * @return the host
	 */
	public String getHost(int i) {
		return host[i];
	}
	
	/**
	 * @param i the index of the record (0 to length-1)
	 * @return the addresses
	 */
	public InetAddress[] getAddr(int i) {
		return addr[i];
	}
}
