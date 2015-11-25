package com.i3xx.util.ctree.parser;

import java.io.IOException;

public interface IParserWrapper {

	/**
	 * Process the parser reader (includes)
	 *   
	 * @param reader
	 * @throws IOException
	 */
	void process(AbstractReader reader) throws IOException;
}
