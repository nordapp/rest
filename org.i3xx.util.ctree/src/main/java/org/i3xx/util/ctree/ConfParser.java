/* robots=none */
package org.i3xx.util.ctree;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.i3xx.util.core.FillReader;
import org.i3xx.util.ctree.func.IResolver;
import org.i3xx.util.ctree.func.IVarNode;
import org.i3xx.util.ctree.func.LinkResolver;
import org.i3xx.util.ctree.func.VarNode;
import org.i3xx.util.ctree.func.WildcardResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Parst die Konfiguration
 * 
 * Nicht aufgel�ste Links bleiben stehen und der Baum wird verarbeitet, bis alle Links
 * aufgel�st werden k�nnen oder in einem Durchlauf keine Links aufgel�st werden.
 * 
 * ACHTUNG: Die Funktionsweise ist schwer verst�ndlich.
 * 
 * In der Konfiguration bedeutet die folgende Sequenz einen Verweis zu einem anderen Eintrag:
 * PCNAME ::= ('_' | [a-z] | [A-Z]) ('_' | [a-z] | [A-Z] | [0-9])* 
 * Link   ::= '[' PCNAME ']'
 * 
 * Die folgenden Zeichen m�ssen mit '\' gesch�tzt werden: '[', ']' und '\'
 * 
 * Je nach Verarbeitungsschritt (der InsertConfReader entfernt den Schutz und wertet '[' ']' aus)
 * m�ssen vorhandene '[' oder ']' gesch�tzt oder vom Sch�tzen ausgenommen werden. Dazu wird
 * wie folgt konvertiert: '[' = \u005B -> \uFF5B, ']' = \u005D -> \uFF5D
 * 
 * Ist der Wert ein '[' oder ']' wird dieser gesch�tzt. Da durch die Verarbeitung in InsertConfReader
 * das sch�tzende '\' entfernt wird mu� nach der Verarbeitung erneut der Schutz '\' hinzugef�gt werden.
 * 
 * Handelt es sich um einen Link, m�ssen die Klammern vom Schutz ausgenommen werden und vor der
 * n�chsten Verarbeitung zur�ckgewandelt werden \uFF5B -> \u005B = '[', \uFF5D -> \u005D = ']'
 * 
 * Beispiel (? nicht druckbare Zeichen \u005B und \u005D):
 * 
 * Eintrag    (191) : [test.selection.prefix]/child::*\[name == "EinName"\]
 * Key lesen  (249) : test.selection.prefix
 * Key aus 1* (282) : ?test.selection.prefix?
 * Wert lesen (204) : /child::*[name == "EinName"]
 * Wert aus   (204) : /child::*[name == "EinName"]
 * Parser aus (204) : ?test.selection.prefix?/child::*[name == "EinName"]
 * Schutz     (208) : ?test.selection.prefix?/child::*\[name == "EinName"\]
 * Umwandeln  (214) : [test.selection.prefix]/child::*\[name == "EinName"\]
 * 
 * 1* In diesem Beispiel kann der Key nicht aufgel�st werden. Wird der Key
 *    aufgel�st (resolved), wird der Linkwert entsprechend konvertiert und
 *    eingef�gt. '[' -> \uFF5B, \[ -> '[', ']' -> \uFF5D, \] -> ']'
 *    
 * Aufl�sen   (262) : //self::*\[name == "NochEinName"\]
 * Umwandeln  (271) : //self::*[name == "NochEinName"]
 * Umwandeln  (210) : //self::*\[name == "NochEinName"\]
 *    
 * 
 * @author Stefan
 * @since 24.07.2008
 * 
 * @Deprecated: Use new parser concept (com.uil.ctree.parser.*).
 */
@Deprecated
public class ConfParser implements IParser
{
	
	private static final Logger logger = LoggerFactory.getLogger(ConfParser.class);
	
	private static final int NOT_INITIALIZED = 0x0;
	private static final int INITIALIZED = 0x1;
	private static final int CONVERTED = 0x2;
	private static final int INTERRUPTED = 0x4;
	private static final int UNESCAPED = 0x8;
	
	private int fUpd;
	private IConfNode root;
	
	//Das Ersetzen mittels RegExp nach \u005C ist in Java nicht m�glich.
	//Workaround: Ersetzen mit RegExp nach CHAR_A_5C und anschlie�end ohne RegExp
	//nach CHAR_U_5C ersetzen.
	private static char CHAR_A_5C = "\uFF41".charAt(0); //A
	
	//Ein X f�r ein U vormachen (vice versa)
	public static char CHAR_X_5B = "\uFF5B".charAt(0); //[
	public static char CHAR_X_5D = "\uFF5D".charAt(0); //]
	public static char CHAR_X_5C = "\uFF5C".charAt(0); //\
	
	private static char CHAR_U_5B = "\u005B".charAt(0); //[
	private static char CHAR_U_5D = "\u005D".charAt(0); //]
	private static char CHAR_U_5C = "\u005C\u005C".charAt(0); //\ siehe Workaround
	
	public ConfParser() throws IOException
	{
		this.fUpd = NOT_INITIALIZED;
		this.root = new ConfNode();
	}

	public ConfParser(IConfNode root) throws IOException
	{
		if(root == null)
			root = new ConfNode();

		this.fUpd = NOT_INITIALIZED;
		this.root = root;
	}

	public void addconf(String file) throws IOException
	{
		if(logger.isDebugEnabled())
			logger.debug("Add the configuration file '{}'", file);
		
		if(file==null)
			return;
		
		ConfFile c = new ConfFile(this);
		c.parse(file);
	}

	public void addconf(String k, String v) throws IOException
	{
		if(logger.isTraceEnabled())
			logger.trace("Add the configuration '{}' : '{}'", k, v);
		
		if(k==null || v==null)
			return;
		
		root.create(k).value( v );
	}

	public void setProperties(Properties props, String prefix) throws IOException
	{
    	Iterator<Object> iter = props.keySet().iterator();
    	while(iter.hasNext()){
    		String key = (String)iter.next();
    		addconf(prefix+"."+key, props.getProperty(key));
    		//root.create(prefix+"."+key).value( props.getProperty(key) );
    	}
	}

	public void setPropertiesD(Properties props, String prefix) throws IOException
	{
    	Iterator<Object> iter = props.keySet().iterator();
    	while(iter.hasNext()){
    		String key = (String)iter.next();
    		addconf(prefix+"."+key, props.getProperty(key).replace("\\", "\\\\"));
    		//root.create(prefix+"."+key).value( props.getProperty(key) );
    	}
	}

	public IConfNode getRoot()
	{
		return root;
	}
	
	/**
	 * Updates all references
	 * 
	 * @throws IOException
	 */
	public void convert() throws IOException
	{
		if(this.fUpd == UNESCAPED)
			throw new IllegalStateException("This operation is not allowed if unescaped once (do not run twice).");
		
		//update all entities
		Converter c = new Converter();
		do{
			this.fUpd = INITIALIZED; //remove CONVERTED, INTERRUPTED
			c.process();
		}while( (this.fUpd & CONVERTED) == CONVERTED );
		
		//Remove the escape char '\' whereever there is one
		for(IConfNode en:c.escaped.values()){
			String v = en.rawValue();
			//Replace escaped '[' and ']'
			//Function of the regular expression:
			//Match 1: '[', escaped with \\[, otherwise ']', escaped with \\]
			//Treffer 1: '[', gesch�tzt durch \\[ bzw. ']', gesch�tzt durch \\]
			//Match 2: ']', escaped with \\[, otherwise ']', escaped with \\]
			//Treffer 2: ']', gesch�tzt durch \\[ bzw. ']', gesch�tzt durch \\]
			//replaced by Match 1 followd by CHAR_U_5B or CHAR_U_5D
			//Println.debug("<> "+value);
			v = v.replaceAll("([\\\\]+)\\[", String.valueOf(CHAR_U_5B));
			v = v.replaceAll("([\\\\]+)\\]", String.valueOf(CHAR_U_5D));
			v = v.replaceAll("([\\\\]+)\\\\", String.valueOf(CHAR_A_5C));
			v = v.replace(CHAR_A_5C, CHAR_U_5C); //Workaround CHAR_A_5C => CHAR_U_5C
			/* v = v.replace("\\\\", "\\"); unable to do this in java with regexp */
			en.value( v );
		}
		
		this.fUpd = UNESCAPED;
	}
	
	/**
	 * @return true if the conversion process has not yet run
	 */
	public boolean isRaw() {
		return (this.fUpd == NOT_INITIALIZED);
	}
	
	/**
	 * Konverter f�r Konfigurationsvariablen
	 * 
	 * @author Stefan
	 */
	private class Converter
	{
		Map<String, IConfNode> escaped;
		
		public Converter(){
			escaped = new HashMap<String, IConfNode>();
		}
		
		/**
		 * @throws IOException
		 */
		public void process() throws IOException {
			iterate(root);
		}
		
		/**
		 * @param node
		 * @throws IOException
		 */
		public void iterate(IConfNode node) throws IOException {
			IConfNode[] nx = node.getChildNodes3();
			for(IConfNode n:nx){
				update(n);
				iterate(n);
			}
		}
		
		/**
		 * @param node
		 * @throws IOException
		 */
		public void update(IConfNode node) throws IOException
		{
			String v = node.rawValue();
			if(v==null || v.equals(""))
				return;
			
			//Replace CHAR_X_5B and CHAR_X_5D with the origin char to resolve the entities
			v = v.replace(CHAR_X_5B, CHAR_U_5B);
			v = v.replace(CHAR_X_5D, CHAR_U_5D);
			v = v.replace(CHAR_X_5C, CHAR_U_5C);
			//Println.debug(">>"+node.getFullName()+"\t"+v);
			
			Reader in = new InsertConfReader( new StringReader( v ), node);
			StringWriter out = new StringWriter();
			
			int c=0;
			while((c=in.read())>-1){
				
				//InsertConfReader removes the escape sequence,
				//escape again
				if(c=='[' || c==']' || c=='\\')
					out.write('\\');
				
				//Do not escape CHAR_X_5B and CHAR_X_5D. These chars are a replacement for
				//CHAR_U_5B and CHAR_U_5D ('[' and ']'). Replace these chars with the origin
				//ones.
				if(c==CHAR_X_5B)
					out.write(CHAR_U_5B);
				else if(c==CHAR_X_5D)
					out.write(CHAR_U_5D);
				else if(c==CHAR_X_5C)
					out.write(CHAR_U_5C);
				else
					out.write(c);
			}
			
			in.close();
			out.close();
			
			v = out.toString();
			node.value( v );
			
			//the escaped elements have to be cleaned up last
			if(v.indexOf('\\')>-1)
				escaped.put(node.getFullName(), node);
		}
	}

	/**
	 *	Konverterstrom f�r Konfigurationseintr�ge
	 */
	private class InsertConfReader extends FillReader
	{
		private IConfNode node;
		
		public InsertConfReader(Reader in, IConfNode node)
		{
			super(in, '[', ']');
			
			this.node = node;
		}
		
		public char[] convert(char[] buf) throws IOException
		{
			char[] b = new char[buf.length-2];
			System.arraycopy(buf, 1, b, 0, buf.length-2);
			
			//The name is the string in ${name} or [name]
			String name = String.valueOf(b);
			String currentFullName = node.getFullName();
			try {
				
				//a function is still in the name
				IVarNode node = new VarNode();
				node.setValue(name);
				node.parse();
				
				IResolver resolver = new WildcardResolver(currentFullName);
				node.resolve(resolver);
				if(resolver.resolved() && (fUpd & INTERRUPTED) == 0)
					fUpd |= CONVERTED;
				
				resolver = new LinkResolver(this.node);
				node.resolve(resolver);
				if(resolver.resolved() && (fUpd & INTERRUPTED) == 0)
					fUpd |= CONVERTED;
				
				/*
				if( name.contains("*") ){
					if(name.contains("->")){
						buf = resolveCNameOfTargetInLink( name, currentFullName ).toCharArray();
					}else{
						buf = resolveCName( name, currentFullName ).toCharArray();
					}
				}else if( name.contains("->") ){
					buf = resolveCLink( name, currentFullName ).toCharArray();
				}else{
				*/
				
				if(node.isComment()){
					//simple comment, do not surround with brackets or process
					buf = node.getValue().toCharArray();
				}else if(node.isLeaf()){
					name = node.getValue();
					String value = root.get( name ).rawValue();
					
					//This is a link. The name is a resolved link to another node
					//Use the name as value.
					if(value==null){
						//Assertion:
						if( name.equals( currentFullName ) )
							throw new IllegalArgumentException("A null value is only supported at a link.");
						
						//Println.debug("<< path:"+node.getFullName()+" name:"+name+" value:"+value);
						value = name;
					}
					
					//simple links are not resolved but replaced
					if( (fUpd & INTERRUPTED) == 0 ){
						fUpd |= CONVERTED;
					}
					
					//Replace not escaped '[' and ']'
					//Function of the regular expression:
					//Match 1: Not '\', not is '^', escaped with \\\\, grouped with '(' ')'
					//Caution: The '?' is needed because missing '\' is the same as not '\'.
					//Match 2: '[', escaped with \\[, otherwise ']', escaped with \\]
					//Treffer 2: '[', gesch�tzt durch \\[ bzw. ']', gesch�tzt durch \\]
					//replaced by Match 1 followd by CHAR_X_5B or CHAR_X_5D
					//Println.debug("<> "+value);
					value = value.replaceAll("([^\\\\]?)\\[", "$1"+String.valueOf(CHAR_X_5B));
					value = value.replaceAll("([^\\\\])\\]", "$1"+String.valueOf(CHAR_X_5D));
					value = value.replaceAll("([^\\\\])\\\\", "$1"+String.valueOf(CHAR_X_5C));
					//Println.debug("<> "+value);
					
					buf = value.toCharArray();
				}else{
					String value = node.toString();
					if(node.getOp()!=null)
						value = "["+value+"]";
					
					value = value.replaceAll("([^\\\\]?)\\[", "$1"+String.valueOf(CHAR_X_5B));
					value = value.replaceAll("([^\\\\])\\]", "$1"+String.valueOf(CHAR_X_5D));
					value = value.replaceAll("([^\\\\])\\\\", "$1"+String.valueOf(CHAR_X_5C));
					buf = value.toCharArray();
				}
			}catch(NoSuchElementException e){
				//throw new IOException("No key \"" + name + "\" in configuration.");
                logger.debug("No key '{}' in configuration.", name);
                
        		fUpd |= INTERRUPTED;
				buf = protectName(name).toCharArray(); //avoid escaping
			}catch(NullPointerException e){
				//throw new IOException("No key \"" + name + "\" in configuration.");
                logger.debug("No key '{}' in configuration.", name);
                e.printStackTrace();
                
        		fUpd |= INTERRUPTED;
				buf = protectName(name).toCharArray(); //avoid escaping
			}

			//Println.debug("_ "+node.getFullName()+"\t"+String.valueOf(buf));
			//for(int i=0;i<buf.length;i++){
			//	if(buf[i] == '\\'){
			//		Assert.except(true, "Backslash in: "+String.valueOf(buf));
			//		break;
			//	}
			//}
			return buf;
		}
		
		/**
		 * Surround with brackets that will not be escaped
		 * 
		 * @param name
		 * @return
		 */
		private String protectName(String name) {
			return CHAR_X_5B+name+CHAR_X_5D;
		}
		
	} // private class
}
