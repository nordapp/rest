package org.i3xx.util.rna.core;

public interface IDocDb {
	
	/** Write the reference in the brick */
	public static final int WRITE_REF = 0;
	
	/** Write the document in the database */
	public static final int WRITE_DOC = 1;
	
	/** Write a placeholder for the document in the database */
	public static final int WRITE_NONE = 2;
	
	/** Write only the latest version of the document in the database */
	public static final int WRITE_LATEST = 3;
}
