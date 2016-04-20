package org.i3xx.util.ctree.parser;

import java.io.IOException;
import java.util.Map;


public class KeyValueRuleComment extends AbstractKeyValueRule {

	public KeyValueRuleComment() {
		super();
	}

	@Override
	public void exec(String stmt, Map<String, String> params) throws IOException {
		//skip the comment
	}

	@Override
	public boolean match(String stmt, Map<String, String> params) throws IOException {
		return stmt.startsWith("#") && super.match(stmt, params);
	}

}
