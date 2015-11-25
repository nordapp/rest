package org.i3xx.util.ctree.parser;

import java.io.IOException;
import java.util.Map;

public abstract class AbstractKeyValueRule extends AbstractRule {

	public AbstractKeyValueRule() {
		super();
	}
	
	@Override
	public boolean match(String stmt, Map<String, String> params) throws IOException {
		return params.containsKey("datatype") && params.get("datatype").equals("line");
	}
	
}
