package com.i3xx.util.ctree.parser;

import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;

import com.i3xx.util.ctree.IConfNode;
import com.i3xx.util.ctree.core.IResolverFactory;
import com.i3xx.util.ctree.impl.NodeParser;

public class KeyValueRuleProcessLine extends AbstractKeyValueRule {
	
	public static final String separator = " \t\n\r";
	
	protected IConfNode root;
	
	private NodeParser nParser;
	
	public KeyValueRuleProcessLine(IConfNode root, IResolverFactory factory) {
		super();
		
		this.root = root;
		this.nParser = new NodeParser(factory);
	}

	@Override
	public void exec(String stmt, Map<String, String> params) throws IOException {
		String key = null;
		String param = null;
		
		StringTokenizer parser = new StringTokenizer(stmt, separator);
		
		//Erster Token ist Schlüssel
		if(parser.hasMoreTokens())
			key = parser.nextToken();
		if(key==null)
			return;
		
		//Zweiter Token ist Wert
		if(parser.hasMoreTokens())
			param = parser.nextToken();
		else
			param = "";
		
		//Weitere Token zu Wert
		while(parser.hasMoreTokens()){
			param += " " + parser.nextToken();
		}
		
		String prefix = params.get("prefix");
		if(prefix!=null){
			key = prefix + "." + key;
		}
		
		//Convert text/xml to text/plain
		//TODO:
		
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
