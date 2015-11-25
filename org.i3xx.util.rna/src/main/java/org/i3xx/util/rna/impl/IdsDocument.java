package org.i3xx.util.rna.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.i3xx.util.rna.core.IIdsDocument;
import org.i3xx.util.rna.core.IIdsStore;

public class IdsDocument implements IIdsDocument
{
	protected List<IIdsStore> content;
	protected long m_id;

	public IdsDocument() {
		content = new ArrayList<IIdsStore>();
		m_id = 0;
	}

	public long ID() {
		return m_id;
	}

	public void ID(long id) {
		m_id = id;
	}

	public Object clone() throws CloneNotSupportedException {
		// Mechanismus, siehe CBrick
		IdsDocument result = (IdsDocument)super.clone();
		result.content = new ArrayList<IIdsStore>();
		Iterator<IIdsStore> iter = content.iterator();
		IIdsStore store;
		while(iter.hasNext()) {
			store = iter.next();
			result.content.add( (IIdsStore)((IdsStore)store).clone());
		}
		return result;
	}

	public void delete() {
		Iterator<IIdsStore> iter = content.iterator();
		IdsStore store;
		while(iter.hasNext()) {
			store = (IdsStore)iter.next();
			store.delete();
		}
	}

	//---------------------------------------------------
	// Inhalt
	//---------------------------------------------------

	public int createVersion() {
		IIdsStore store = new IdsStore();
		content.add(store);
		return content.indexOf(store);
	}

	public int last() {
		return content.size() - 1;
	}

	public void destroy(int i) throws NoSuchElementException {
		get(i).destroy();
	}

	public int length(int i) throws NoSuchElementException {
		return get(i).length();
	}

	public long TS(int i) throws NoSuchElementException {
		return get(i).TS();
	}

	public String mimetype(int i) throws NoSuchElementException {
		return get(i).mimetype();
	}

	public void mimetype(String m, int i) throws NoSuchElementException {
		get(i).mimetype(m);
	}

	public IIdsStore get(int i) throws NoSuchElementException {
		IIdsStore store = content.get(i);

		if ( store == null )
			throw new NoSuchElementException();

		return store;
	}
}
