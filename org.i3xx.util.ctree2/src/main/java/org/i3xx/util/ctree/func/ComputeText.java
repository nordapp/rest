package org.i3xx.util.ctree.func;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComputeText {
	
	private static final Pattern pattern = Pattern.compile("(([^\\{^\\}]+)|(\\{\\d+\\+?\\}))");
	
	private static final Logger logger = LoggerFactory.getLogger(ComputeText.class);
	
	/**
	 * @param stmt The statement to complete
	 * @param args The arguments
	 * @return
	 */
	public static final String complete(String stmt, String[] args) {
		
		Matcher m = pattern.matcher(stmt);
		
		StringBuffer buf = new StringBuffer();
		
		while(m.find()) {
			String tok = m.group();
			// regexp: { \d+ }
			if(tok.length()>2 && tok.charAt(0)=='{' && tok.charAt(tok.length()-1)=='}') {
				String d = tok.substring(1, tok.length()-1);
				//process factor '+'
				boolean f = (d.charAt(d.length()-1)=='+');
				d = f ? d.substring(0, d.length()-1) : d;
				//
				try{
					int i = Integer.parseInt(d);
					if(i>-1 && i<args.length) {
						if(f) {
							for(int j=i;j<args.length;j++) {
								if(j>i)
									buf.append('.');
								
								buf.append(args[j]);
							}
						}else
							buf.append(args[i]);
					}else{
						logger.debug("The index {} is out of range: {}.", i, stmt);
					}//fi
				}catch(NumberFormatException e){
					logger.debug("Number format exception", e);
				}
			}else{
				buf.append(tok);
			}//fi
		}//while
		
		
		return buf.toString();
	}
}
