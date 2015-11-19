package org.i3xx.util.hateoas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LinkHeader {
	
	private static final String LINK_SEPARATOR = ",";
	
	/**
	 * Creates the value of the link header
	 * 
	 * @param list The list of valid resources
	 * @param server The path to server root
	 * @param session The session id
	 * @return The link header
	 */
	public static String getLinkHeader(List<Link> list) {
		
		StringBuffer buf = new StringBuffer();
		
		for(int i=0;i<list.size();i++) {
			Link link = list.get(i);
			if(i>0)
				buf.append(",");
			
			buf.append(link.toString());
		}
		
		return buf.toString();
	}
	
	/**
	 * Parses a link header to the fields.
	 * 
	 * @param header
	 */
	public static List<Link> parseLinkHeader(String header) {
		
		List<Link> list = new ArrayList<Link>();
		
		String[] headrs = header.split(LINK_SEPARATOR);
		for(int i=0;i<headrs.length;i++) {
			list.add(new LinkImpl(headrs[i]));
		}//for
		
		return list;
	}
	
	public static interface Link {
		
		/**
		 * Gets the resource
		 * 
		 * @return
		 */
		String getResource();
		
		/**
		 * Sets the resource
		 * 
		 * @param resource The resource to set
		 */
		void setResource(String resource);
		
		/**
		 * Gets the class of the link
		 * 
		 * @return
		 */
		String getLinkClass();
		
		/**
		 * Sets the class of the link
		 * 
		 * @param linkClass The class to set
		 */
		void setLinkClass(String linkClass);
		
		/**
		 * Gets the verb
		 * 
		 * @return
		 */
		String getVerb();
		
		/**
		 * Sets the verb
		 * 
		 * @param verb The verb to set
		 */
		void setVerb(String verb);
		
		/**
		 * Gets the relation
		 * 
		 * @return
		 */
		String getRelation();
		
		/**
		 * Sets the relation
		 * 
		 * @param relation The relation to set
		 */
		void setRelation(String relation);
		
		/**
		 * Gets the mime type
		 * 
		 * @return
		 */
		String getMimetype();
		
		/**
		 * Sets the mime type
		 * 
		 * @param mimetype The mime type to set
		 */
		void setMimetype(String mimetype);
		
		/**
		 * Gets the charset
		 * 
		 * @return
		 */
		String getCharset();
		
		/**
		 * Sets the charset
		 * 
		 * @param charset The charset to set
		 */
		void setCharset(String charset);
		
		/**
		 * Gets the value of a key value pair
		 * @param key The key of the parameter
		 * @return
		 */
		String getParam(String key);
		
		/**
		 * Sets a  key value pair
		 * 
		 * @param key The key of the parameter
		 * @param value The value of the parameter
		 */
		void setParam(String key, String value);
		
		/**
		 * Returns a collection containing the parameter keys.
		 * 
		 * @param col The collection as a container.
		 */
		void getParamKeys(Collection<String> col);
	}
}
