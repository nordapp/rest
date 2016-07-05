package org.i3xx.util.client.dns;

import java.net.InetAddress;

public class DomainInfo {
	
	/**  */
	private String hostnameCanonical;
	
	/**  */
	private String strHostName;
	
	/**  */
	private String strDomainName;
	
	/**  */
	private String strDomainSuffix;
	
	/**  */
	private boolean expectDNS;
	
	/**
	 * 
	 */
	public DomainInfo() {
		
		try{
			InetAddress addr = InetAddress.getByName(InetAddress.getLocalHost().getHostName());
			
		    hostnameCanonical = addr.getCanonicalHostName();
		    strHostName = addr.getHostName();
		    
		    if(hostnameCanonical.contains(".") && (strHostName.length() < hostnameCanonical.length()) ) {
		    	strDomainName = hostnameCanonical.substring( strHostName.length()+1, hostnameCanonical.lastIndexOf("."));
		    	strDomainSuffix = hostnameCanonical.substring( hostnameCanonical.lastIndexOf(".")+1 );
		    	expectDNS = true;
		    }else{
				strDomainName = strHostName;
				strDomainSuffix = null;
		    	expectDNS = false;
		    }
		} 
		catch (Exception e) {
			strDomainName = strHostName;
			strDomainSuffix = null;
	    	expectDNS = false;
		}
	}

	/**
	 * @return the hostnameCanonical
	 */
	public String getHostnameCanonical() {
		return hostnameCanonical;
	}
	
	/**
	 * @return the domain
	 */
	public String getDomain() {
		if(strDomainName!=null && strDomainSuffix!=null) {
			return strDomainName+"."+strDomainSuffix;
		}else if(strDomainName!=null) {
			return strDomainName;
		}else if(hostnameCanonical.length()>(strHostName.length())){
			return hostnameCanonical.substring(strHostName.length()+1);
		}else{
			return strHostName;
		}
	}

	/**
	 * @return the strHostName
	 */
	public String getStrHostName() {
		return strHostName;
	}

	/**
	 * @return the strDomainName
	 */
	public String getStrDomainName() {
		return strDomainName;
	}

	/**
	 * @return the strDomainSuffix
	 */
	public String getStrDomainSuffix() {
		return strDomainSuffix;
	}

	/**
	 * @return the expectDNS
	 */
	public boolean isExpectDNS() {
		return expectDNS;
	}
	
	
}
