package org.i3xx.util.ramdisk;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.i3xx.util.ramdisk.AbstractFileResource.FileType.*;

public class MountPointTest {

	@Before
	public void setUp() throws Exception {
		MountPoint.mount();
	}

	@After
	public void tearDown() throws Exception {
		MountPoint.unmount();
	}

	@Test
	public void testA() {
		
		//
		// Test the split function
		//
		
		assertArrayEquals( MountPoint.split(""), new String[]{} );
		assertArrayEquals( MountPoint.split("/"), new String[]{} );
		assertArrayEquals( MountPoint.split("/Name1"), new String[]{"Name1"} );
		assertArrayEquals( MountPoint.split("/Name1/"), new String[]{"Name1"} );
		assertArrayEquals( MountPoint.split("/Name1/Name2"), new String[]{"Name1", "Name2"} );
		assertArrayEquals( MountPoint.split("/Name1/Name2/"), new String[]{"Name1", "Name2"} );
		assertArrayEquals( MountPoint.split("/Name1/Name2/Name3"), new String[]{"Name1", "Name2", "Name3"} );
		assertArrayEquals( MountPoint.split("/Name1/Name2/Name3/"), new String[]{"Name1", "Name2", "Name3"} );
		
	}

	@Test
	public void testB() throws IOException {
		
		//
		// Test the absolutePath(..) and the find(..) functions
		// f(x) ~ 1/f(y) x=absolutePath; y=find
		//
		
		AbstractFileResource f0 = MountPoint.getRoot();
		assertEquals( MountPoint.getAbsolutePath(f0), "/");
		
		AbstractFileResource f1 = new DirectoryResource(f0, "Name1", FILE);
		f1.attach();
		assertEquals( MountPoint.getAbsolutePath(f1), "/Name1");
		
		AbstractFileResource f2 = new DirectoryResource(f1, "Name2", FILE);
		f2.attach();
		assertEquals( MountPoint.getAbsolutePath(f2), "/Name1/Name2");
		
		AbstractFileResource f3 = new DirectoryResource(f2, "Name3", FILE);
		f3.attach();
		assertEquals( MountPoint.getAbsolutePath(f3), "/Name1/Name2/Name3");
		
		assertEquals( MountPoint.find(""), f0 );
		assertEquals( MountPoint.find("/"), f0 );
		assertEquals( MountPoint.find("/Name1"), f1 );
		assertEquals( MountPoint.find("/Name1/"), f1 );
		assertEquals( MountPoint.find("/Name1/Name2"), f2 );
		assertEquals( MountPoint.find("/Name1/Name2/"), f2 );
		assertEquals( MountPoint.find("/Name1/Name2/Name3"), f3 );
		assertEquals( MountPoint.find("/Name1/Name2/Name3/"), f3 );
		
	}

	@Test
	public void testC() throws IOException {
		
		//
		// Test the getBranch(..)
		//
		
		AbstractFileResource f0 = MountPoint.getRoot();
		
		AbstractFileResource f1 = new DirectoryResource(f0, "Name1", FILE);
		f1.attach();
		
		AbstractFileResource f2 = new DirectoryResource(f1, "Name2", FILE);
		f2.attach();
		
		AbstractFileResource f3 = new DirectoryResource(f2, "Name3", FILE);
		f3.attach();
		
		String[] t0 = MountPoint.split("/Test1/Test2/Test3");
		AbstractFileResource r0 = MountPoint.getBranch(t0);
		assertEquals( r0, f0 );
		
		String[] t1 = MountPoint.split("/Name1/Test1/Test2");
		AbstractFileResource r1 = MountPoint.getBranch(t1);
		assertEquals( r1, f1 );
		
		String[] t2 = MountPoint.split("/Name1/Name2/Test2");
		AbstractFileResource r2 = MountPoint.getBranch(t2);
		assertEquals( r2, f2 );
		
		String[] t3 = MountPoint.split("/Name1/Name2/Name3");
		AbstractFileResource r3 = MountPoint.getBranch(t3);
		assertEquals( r3, f3 );
	}

}
