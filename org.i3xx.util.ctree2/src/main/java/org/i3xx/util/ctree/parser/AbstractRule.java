package org.i3xx.util.ctree.parser;

import java.io.IOException;
import java.util.Map;

public abstract class AbstractRule {
	
	public AbstractRule() {
	}
	
	/**
	 * @return True if the rule matches
	 * @param stmt The current statement.
	 * @param params The parameter
	 */
	public abstract boolean match(String stmt, Map<String, String> params) throws IOException;
	
	/**
	 * The processing of the rule
	 * @param stmt The current statement.
	 * @param params The parameter
	 */
	public abstract void exec(String stmt, Map<String, String> params) throws IOException;
}
