package org.i3xx.util.ramdisk;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class may not be synchronized.
 * 
 * Every access to the child list must be synchronized.
 * 
 * @author Stefan
 *
 */
public final class DirectoryResource extends AbstractFileResource {
	
	//Every access to the map must be synchronized
	private final Map<String, AbstractFileResource> list;
	
	//The state
	private boolean deleted;
	private boolean attached;
	
	/**
	 * @param dir
	 * @param name
	 */
	public DirectoryResource(AbstractFileResource parent,
			String name,
			FileType type) {
		
		super(parent, name, type);
		
		this.list = Collections.synchronizedMap(
				new LinkedHashMap<String, AbstractFileResource>());
		
		this.deleted = false;
		this.attached = false;
	}
	
	/**
	 * @return Determines whether this file names a directory
	 */
	public boolean isDirectory() {
		return type==FileType.DIRECTORY || type==FileType.ROOT;
	}
	
	/**
	 * @return Determines whether this file names regulare file
	 */
	public boolean isFile() {
		return type==FileType.FILE;
	}
	
	/**
	 * @return Retrieves the filename (no directory) of this file
	 */
	public String getName() {
		return name;
	}
	
	// --------------------------------------------------------------------------
	// Unsynchronized fields
	// --------------------------------------------------------------------------
	
	/**
	 * @return
	 */
	public byte[] getContent() {
		throw new UnsupportedOperationException("This is a directory and not a file resource.");
	}
	
	/**
	 * @param content
	 */
	public void setContent(byte[] content) {
		throw new UnsupportedOperationException("This is a directory and not a file resource.");
	}
	
	/**
	 * @return
	 */
	public String getContentType() {
		throw new UnsupportedOperationException("This is a directory and not a file resource.");
	}
	
	/**
	 * @param contentType
	 */
	public void setContentType(String contentType) {
		throw new UnsupportedOperationException("This is a directory and not a file resource.");
	}
	
	/**
	 * @return Returns true if the file is deleted.
	 */
	public boolean isDeleted() {
		return deleted;
	}
	
	/**
	 * @param deleted The delete flag
	 */
	public void setDelete(boolean deleted) {
		this.deleted = deleted;
	}
	
	/**
	 * Note: The root node is not attached.
	 * 
	 * @return The attached flag
	 */
	public boolean isAttached() {
		return attached;
	}
	
	// --------------------------------------------------------------------------
	// Methods
	// --------------------------------------------------------------------------
	
	/**
	 * Returns the child
	 * 
	 * @param name The name of the child
	 * @return
	 */
	protected AbstractFileResource getChild(String name) {
		return list.get(name);
	}
	
	/**
	 * Returns the child collection
	 * 
	 * @return
	 */
	protected Collection<AbstractFileResource> getList() {
		return new InternCollection<AbstractFileResource>();
	}
	
	/**
	 * Returns a list of the children
	 * 
	 * @return
	 */
	protected String[] list() {
		synchronized(this) {
			int i = 0;
			String[] a = new String[list.size()];
			Iterator<String> iter = list.keySet().iterator();
			while(iter.hasNext() && i<a.length) {
				a[i++] = iter.next();
			}
			return a;
		}
	}
	
	/**
	 * Attaches (appends) this file to the parent file
	 */
	protected void attach() throws IOException {
		synchronized(this) {
			parent.getList().add(this);
			attached = true;
		}//synchronized
	}
	
	/**
	 * Detaches this file from the parent file
	 */
	protected void detach() throws IOException {
		synchronized(this) {
			parent.getList().remove(this);
			attached = false;
		}//synchronized
	}
	
	/**
	 * @author Stefan
	 *
	 * @param <T>
	 */
	private final class InternCollection<T extends AbstractFileResource> extends AbstractCollection<T> {

		@Override
		public int size() {
			return list.size();
		}

		@Override
		public Iterator<T> iterator() {
			return new InternIterator<T>();
		}

		@Override
		public boolean add(T e) {
			return (list.put(e.name, e) != null);
		}
		
	}/* class */
	
	/**
	 * @author Stefan
	 *
	 * @param <T>
	 */
	private final class InternIterator<T extends AbstractFileResource> implements Iterator<T> {
		
		private final Iterator<Map.Entry<String, AbstractFileResource>> iter;
		
		private InternIterator() {
			iter = list.entrySet().iterator();
		}
		
		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@SuppressWarnings("unchecked")
		@Override
		public T next() {
			return (T)iter.next().getValue();
		}

		@Override
		public void remove() {
			iter.remove();
		}
		
	}/* class */
}
