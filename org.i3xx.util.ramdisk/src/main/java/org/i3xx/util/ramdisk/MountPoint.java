package org.i3xx.util.ramdisk;

import java.io.IOException;

import org.i3xx.util.ramdisk.AbstractFileResource.FileType;

/**
 * The class MountPoint searches the FileResources of the file objects
 * and mount paths.
 * 
 * To speed up the FileResource has a garbage collector instead of using delete.
 * 
 * @author Stefan
 *
 */
public final class MountPoint {
	
	/** The root of the file system */
	private static volatile AbstractFileResource root;
	
	/**
	 * Mounts the file system into the ram
	 * 
	 * @throws IOException
	 */
	public static final void mount() throws IOException {
		root = new DirectoryResource(null, "[Root]", FileType.ROOT);
	}
	
	/**
	 * Unmount the file system
	 */
	public static final void unmount() {
		root = null;
	}
	
	/**
	 * @return True if the file system is mounted
	 */
	public static final boolean isMounted() {
		return (root!=null);
	}
	
	/**
	 * Gets the root element
	 * 
	 * @return
	 */
	protected static final AbstractFileResource getRoot() {
		if(root==null)
			throw new IllegalStateException("The file system is not mounted.");
		
		return root;
	}
	
	/**
	 * Returns the file corresponding to the path
	 * 
	 * @param path The absolute pathname of the file
	 * @return
	 */
	protected static final AbstractFileResource find(String path) {
		if(root==null)
			throw new IllegalStateException("The file system is not mounted.");
		
		if(path.equals("/"))
			return root;
		
		return find( split(path));
	}
	
	/**
	 * Returns the file on the given pathname or null. If
	 * the array is empty (length=0) the root is returned.
	 * 
	 * @param names
	 * @return
	 */
	protected static final AbstractFileResource find(String[] names) {
		if(root==null)
			throw new IllegalStateException("The file system is not mounted.");
		
		AbstractFileResource current = root;
		for(int i=0;i<names.length;i++){
			current = current.getChild(names[i]);
			if(current==null)
				return null;
		}
		return current;
	}
	
	/**
	 * Returns the latest existing file by name. If
	 * the array is empty (length=0) the root is returned.
	 * 
	 * @param names
	 * @return
	 */
	protected static final AbstractFileResource getBranch(String[] names) {
		if(root==null)
			throw new IllegalStateException("The file system is not mounted.");
		
		AbstractFileResource current = root;
		for(int i=0;i<names.length;i++){
			AbstractFileResource c = current.getChild(names[i]);
			if(c==null)
				return current;
			else
				current = c;
		}
		return current;
	}
	
	protected static final void attach(AbstractFileResource resource) {
		
	}
	
	protected static final void detach(AbstractFileResource resource) {
		
	}
	
	/**
	 * @param path
	 * @return
	 */
	protected static final String[] split(String path) {
		//leading '/' results in an empty element at 0 (array[0])
		if(path.startsWith(FileResource.separator))
			path = path.substring(1);
		//trailing '/' results in an empty element at n (array[n]); n = length-1
		if(path.endsWith(FileResource.separator))
			path = path.substring(0, path.length()-1);
		
		if(path.equals(""))
			return new String[]{};
		
		return path.split("\\"+FileResource.separator);
	}
	
	/**
	 * @param names Copies the path names of the names array
	 * @return
	 */
	protected static final String[] copyPath(String[] names) {
		String[] path = new String[names.length-1];
		System.arraycopy(names, 0, path, 0, names.length-1);
		
		return path;
	}
	
	/**
	 * @return
	 */
	protected static final String getAbsolutePath(AbstractFileResource resource) {
		if(root==null)
			throw new IllegalStateException("The file system is not mounted.");
		
		StringBuffer buffer = new StringBuffer();
		
		for(int i=0;resource!=null && resource!=root;i++) {
			if(i!=0) {
				buffer.insert(0, FileResource.separatorChar);
			}//fi
			buffer.insert( 0, resource.getName() );
			resource = resource.getParent();
		}//for
		buffer.insert( 0, FileResource.separatorChar);
		
		return buffer.toString();
	}
	
	/**
	 * Searches the name of a resource in an array of names.
	 * 
	 * @param resource The resource to search in the names array
	 * @param names The index of the resource in the array or -1 if not found.
	 * @return
	 */
	protected static final int getIndexOf(AbstractFileResource resource, String[] names) {
		int index = -1;
		for(int i=0;i<names.length;i++) {
			if(names[i].equals(resource.getName()))
				return i;
		}
		
		return index;
	}
}
