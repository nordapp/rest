package org.i3xx.util.context.impl;

import static org.junit.Assert.assertEquals;

import org.i3xx.util.context.model.IContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SimpleContextTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		// A test with a String with the length 15
		
		IContext<String, String> c = new SimpleContext<String, String>("Test");
		c.put("my.test.1", "This is a test.");
		c.put("my.test.2", "This is a test.");
		c.put("my.test.3", "This is a test.");
		c.put("my.test.4", "This is a test.");
		c.put("my.test.5", "This is a test.");
		c.put("my.test.6", "This is a test.");
		c.put("my.test.7", "This is a test.");
		c.put("my.test.8", "This is a test.");
		c.put("my.test.9", "This is a test.");
		c.put("my.test.0", "This is a test.");
		
		int t = 0;
		for(int i=0; i<1000000; i++) {
			String s = new SimpleContext<String, String>("Test").get("my.test.3");
			t += s.length();
		}
		
		// ~31 millis
		assertEquals( 15000000, t );
	}

}
