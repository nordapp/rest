package org.i3xx.util.ctree.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AbstractElementReader extends AbstractReader {
	
	protected boolean init;
	protected boolean open;
	protected List<String> elements;
	
	public AbstractElementReader() {
		super();
		
		this.init = false;
		this.open = true;
		this.elements = new ArrayList<String>();
	}
	
	/**
	 * Gets the number corresponding to the name
	 * 
	 * @param name The name
	 * @return The number
	 */
	protected int getElementNumber(String name) {
		return ArrayIndexMap.get(name).intValue();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.parser.AbstractReader#available()
	 */
	public boolean available() throws IOException {
		return elements.size()>0;
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.parser.AbstractReader#readNexte()
	 */
	public String readNext() throws IOException {
		return elements.remove(0);
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.util.ctree.parser.AbstractReader#close()
	 */
	public void close() throws IOException {
		this.open = false;
		elements.clear();
	}
}
