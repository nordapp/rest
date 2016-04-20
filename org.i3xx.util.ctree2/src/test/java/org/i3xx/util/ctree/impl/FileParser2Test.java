package org.i3xx.util.ctree.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.PropertyConfigurator;
import org.i3xx.util.ctree.ConfNode;
import org.i3xx.util.ctree.IConfNode;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileParser2Test {
	
	private static final Logger logger = LoggerFactory.getLogger(FileParser2Test.class);
	
	private static IConfNode root = new ConfNode();
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		URL url = ClassLoader.getSystemClassLoader().getResource("Log4j.properties");
		PropertyConfigurator.configure(url);
		logger.info("The logger started {}", url);
		
		String confFile = ClassLoader.getSystemClassLoader().getResource("Parser2Test.conf")
				.getFile().substring(1).replace('/', File.separatorChar);
		logger.info("The conf file is {}", confFile);
		
		DefaultParser parser = new DefaultParser(confFile);
		parser.setRules(root);
		parser.process();
		
		//sets the system home
		root.create("main.system.home").value(confFile);
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHjson() throws IOException {
		
		assertEquals( root.get("main.system.json.test-1").value(), "MyTest" );
		assertEquals( root.get("main.system.json.test-2").value(), "MyTest" );
		assertEquals( root.get("main.system.json.test-3").value(), "MyTest" );
		assertEquals( root.get("main.system.json.test-4").value(), "MyTest" );
		assertEquals( root.get("main.system.json.test-5").value(), "MyTest" );
		
	}

}
