package org.i3xx.util.ctree.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.i3xx.util.ctree.IConfNode;
import org.i3xx.util.ctree.parser.AbstractReader;
import org.i3xx.util.ctree.parser.AbstractRule;
import org.i3xx.util.ctree.parser.IParserWrapper;
import org.i3xx.util.ctree.parser.KeyValueRuleComment;
import org.i3xx.util.ctree.parser.KeyValueRuleEmptyLine;
import org.i3xx.util.ctree.parser.KeyValueRuleIncludeA;
import org.i3xx.util.ctree.parser.KeyValueRuleProcessLine;
import org.i3xx.util.ctree.parser.LineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The default parser parses the configuration files into the configuration tree.
 * 
 * How to read the configuration:
 * - DefaultParser.process()
 * - DefaultParser.process(LineReader reader) <-------
 *     //LineReader family                            |  The recursive circle
 *   - String line = AbstractLineReader.readNext()    |  to load all files.
 *     - KeyValueRuleIncludeA.exec(line)              |  KeyValueIncludeA got
 *         //LineReader family                        |  a link to this.
 *       - AbstractLineReader.readNext() -------------
 * 
 * @author Stefan
 *
 */
public class DefaultParser implements IParserWrapper {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(DefaultParser.class);
	
	private File file;
	private AbstractRule[] inputRules;
	
	/**
	 * Parses the parameter file and builds the configuration
	 * 
	 * @param fileName The file to parse
	 * @throws IOException
	 */
	public DefaultParser(String fileName) throws IOException {
		this(new File(fileName));
	}
	
	/**
	 * Parses the parameter file and builds the configuration
	 * 
	 * @param file The file to parse
	 * @throws IOException
	 */
	public DefaultParser(File file) throws IOException {
		this.file = file;
		this.inputRules = null;
	}
	
	/**
	 * Set the rules
	 * @param root The root node
	 */
	public void setRules(IConfNode root) {
		
		inputRules = new AbstractRule[4];
		
		inputRules[0] = new KeyValueRuleIncludeA(this);
		inputRules[1] = new KeyValueRuleComment();
		inputRules[2] = new KeyValueRuleEmptyLine();
		inputRules[3] = new KeyValueRuleProcessLine(root, new LinkableResolverFactory());
	}
	
	/**
	 * Process the input
	 * 
	 * @throws IOException
	 */
	public void process() throws IOException {
		LineReader reader = new LineReader( new FileReader(file));
		reader.setParams(new HashMap<String, String>());
		reader.getParams().put("filename", file.getAbsolutePath());
		reader.getParams().put("mimetype", "text/plain");
		reader.getParams().put("datatype", "line");
		process( reader );
		reader.close();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.parser.IParserWrapper#process(com.i3xx.util.ctree.parser.AbstractReader)
	 */
	public void process(AbstractReader current) throws IOException {
		
		//
		//Println.console("[CONF] Read file '"+
		//		(current.getParams()!=null && current.getParams().get("filename")!=null ? 
		//				current.getParams().get("filename") : "-")+"'");
		//
		
		while(current.available()) {
			String data = current.readNext();
			
			//
			// The whole configuration can be accessed here
			//
			
			String mime = current.getParams().get("mimetype");
			if(mime!=null && mime.equals("text/xml")){
				data = Protector.escape(data);
			}
			data = Protector.wrap(data);
			
			//
			// Run the rules to every line
			//
			for(int i=0;i<inputRules.length;i++){
				if(inputRules[i].match(data, current.getParams())){
					inputRules[i].exec(data, current.getParams());
					break;
				}
			}//for
		}//while
	}
}
