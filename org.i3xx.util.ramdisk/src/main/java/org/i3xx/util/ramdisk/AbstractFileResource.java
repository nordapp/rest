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
public abstract class AbstractFileResource {
	
	/**
	 * The types of the file resource
	 * @author Stefan
	 */
	public enum FileType {
		ROOT,
		DIRECTORY,
		FILE
	}
	
	public static final String separator = "/";
	public static final char separatorChar = '/';
	
	//The structure
	protected final String name;
	protected final AbstractFileResource parent;
	protected final FileType type;
	
	/**
	 * @param dir
	 * @param name
	 */
	public AbstractFileResource(AbstractFileResource parent,
			String name,
			FileType type) {
		
		this.name = name;
		this.parent = parent;
		this.type = type;
	}
	
	/**
	 * @return Determines whether this file names a directory
	 */
	public boolean isDirectory() {
		return type==FileType.DIRECTORY || type==FileType.ROOT;
	}
	
	/**
	 * @return Determines whether this file names a regular file
	 */
	public boolean isFile() {
		return type==FileType.FILE;
	}
	
	/**
	 * @return true If the file is root.
	 */
	public boolean isRoot() {
		return type==FileType.ROOT;
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
	public abstract byte[] getContent();
	
	/**
	 * @param content
	 */
	public abstract void setContent(byte[] content);
	
	/**
	 * @return
	 */
	public abstract String getContentType();
	
	/**
	 * @param contentType
	 */
	public abstract void setContentType(String contentType);
	
	/**
	 * @return Returns true if the file is deleted.
	 */
	public abstract boolean isDeleted();
	
	/**
	 * @param deleted The delete flag
	 */
	public abstract void setDelete(boolean deleted);
	
	/**
	 * @return The attached flag
	 */
	public abstract boolean isAttached();
	
	
	// --------------------------------------------------------------------------
	// Methods
	// --------------------------------------------------------------------------
	
	/**
	 * Returns the parent
	 * 
	 * @return
	 */
	public AbstractFileResource getParent() {
		return parent;
	}
	
	/**
	 * Returns the child
	 * 
	 * @param name The name of the child
	 * @return
	 */
	protected abstract AbstractFileResource getChild(String name);
	
	/**
	 * Returns the child collection
	 * 
	 * @return
	 */
	protected abstract Collection<AbstractFileResource> getList();
	
	/**
	 * Returns a list of the children
	 * 
	 * @return
	 */
	protected abstract String[] list();
	
	/**
	 * Returns the absolute path
	 * 
	 * @return The path
	 */
	protected String getAbsolutePath() {
		return MountPoint.getAbsolutePath(this);
	}
	
	/**
	 * Attaches (appends) this file to the parent file
	 */
	protected abstract void attach() throws IOException;
	
	/**
	 * Detaches this file from the parent file
	 */
	protected abstract void detach() throws IOException;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getAbsolutePath()+" ["+type+"]";
	}
	
}
