package org.i3xx.util.ramdisk;

import static org.i3xx.util.ramdisk.AbstractFileResource.FileType.DIRECTORY;
import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileParentTest {

	@Before
	public void setUp() throws Exception {
		MountPoint.mount();
		createDirectory();
	}

	@After
	public void tearDown() throws Exception {
		MountPoint.unmount();
	}

	/**
	 * Not existing file, not existing parent
	 */
	@Test
	public void testA() {
		
		File file = new File("/DirB/DirBB1/FileA");
		File parent = file.getParent();
		
		assertEquals(parent.toString(), "DirBB1");
	}

	/**
	 * Not existing file, existing parent but not attached
	 */
	@Test
	public void testB() {
		
		File file = new File("/DirA/DirAB1/FileA");
		File parent = file.getParent();
		
		assertEquals(parent.toString(), "DirAB1");
	}

	/**
	 * Not existing file, existing parent but attached
	 */
	@Test
	public void testC() {
		
		File file = new File("/DirA/DirAB1/FileA");
		File parent = file.getParent();
		
		//attach parent
		assertTrue(parent.exists());
		
		assertEquals(parent.toString(), "/DirA/DirAB1");
	}

	/**
	 * Existing file, has always an attached parent
	 * @throws IOException 
	 */
	@Test
	public void testD() throws IOException {
		
		File file = new File("/DirA/DirAB1/FileA");
		file.create();
		File parent = file.getParent();
		
		assertEquals(parent.toString(), "/DirA/DirAB1");
	}
	
	/**
	 * @throws IOException
	 */
	public void createDirectory() throws IOException {
		
		AbstractFileResource f0 = MountPoint.getRoot();
		
		AbstractFileResource f1 = new DirectoryResource(f0, "DirA", DIRECTORY);
		f1.attach();
		
		AbstractFileResource f2 = new DirectoryResource(f1, "DirAB1", DIRECTORY);
		f2.attach();
		AbstractFileResource f3 = new DirectoryResource(f1, "DirAB2", DIRECTORY);
		f3.attach();
		AbstractFileResource f4 = new DirectoryResource(f1, "DirAB3", DIRECTORY);
		f4.attach();
		
		AbstractFileResource f5 = new DirectoryResource(f2, "DirAB1C1", DIRECTORY);
		f5.attach();
		AbstractFileResource f6 = new DirectoryResource(f2, "DirAB1C2", DIRECTORY);
		f6.attach();
		AbstractFileResource f7 = new DirectoryResource(f2, "DirAB1C3", DIRECTORY);
		f7.attach();
		AbstractFileResource f8 = new DirectoryResource(f3, "DirAB2C4", DIRECTORY);
		f8.attach();
		AbstractFileResource f9 = new DirectoryResource(f3, "DirAB2C5", DIRECTORY);
		f9.attach();
		AbstractFileResource fa = new DirectoryResource(f3, "DirAB2C6", DIRECTORY);
		fa.attach();
		AbstractFileResource fb = new DirectoryResource(f4, "DirAB3C7", DIRECTORY);
		fb.attach();
		AbstractFileResource fc = new DirectoryResource(f4, "DirAB3C8", DIRECTORY);
		fc.attach();
		
	}

}
