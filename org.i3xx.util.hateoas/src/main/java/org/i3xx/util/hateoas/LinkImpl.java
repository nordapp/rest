package org.i3xx.util.hateoas;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class LinkImpl implements LinkHeader.Link {
	
	private static final String LINK_PARAM_SEPARATOR = ";";
	
	private String resource;
	private String linkClass;
	private String verb;
	private String relation;
	private String mimetype;
	private String charset;
	private Map<String, String> param;
	
	/**
	 * 
	 * @param link The data to parse
	 */
	public LinkImpl(String link) {
		this();
		parseData(link);
	}
	
	/**
	 * 
	 */
	public LinkImpl() {
		super();
		
		this.resource = null;
		this.linkClass = null;
		this.verb = null;
		this.relation = null;
		this.mimetype = null;
		this.charset = null;
		this.param = new LinkedHashMap<String, String>();
	}

	@Override
	public String getResource() {
		return resource;
	}

	@Override
	public void setResource(String resource) {
		this.resource = resource;
	}

	@Override
	public String getLinkClass() {
		return linkClass;
	}

	@Override
	public void setLinkClass(String linkClass) {
		this.linkClass = linkClass;
	}

	@Override
	public String getVerb() {
		return verb;
	}

	@Override
	public void setVerb(String verb) {
		this.verb = verb;
	}

	@Override
	public String getRelation() {
		return relation;
	}

	@Override
	public void setRelation(String relation) {
		this.relation = relation;
	}

	@Override
	public String getMimetype() {
		return mimetype;
	}

	@Override
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	@Override
	public String getCharset() {
		return charset;
	}

	@Override
	public void setCharset(String charset) {
		this.charset = charset;
	}

	@Override
	public String getParam(String key) {
		return param.get(key);
	}

	@Override
	public void setParam(String key, String value) {
		param.put(key, value);
	}
	
	/**
	 * Parameterize the resource, can replace '{}' only.
	 * 
	 * @param strings The parameters as values
	 */
	public void parameterize(String... strings) {
		StringBuffer buf = new StringBuffer();
		
		int i=0, l=0, n=0;
		for(;;) {
			i = resource.indexOf("{}", i);
			if(i<0 || n>=strings.length){
				buf.append(resource.substring(l));
				resource = buf.toString();
				break;
			}
			
			buf.append(resource.substring(l, i));
			buf.append(strings[n++]);
			l = i+2;
			i = l;
		}//for
	}
	
	/**
	 * Put the keys of the named parameters to the collection.
	 * 
	 * @return
	 */
	public void getParamKeys(Collection<String> col) {
		
		int i=0, l=0;
		for(;;) {
			i = resource.indexOf('{', i);
			if(i<0){
				break;
			}
			
			l = i+1;
			i = l;
			i = resource.indexOf('}', i);
			if(i<0) {
				break;
			}
			
			String key = resource.substring(l, i);
			col.add(key);
			l = i+1;
			i = l;
		}//for
	}
	
	/**
	 * Put the keys of the named parameters to the map. The value
	 * is null
	 * 
	 * @return
	 */
	public void getParamKeys(Map<String, String> map) {
		
		int i=0, l=0;
		for(;;) {
			i = resource.indexOf('{', i);
			if(i<0){
				break;
			}
			
			l = i+1;
			i = l;
			i = resource.indexOf('}', i);
			if(i<0) {
				break;
			}
			
			String key = resource.substring(l, i);
			map.put(key, null);
			l = i+1;
			i = l;
		}//for
	}
	
	/**
	 * Returns a new Map filled with the arguments passed as key/value tuples.
	 * 
	 * @param params The parameter map (or null to create one)
	 * @param args The key value tuples
	 * @return The parameter map
	 */
	public Map<String, String> getParameterMap(Map<String, String> params, String... args) {
		
		if(params==null)
			params = new LinkedHashMap<String, String>();
		
		for(int i=0;i<args.length;i+=2) {
			params.put(args[i], args[i+1]);
		}
		
		return params;
	}
	
	/**
	 * Parameterize the resource and replaces named parameter in the URL
	 * 
	 * @param params The parameter map
	 */
	public void parameterize(Map<String, String> params) {
		
		StringBuffer buf = new StringBuffer();
		
		int i=0, l=0;
		for(;;) {
			i = resource.indexOf('{', i);
			if(i<0){
				buf.append(resource.substring(l));
				resource = buf.toString();
				break;
			}
			buf.append(resource.substring(l, i));
			
			l = i+1;
			i = l;
			i = resource.indexOf('}', i);
			if(i<0) {
				resource = buf.toString();
				break;
			}
			
			String key = resource.substring(l, i);
			String val = params.get(key);
			buf.append(val==null?"{"+key+"}":val);
			l = i+1;
			i = l;
		}//for
	}
	
	/**
	 * Prints the content data to a link header value
	 * 
	 * @param buf The buffer to print to.
	 * @return
	 */
	public StringBuffer printData(StringBuffer buf) {
		
		buf.append("<");
		buf.append(getResource());
		buf.append(">;");
		buf.append(" rel=\"");
		buf.append(getRelation());
		buf.append("\"");
		
		if(getLinkClass()!=null) {
			buf.append("; class=\"");
			buf.append(getLinkClass());
			buf.append("\"");
		}
		if(getVerb()!=null) {
			buf.append("; verb=\"");
			buf.append(getVerb());
			buf.append("\"");
		}
		
		if(getMimetype()!=null) {
			buf.append("; type=\"");
			
			buf.append(getMimetype());
			if(getCharset()!=null) {
				buf.append(";");
				buf.append(getCharset());
			}
			
			buf.append("\"");
		}
		
		for(Map.Entry<String, String> e : param.entrySet()) {
			buf.append("; ");
			buf.append(e.getKey());
			buf.append("=\"");
			buf.append(e.getValue());
			buf.append("\"");
		}//for
		
		return buf;
	}
	
	/**
	 * Parses a single link from the link header
	 * 
	 * @param link
	 * @return The return code: 1=parse error, 0=ok
	 */
	public int parseData(String link) {
		String[] fields = link.split(LINK_PARAM_SEPARATOR);
		if(fields.length<2)
			return 1;
		
		char[] rc = fields[0].trim().toCharArray();
		if(rc[0]!='<' && rc[rc.length-1]!='>')
			return 1;
		
		this.setResource(new String(rc, 1, rc.length-2));
		
		for(int j=1;j<fields.length;j++) {
			String[] tuples = fields[j].trim().split("=");
			if(tuples.length<2)
				continue;
			
			final int p = tuples[1].length()-1;
			final boolean lit = tuples[1].charAt(0)=='"' && tuples[1].charAt(p)=='"';
			final boolean par = tuples[1].charAt(0)=='"' && tuples[1].charAt(p)!='"'; //"...;..."
			String key = tuples[0];
			String val = null;
			if(par) {
				//does nothing yet
			}else{
				val = lit ? tuples[1].substring(1, p) : tuples[1];
			}
			
			if(key.equalsIgnoreCase("class")){
				this.setLinkClass(val);
			}else
			if(key.equalsIgnoreCase("verb")){
				this.setVerb(val);
			}else
			if(key.equalsIgnoreCase("rel")){
				this.setRelation(val);
			}else
			if(key.equalsIgnoreCase("type")){
				if(par) {
					//the last char is no '"' and part of the value
					val = tuples[1].substring(1, p+1);
					this.setMimetype(val);
					if(fields.length>(j+1)){
						String tmp = fields[j+1].trim();
						if( tmp.indexOf('=')<0 && tmp.charAt(tmp.length()-1)=='"') {
							j++;
							val = tmp.substring(0, tmp.length()-1);
							this.setCharset(val);
						}//fi
					}//fi
				}else{
					val = lit ? tuples[1].substring(1, p) : tuples[1];
					this.setMimetype(val);
				}
			}else{
				//key
				//val
				if(par) {
					//the last char is no '"' and part of the value
					val = tuples[1].substring(1, p+1);
					if(fields.length>(j+1)){
						String tmp = fields[j+1].trim();
						if( tmp.indexOf('=')<0 && tmp.charAt(tmp.length()-1)=='"') {
							j++;
							val += ";"+tmp.substring(0, tmp.length()-1);
						}//fi
					}//fi
				}else{
					val = lit ? tuples[1].substring(1, p) : tuples[1];
				}//fi
				this.setParam(key, val);
			}//fi
		}//for
		
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return printData(new StringBuffer()).toString();
	}

}
