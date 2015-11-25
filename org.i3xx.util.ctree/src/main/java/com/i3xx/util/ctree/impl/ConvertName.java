package com.i3xx.util.ctree.impl;

public class ConvertName {

	/**
	 * Converts a configuration name to a valid java identifier.
	 * The name must match the definition described in spec.txt.
	 * 
	 * @param name
	 * @return
	 */
	public static String toCamelCase(String name) {
		StringBuffer buf = new StringBuffer();
		
		boolean uCase = false;
		for(int i=0;i<name.length();i++){
			char c = name.charAt(i);
			if(c=='-'){
				uCase = true; //§1 of the spec
				continue; //§2 of the spec
			}
			if(uCase){
				uCase = false;
				c = Character.toUpperCase(c);
			}
			buf.append(c);
		}
		
		return buf.toString();
	}
	
}
