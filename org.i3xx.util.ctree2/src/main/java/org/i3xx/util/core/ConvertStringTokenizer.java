package org.i3xx.util.core;

import java.util.Enumeration;
import java.util.Vector;

public class ConvertStringTokenizer implements Enumeration<Object>
{

	protected Vector<ElementNode> tokens;
	protected Enumeration<ElementNode> enumeration;
	protected ElementNode current; 
	
	/**
	 * @param value The statement to tokenize
	 * @param startToken e.g '${'
	 * @param endToken e.g '}'
	 */
	public ConvertStringTokenizer(String value, String startToken, String endToken)
	{
		tokens = new Vector<ElementNode>();
		
		StringBuffer out = new StringBuffer(); //text token
		StringBuffer buf = new StringBuffer(); //start/stop token fragments
		
		boolean check = false;
		boolean checked = false;
		
		int i=0; //count pos in value
		int n=0; //count pos in start/end token
		do{
			if(check){
				buf.append(value.charAt(i));
				//handle start/stop token
				if(checked){
					//stop token
					if(value.charAt(i) == endToken.charAt(n)){
						n++; //continue checking
						if(endToken.length()==n){
							//end token found
							check = false;
							checked = false;
							tokens.add(new ElementNode(out.toString(), false, false, true));
							out = new StringBuffer();
							n=0;
							//write out
							tokens.add(new ElementNode(buf.toString(), false, true, false));
							buf = new StringBuffer();
						}
					}else{
						//no end token, write out
						out.append(buf.toString());
						check = false;
						checked = false;
						buf = new StringBuffer();
						n=0;
					}
				}else{
					//start token
					if(value.charAt(i) == startToken.charAt(n)){
						n++; //continue checking
						if(startToken.length()==n){
							//start token found
							check = false;
							checked = true;
							tokens.add(new ElementNode(out.toString(), false, false, false));
							out = new StringBuffer();
							n=0;
							//write text
							tokens.add(new ElementNode(buf.toString(), true, false, false));
							buf = new StringBuffer();
						}
					}else{
						//no start token, write out
						out.append(buf.toString());
						check = false;
						checked = false;
						buf = new StringBuffer();
						n=0;
					}
				}
			}else if(checked){
				//ignore start token
				//test for stop token
				if(value.charAt(i) == endToken.charAt(0)){
					check = true;
					n++;
					buf.append(value.charAt(i));
					
					if(endToken.length()==n){
						//end token found
						check = false;
						checked = false;
						tokens.add(new ElementNode(out.toString(), false, false, true));
						out = new StringBuffer();
						n=0;
						//write out
						tokens.add(new ElementNode(buf.toString(), false, true, false));
						buf = new StringBuffer();
					}
				}else{
					//write to output
					out.append(value.charAt(i));
				}
			}else{
				//ignore stop token
				//test for start token
				if(value.charAt(i) == startToken.charAt(0)){
					//first char of start token
					check = true;
					n++;
					buf.append(value.charAt(i));
					
					if(startToken.length()==n){
						//start token found
						check = false;
						checked = true;
						tokens.add(new ElementNode(out.toString(), false, false, false));
						out = new StringBuffer();
						n=0;
						//write text
						tokens.add(new ElementNode(buf.toString(), true, false, false));
						buf = new StringBuffer();
					}
				}else{
					//write to output
					out.append(value.charAt(i));
				}
			}
			i++;
		}while(i<value.length());
		
		out.append(buf.toString());
		tokens.add(new ElementNode(out.toString(), false, false, false));
		
		enumeration = tokens.elements();
	}

	public boolean hasMoreElements()
	{
		return enumeration.hasMoreElements();
	}

	public Object nextElement()
	{
		return enumeration.nextElement();
	}
	
	public boolean hasNextToken(){
		return hasMoreElements();
	}
	
	public String nextToken(){
		current = enumeration.nextElement();
		return current.text;
	}
	
	public boolean tokenIsText(){
		return ( ! (current.startToken || current.endToken || current.element) );
	}
	
	public boolean tokenIsElement(){
		return current.element;
	}
	
	private class ElementNode
	{
		public String text;
		public boolean startToken;
		public boolean endToken;
		public boolean element;
		
		public ElementNode(String t, boolean st, boolean et, boolean e){
			text = t;
			startToken = st;
			endToken = et;
			element = e;
		}
	}
}
