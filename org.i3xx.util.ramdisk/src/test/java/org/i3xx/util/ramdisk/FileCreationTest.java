package org.i3xx.util.ramdisk;

import static org.i3xx.util.ramdisk.AbstractFileResource.FileType.DIRECTORY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileCreationTest {

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
	 * create()
	 * 
	 * @throws IOException
	 */
	@Test
	public void testA() throws IOException {
		
		File file = new File("/DirA/DirAB1/FileA");
		
		assertEquals(file.toString(), "FileA");
		assertFalse(file.exists());
		
		file.create();
		
		assertTrue(file.exists());
		assertEquals(file.toString(), "/DirA/DirAB1/FileA");
	}

	/**
	 * create()
	 * mkdir()
	 * 
	 * @throws IOException
	 */
	@Test
	public void testB() throws IOException {
		
		File file = new File("/DirA/DirAB4/FileA");
		
		assertEquals(file.toString(), "FileA");
		assertFalse(file.exists());
		
		try{
			file.create();
			fail("create must throw the IOException here.");
		}catch(IOException e){};
		
		file.getParent().mkdir();
		file.create();
		
		assertTrue(file.exists());
		assertEquals(file.toString(), "/DirA/DirAB4/FileA");
	}

	/**
	 * mkdir()
	 * mkdirs()
	 * 
	 * @throws IOException
	 */
	@Test
	public void testC() throws IOException {
		
		File file = new File("/DirB/DirBB1/FileA");
		
		assertEquals(file.toString(), "FileA");
		assertFalse(file.exists());
		
		try{
			file.create();
			fail("create must throw the IOException here.");
		}catch(IOException e){};
		
		try{
			file.getParent().mkdir();
			fail("mkdir must throw the IOException here.");
		}catch(IOException e){};
		
		file.getParent().mkdirs();
		file.create();
		
		assertTrue(file.exists());
		assertEquals(file.toString(), "/DirB/DirBB1/FileA");
	}
	
	/**
	 * listFiles()
	 * 
	 * @throws IOException
	 */
	@Test
	public void testD() throws IOException {
		
		File file = null;
		
		file = new File("/DirB/DirBB1");
		file.mkdirs();
		
		file = new File("/DirB/DirBB1/FileB");
		file.create();
		file = new File("/DirB/DirBB1/FileC");
		file.create();
		file = new File("/DirB/DirBB1/FileD");
		file.create();
		file = new File("/DirB/DirBB1/FileE");
		file.create();
		
		file = new File("/DirB/DirBB1");
		File[] files = file.listFiles();
		
		assertEquals(files[0].getAbsolutePath(), "/DirB/DirBB1/FileB");
		assertEquals(files[1].getAbsolutePath(), "/DirB/DirBB1/FileC");
		assertEquals(files[2].getAbsolutePath(), "/DirB/DirBB1/FileD");
		assertEquals(files[3].getAbsolutePath(), "/DirB/DirBB1/FileE");
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
