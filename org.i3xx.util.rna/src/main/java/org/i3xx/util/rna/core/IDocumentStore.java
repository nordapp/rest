package org.i3xx.util.rna.core;

public interface IDocumentStore {

	/**
	 * Migrates a document into the store and returns the access key (uri)
	 * 
	 * @param doc
	 * @return
	 */
	String migrate(IIdsDocument doc);
	
	/**
	 * Gets a document from the store
	 * 
	 * @param uri
	 * @return
	 */
	IIdsDocument fetch(String uri);
	
}
