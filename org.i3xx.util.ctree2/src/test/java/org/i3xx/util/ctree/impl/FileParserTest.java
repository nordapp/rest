package org.i3xx.util.ctree.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.i3xx.util.ctree.ConfNode;
import org.i3xx.util.ctree.IConfNode;
import org.i3xx.util.ctree.TreeBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileParserTest {
	
	private static final Logger logger = LoggerFactory.getLogger(FileParserTest.class);
	
	private static IConfNode root = new ConfNode();
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		URL url = ClassLoader.getSystemClassLoader().getResource("Log4j.properties");
		PropertyConfigurator.configure(url);
		logger.info("The logger started {}", url);
		
		String confFile = ClassLoader.getSystemClassLoader().getResource("ParserTest.conf")
				.getFile().substring(1).replace('/', File.separatorChar);
		logger.info("The conf file is {}", confFile);
		
		Properties props = new Properties();
		props.put("main.system.home", confFile);
		
		TreeBuilder.doParse(root, new File(confFile), props);
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPlain() throws IOException {
		
		assertEquals( root.get("main.system.name.test-1").value(), "MyTest" );
		assertEquals( root.get("main.system.name.test-2").value(), "MyTest" );
		assertEquals( root.get("main.system.name.test-3").value(), "MyTest" );
		assertEquals( root.get("main.system.name.test-4").value(), "MyTest" );
		assertEquals( root.get("main.system.name.test-5").value(), "MyTest" );
		
	}

	@Test
	public void testHjson() throws IOException {
		
		/*assertEquals( root.get("main.system.json.test-1").value(), "MyTest" );
		assertEquals( root.get("main.system.json.test-2").value(), "MyTest" );
		assertEquals( root.get("main.system.json.test-3").value(), "MyTest" );
		assertEquals( root.get("main.system.json.test-4").value(), "MyTest" );
		assertEquals( root.get("main.system.json.test-5").value(), "MyTest" );
		*/
	}

}
