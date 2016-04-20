package org.i3xx.util.ctree.parser;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import org.i3xx.util.ctree.IConfNode;
import org.i3xx.util.ctree.core.IResolverFactory;
import org.i3xx.util.ctree.impl.NodeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KeyValueRuleProcessLine extends AbstractKeyValueRule {
	
	private static final Logger logger = LoggerFactory.getLogger(KeyValueRuleProcessLine.class);
	
	// 
	public static final Pattern separator = Pattern.compile("\\s*[\\s|=]\\s*");
	
	protected IConfNode root;
	
	private NodeParser nParser;
	
	public KeyValueRuleProcessLine(IConfNode root, IResolverFactory factory) {
		super();
		
		this.root = root;
		//The NodeParser does the resolving of the value of the configuration node.
		this.nParser = new NodeParser(factory);
	}

	@Override
	public void exec(String stmt, Map<String, String> params) throws IOException {
		String key = null;
		String param = null;
		
		String[] temp = separator.split(stmt, 2);
		if(temp.length==0) {
			return;
		}else if(temp.length==1) {
			key = temp[0].trim();
			param = "";
		}else{
			key = temp[0].trim();
			param = temp[1].trim();
		}
		
		String prefix = params.get("prefix");
		if(prefix!=null){
			key = prefix + "." + key;
		}
		
		//Convert text/xml to text/plain
		logger.trace("Add to configuration key:{}, value:{}", key, param);
		
		//Insert to confTree
		IConfNode node = root.create(key);
		node.value( param );
		
		//Ensure to remove a former resolving to avoid malfunction.
		node.resolved(false);
		node.resolver(null);
		
		nParser.process(node);
	}

	@Override
	public boolean match(String stmt, Map<String, String> params) throws IOException {
		return super.match(stmt, params);
	}

}
