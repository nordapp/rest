package org.i3xx.util.ctree.impl;

import java.util.regex.Pattern;

/**
 * 
 * Protected pattern: Get X instead of U to avoid matching
 * 
 * 
 * It is not possible to replace to \u005C with java replaceAll("\\\\", "\\").
 * Workaround: Replace to \uFF41 and after that
 * replace to \u005C.
 *  \uFF41 = A
 * 
 *  The X
 *  "\uFF5B" = [
 *  "\uFF5D" = ]
 *  "\uFF5C" = \
 * 
 * The U
 *  "\u005B" = [
 *  "\u005D" = ]
 *  "\uFF41" = \ (Workaround)
 * @author Stefan
 *
 */
public class Protector {
	
	public static final Pattern testPtn = Pattern.compile(".*\\\\[\\[|\\]].*");
	public static final Pattern pattern = Pattern.compile(".*[\uFF5B|\uFF5D].*");
	
	/**
	 * @param text
	 * @return
	 */
	public static boolean match(String text) {
		
		if(pattern.matcher(text).matches())
			return true;
		
		return false;
	}
	
	/**
	 * @param text
	 * @return
	 */
	public static boolean testMatch(String text) {
		
		if(testPtn.matcher(text).matches())
			return true;
		
		return false;
	}
	
	/**
	 * Wraps the text but removes any single '\' from. Wrapping the text is to hide the text from matchers.
	 * To do this the wrapper uses char conversion
	 * \u005B -> \uFF5B '[
	 * \u005D -> \uFF5D ']
	 * 
	 * Removes any single '\' from the text.
	 * 
	 * @param text The text
	 * @return
	 */
	public static final String wrap(String text) {
		/* No regexp */
		
		final StringBuffer buf = new StringBuffer();
		final char[] cc = text.toCharArray();
		for(int i=0;i<cc.length;i++){
			char c = cc[i];
			//look 
			if(c=='\\' && i<(cc.length-1)){
				i++;
				c = cc[i];
			}else{
				switch(c){
				case '[':
					c = '\uFF5B';
					break;
				case ']':
					c = '\uFF5D';
					break;
				}
			}
			buf.append(c);
		}
		return buf.toString();
	}
	
	/**
	 * Unwrap the text - undo the wrapping by converting to the original chars.
	 * \uFF5B -> \u005B '[
	 * \uFF5D -> \u005D ']
	 * 
	 * 
	 * @param text The text
	 * @return
	 */
	public static final String unwrap(String text) {
		/*
		text = text.replaceAll("\uFF5B", "\u005B");
		text = text.replaceAll("\uFF5D", "\u005D");
		return text;
		*/
		
		final char[] cc = text.toCharArray();
		for(int i=0;i<cc.length;i++){
			switch(cc[i]){
			case '\uFF5B':
				cc[i] = '[';
				break;
			case '\uFF5D':
				cc[i] = ']';
				break;
				default:
			}
		}
		return new String(cc);
	}
	
	/**
	 * Escapes the text
	 * [ -> \[
	 * ] -> \]
	 * \ -> \\
	 * 
	 * @param text The text
	 * @return
	 */
	public static final String escape(String text) {
		//Note: Expression '\\[' means '['
		//Note: Expression '\\]' means ']'
		/*
		text = text.replaceAll("\\[", "\uFF41[");
		text = text.replaceAll("\\]", "\uFF41]");
		text = text.replace('\uFF41', '\\'); //workaround
		return text;
		*/
		
		final StringBuffer buf = new StringBuffer();
		final char[] cc = text.toCharArray();
		final char e = '\\';
		for(int i=0;i<cc.length;i++){
			char c = cc[i];
			switch(c){
			case '[':
				buf.append(e);
				buf.append(c);
				break;
			case ']':
				buf.append(e);
				buf.append(c);
				break;
			case '\\':
				buf.append(e);
				buf.append(c);
				break;
				default:
					buf.append(c);
			}
		}
		
		return buf.toString();
	}
	
	/**
	 * Undo the escape of the text
	 * \c -> c
	 * \\ -> \
	 * 
	 * @param text The text
	 * @return
	 */
	public static final String unescape(String text) {
		//Note: Expression '\\\\\\\\' means '\\'
		//Note: Expression '\\\\\\[' means '\['
		//Note: Expression '\\\\\\]' means '\]'
		/*
		text = text.replaceAll("\\\\\\\\", "\\\\");
		text = text.replaceAll("\\\\\\[", "[");
		text = text.replaceAll("\\\\\\]", "]");
		return text;
		*/
		
		final StringBuffer buf = new StringBuffer();
		final char[] cc = text.toCharArray();
		for(int i=0;i<cc.length;i++){
			char c = cc[i];
			//look 
			if(c=='\\' && i<(cc.length-1)){
				i++;
				c = cc[i];
			}
			buf.append(c);
		}
		
		return buf.toString();
	}
}
