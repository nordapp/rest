package org.i3xx.util.ramdisk;

import java.io.IOException;
import java.util.Collection;

/**
 * This class may not be synchronized.
 * 
 * Every access to the child list must be synchronized.
 * 
 * @author Stefan
 *
 */
public final class FileResource extends AbstractFileResource {
	
	//The content
	private byte[] content;
	private String contentType;
	
	//The state
	private boolean deleted;
	private boolean attached;
	
	/**
	 * @param dir
	 * @param name
	 */
	public FileResource(AbstractFileResource parent,
			String name) {
		
		super(parent, name, FileType.FILE);
		
		this.content = null;
		this.contentType = null;
		
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
		return content;
	}
	
	/**
	 * @param content
	 */
	public void setContent(byte[] content) {
		this.content = content;
	}
	
	/**
	 * @return
	 */
	public String getContentType() {
		return contentType;
	}
	
	/**
	 * @param contentType
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
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
	protected FileResource getChild(String name) {
		throw new UnsupportedOperationException("This is a file and not a directory resource.");
	}
	
	/**
	 * Returns the child collection
	 * 
	 * @return
	 */
	protected Collection<AbstractFileResource> getList() {
		throw new UnsupportedOperationException("This is a file and not a directory resource.");
	}
	
	/**
	 * Returns a list of the children
	 * 
	 * @return
	 */
	protected String[] list() {
		return null;
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
}
