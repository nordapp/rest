package org.i3xx.util.ctree.impl;

import java.io.File;
import java.io.IOException;

import org.i3xx.util.ctree.parser.HJsonReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HJsonTest {
	
	private static final Logger logger = LoggerFactory.getLogger(HJsonTest.class);
	
	private String confFile = null;
	
	@Before
	public void setUp() throws Exception {
		
		confFile = ClassLoader.getSystemClassLoader().getResource("HJsonTest.conf")
				.getFile().substring(1).replace('/', File.separatorChar);
		logger.info("The conf file is {}", confFile);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException {
		
		HJsonReader hjson = new HJsonReader(confFile);
		hjson.available();
		
		//fail("Not yet implemented");
	}

}
