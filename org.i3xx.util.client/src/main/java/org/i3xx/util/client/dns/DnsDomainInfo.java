package org.i3xx.util.client.dns;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

import org.xbill.DNS.DClass;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Message;
import org.xbill.DNS.NSRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.ReverseMap;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;
import org.xbill.DNS.ZoneTransferException;

public class DnsDomainInfo {
	
	/**  */
	private final String strDomain;
	
	/**  */
	private final String[] nameServer;
	
	
	/**
	 * Gets the domain and the nameserver
	 * 
	 * @throws IOException
	 */
	public DnsDomainInfo() throws IOException {
		
		InetAddress addr = InetAddress.getByName(InetAddress.getLocalHost().getHostName());
		String dm = addr.getCanonicalHostName();
		
		String[] namesrv = null;
		
		while(dm.contains(".")) {
			dm = dm.substring(dm.indexOf('.')+1);
			Record [] records = new Lookup(dm, Type.NS).run();
			
			if(records!=null) {
				namesrv = new String[records.length];
				for(int i=0;i<records.length;i++) {
					NSRecord r = (NSRecord)records[i];
					namesrv[i] = r.getTarget().toString(true);
				}
				break;
			}//fi
		}//
		Arrays.sort(namesrv);
		
		nameServer = namesrv;
		strDomain = dm;
	}
	
	/**
	 * Gets the domain, but not the nameserver
	 * From various code examples
	 * 
	 * @param hostIp
	 * @throws IOException
	 * @throws ZoneTransferException 
	 */
	public DnsDomainInfo(String hostIp) throws IOException, ZoneTransferException {

		Resolver res = new ExtendedResolver();
		Name name = ReverseMap.fromAddress(hostIp);
		
		int type = Type.PTR;
		int dclass = DClass.IN;
		
		Record rec = Record.newRecord(name, type, dclass);
		Message query = Message.newQuery(rec); 
		Message response = res.send(query);
		
		Record[] answers = response.getSectionArray(Section.ANSWER);
		if (answers.length == 0) {
			strDomain = null;
			nameServer = null;
		}else{
			String s = answers[0].rdataToString();
			int n = s.indexOf('.');
			String dm = n<0 ? s : s.substring(n+1);
			if(dm.endsWith("."))
				dm = dm.substring(0, dm.length()-1);
			
			strDomain = dm;
			 
			String[] namesrv = null;
			Record [] records = new Lookup(strDomain, Type.NS).run();
			if(records!=null) {
				namesrv = new String[records.length];
				for(int i=0;i<records.length;i++) {
					NSRecord r = (NSRecord)records[i];
					namesrv[i] = r.getTarget().toString(true);
				}
				Arrays.sort(namesrv);
			}//fi
			
			nameServer = namesrv;
		}//fi
		
	}
	
	/**
	 * @return the domain
	 */
	public String getDomain() {
		return strDomain;
	}
	
	/**
	 * @return
	 */
	public String[] getNameserver() {
		return nameServer;
	}
	
}
