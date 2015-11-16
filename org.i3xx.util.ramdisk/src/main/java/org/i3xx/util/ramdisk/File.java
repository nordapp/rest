package org.i3xx.util.ramdisk;

import java.io.IOException;

import org.i3xx.util.ramdisk.AbstractFileResource.FileType;

/**
 * The content of this class should not be synchronized.
 * Use a snapshot of the binary data before reading and
 * replace the whole data after writing.
 * 
 * @author Stefan
 *
 */
public final class File {
	
	private final String path;
	private AbstractFileResource resource;
	
	/**
	 * Used to create the root node
	 */
	public File(String path) {
		this.path = path.replaceAll("\\/+", "/");
		this.resource = null;
	}
	
	/**
	 * @param resource
	 */
	protected File(AbstractFileResource resource) {
		this.path = resource.getAbsolutePath();
		this.resource = resource;
	}
	
	/**
	 * @return
	 */
	public byte[] getContent() {
		ensureResource();
		return resource==null ? null : resource.getContent();
	}
	
	/**
	 * @param content
	 */
	public void setContent(byte[] content) {
		ensureResource();
		resource.setContent(content);
	}
	
	/**
	 * @return
	 */
	public String getContentType() {
		ensureResource();
		return resource==null ? null : resource.getContentType();
	}
	
	/**
	 * @param contentType
	 */
	public void setContentType(String contentType) {
		ensureResource();
		resource.setContentType(contentType);
	}
	
	/**
	 * @return Determines whether this file names a directory
	 */
	public boolean isDirectory() {
		ensureResource();
		return resource==null ? false : resource.isDirectory();
	}
	
	/**
	 * @return Determines whether this file names regulare file
	 */
	public boolean isFile() {
		ensureResource();
		return resource==null ? true : resource.isFile();
	}
	
	/**
	 * Creates a file resource 'FILE' with the pathname of this file
	 * 
	 * @throws IOException
	 */
	public void create() throws IOException {
		String[] names = MountPoint.split(path);
		AbstractFileResource parent = MountPoint.getBranch(names);
		int i = MountPoint.getIndexOf(parent, names);
		
		if(i==names.length-2) {
			synchronized(this) {
				FileResource current = new FileResource(parent, names[names.length-1]);
				current.attach();
				resource = current;
			}
		}
		else if(i==names.length-1){
			throw new IOException("The file '"+
					names[names.length-1]+"' already exists.");
		}
		else if(i==-1){
			throw new IOException("The parent "+names[0]+" directory doesn't exist.");
		}
		else{
			throw new IOException("The parent "+names[i+1]+" directory doesn't exist.");
		}//fi
	}
	
	/**
	 * Creates a file resource 'DIRECTORY' with the pathname of this file
	 * 
	 * @throws IOException 
	 */
	public void mkdir() throws IOException {
		String[] names = MountPoint.split(path);
		AbstractFileResource parent = MountPoint.getBranch(names);
		int i = MountPoint.getIndexOf(parent, names);
		
		if(i==names.length-2) {
			synchronized(this) {
				DirectoryResource current = new DirectoryResource(parent, names[names.length-1], FileType.DIRECTORY);
				current.attach();
				resource = current;
			}
		}
		else if(i==names.length-1){
			throw new IOException("The file '"+
					names[names.length-1]+"' already exists.");
		}
		else if(i==-1){
			throw new IOException("The parent "+names[0]+" directory doesn't exist.");
		}
		else{
			throw new IOException("The parent "+names[i+1]+" directory doesn't exist.");
		}//fi
	}
	
	/**
	 * Creates all directories with the pathname of this file
	 * @throws IOException 
	 */
	public void mkdirs() throws IOException {
		String[] names = MountPoint.split(path);
		AbstractFileResource parent = MountPoint.getBranch(names);
		int i = MountPoint.getIndexOf(parent, names);
		
		if(i==names.length-2) {
			synchronized(this) {
				DirectoryResource current = new DirectoryResource(parent, names[names.length-1], FileType.DIRECTORY);
				current.attach();
				resource = current;
			}
		}
		else if(i==names.length-1){
			//does nothing
		}
		if(i==-1) {
			parent = MountPoint.getRoot();
			synchronized(this) {
				for(i=0;i<names.length;i++) {
					DirectoryResource current = new DirectoryResource(parent, names[i], FileType.DIRECTORY);
					current.attach();
					parent = current;
				}//for
			}
			resource = parent;
		}//fi
		else{
			synchronized(this) {
				for(i++;i<names.length;i++) {
					DirectoryResource current = new DirectoryResource(parent, names[i], FileType.DIRECTORY);
					current.attach();
					parent = current;
				}//for
			}
			resource = parent;
		}//fi
	}
	
	/**
	 * @param file
	 * @throws IOException 
	 */
	public void renameTo(File file) throws IOException {
		ensureResource();
		//TODO:
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Deletes this file or directory
	 * @throws IOException 
	 */
	public void delete() throws IOException {
		//
		// The GC does the deletion
		//
		ensureResource();
		if(resource!=null) {
			resource.detach();
			resource.setDelete(true);
		}
		resource = null;
	}
	
	/**
	 * @return Determines whether this file exists
	 */
	public boolean exists() {
		ensureResource();
		return resource != null && ( resource.isAttached() || resource.isRoot() );
	}
	
	/**
	 * @return Retrieves the filename (no directory) of this file
	 */
	public String getName() {
		if(resource==null) {
			String[] names = MountPoint.split(path);
			return names[names.length-1];
		}
		else
			return resource.getName();
	}
	
	/**
	 * Returns the parent of the file
	 * 
	 * @return
	 */
	public File getParent() {
		ensureResource();
		
		if(resource==null) {
			String[] names = MountPoint.split(path);
			return new File( toString(names, 0, names.length-1) );
		}else if(resource.isAttached()){
			return new File(resource.parent);
		}else{
			String[] names = MountPoint.split(path);
			return new File( toString(names, 0, names.length-1) );
		}//fi
	}
	
	/**
	 * @return Returns the filenames of the children or null
	 */
	public String[] list() {
		ensureResource();
		if(resource==null)
			return null;
		
		return resource.list();
	}
	
	/**
	 * @return Returns the files of the children or null
	 */
	public File[] listFiles() {
		ensureResource();
		if(resource==null)
			return null;
		
		String[] names = resource.list();
		if(names==null)
			return null;
		
		String path = resource.getAbsolutePath();
		File[] files = new File[names.length];
		
		for(int i=0;i<names.length;i++) {
			File file = new File(path+"/"+names[i]);
			file.ensureResource();
			files[i] = file;
		}
		
		return files;
	}
	
	/**
	 * @return Generates the absolute pathname of this file
	 */
	public String getAbsolutePath() {
		return resource==null ? getName() : resource.getAbsolutePath();
	}
	
	/**
	 * Searches the resource and attaches the file
	 */
	private void ensureResource() {
		if(resource!=null) {
			return;
		}
		
		resource = MountPoint.find(path);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getAbsolutePath();
	}
	
	/**
	 * @param names The path array of names
	 * @param off The offset
	 * @param len The length
	 * @return
	 */
	public String toString(String[] names, int off, int len) {
		StringBuffer buf = new StringBuffer();
		if((off+len)>names.length)
			throw new ArrayIndexOutOfBoundsException("The offset "+off+
					" and the length "+len+" doesn't match the array size of "+names.length);
		
		for(int i=off;i<(off+len);i++) {
			buf.append('/');
			buf.append(names[i]);
		}
		if(buf.length()==0)
			buf.append('/');
		
		return buf.toString();
	}
}
