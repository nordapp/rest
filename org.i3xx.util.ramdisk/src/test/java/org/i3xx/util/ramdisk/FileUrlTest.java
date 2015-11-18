package org.i3xx.util.ramdisk;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileUrlTest {

	@Before
	public void setUp() throws Exception {
		//
		// Put the handler Handler to the directory given to the property
		// 'java.protocol.handler.pkgs' in a subdirectory called 'ramdisk'.
		// In this test it is 'org.i3xx.util.ramdisk.ramdisk'. After then
		// you can use the protocol 'ramdisk:' as shown above.
		//
		System.setProperty( "java.protocol.handler.pkgs", "org.i3xx.util.ramdisk" );
		
		MountPoint.mount();
	}

	@After
	public void tearDown() throws Exception {
		MountPoint.unmount();
	}

	@Test
	public void test() throws IOException {
		
		final String content = "This is the content.";
		
		File file = new File("/test/url/MyFile");
		file.getParent().mkdirs();
		file.create();
		file.setContent(content.getBytes());
		file.setContentType("text/plain");
		
		file = new File("/test/url/MyFile");
		assertTrue( file.exists() );
		
		URL url = new URL("ramdisk://localhost/test/url/MyFile");
		int c = 0;
		InputStream is = url.openStream();
		OutputStream os = new ByteArrayOutputStream();
		while((c=is.read())>-1)
			os.write(c);
		
		assertEquals(content, os.toString());
	}

}
