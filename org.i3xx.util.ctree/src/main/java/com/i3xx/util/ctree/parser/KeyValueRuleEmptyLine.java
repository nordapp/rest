package com.i3xx.util.ctree.parser;

import java.io.IOException;
import java.util.Map;


public class KeyValueRuleEmptyLine extends AbstractKeyValueRule {

	public KeyValueRuleEmptyLine() {
		super();
	}

	@Override
	public void exec(String stmt, Map<String, String> params) throws IOException {
		//skip the empty line
	}

	@Override
	public boolean match(String stmt, Map<String, String> params) throws IOException {
		return stmt.equals("") && super.match(stmt, params);
	}

}
