package org.i3xx.util.rna.impl;

import java.util.NoSuchElementException;

import org.i3xx.util.rna.core.IIdsDocument;
import org.i3xx.util.rna.core.IIdsStore;

public class IdsDocumentAsString implements IIdsDocument {
	
	private String document;
	
	public IdsDocumentAsString(String document) {
		this.document = document;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return document;
	}
	
	//
	// Unsupported operations
	//
	
	public long ID() {
		throw new UnsupportedOperationException("The operation is not supported. This is the string representation of a document");
	}

	public void ID(long id) {
		throw new UnsupportedOperationException("The operation is not supported. This is the string representation of a document");
	}

	public long TS(int i) throws NoSuchElementException {
		throw new UnsupportedOperationException("The operation is not supported. This is the string representation of a document");
	}

	public int createVersion() {
		throw new UnsupportedOperationException("The operation is not supported. This is the string representation of a document");
	}
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public void delete() {
		throw new UnsupportedOperationException("The operation is not supported. This is the string representation of a document");
	}

	public void destroy(int i) throws NoSuchElementException {
		throw new UnsupportedOperationException("The operation is not supported. This is the string representation of a document");
	}

	public IIdsStore get(int i) throws NoSuchElementException {
		throw new UnsupportedOperationException("The operation is not supported. This is the string representation of a document");
	}

	public int last() {
		throw new UnsupportedOperationException("The operation is not supported. This is the string representation of a document");
	}

	public int length(int i) throws NoSuchElementException {
		throw new UnsupportedOperationException("The operation is not supported. This is the string representation of a document");
	}

	public String mimetype(int i) throws NoSuchElementException {
		throw new UnsupportedOperationException("The operation is not supported. This is the string representation of a document");
	}

	public void mimetype(String m, int i) throws NoSuchElementException {
		throw new UnsupportedOperationException("The operation is not supported. This is the string representation of a document");
	}

}
