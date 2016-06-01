package org.i3xx.util.ctree.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;

import org.apache.log4j.PropertyConfigurator;
import org.i3xx.util.ctree.ConfNode;
import org.i3xx.util.ctree.IConfNode;
import org.i3xx.util.ctree.linker.Linker;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TResolveOp2 {
	
	/**
	 * @throws Exception
	 */
	@BeforeClass
	public static void beforeClass() throws Exception {
		URL url = ClassLoader.getSystemClassLoader().getResource("Log4j.properties");
		PropertyConfigurator.configure(url);
		
		Logger logger = LoggerFactory.getLogger(TResolveOp.class);
		logger.info("The logger started {}", url);
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link test.i3xx.util.ctree.ResolveOp#resolve()}.
	 * @throws IOException 
	 */
	@Test
	public void testResolve() throws IOException {
		
		/*
			Caution: Link overwrites 'test.f11' in configuration 'Normaler Text' with 'Normaler Text'.
			Test: test.f1 Normaler Text
			Test: test.f2 Text
			Test: test.f3 Normaler Text
			Test: test.f4 Normaler
			Test: test.f5 Normaler Text
			Test: test.f6 test.f5 to test.f8
			Test: test.f7 Normaler Text
			Test: test.f10 test.f5 to test.f11
			Test: test.f11 Normaler Text
			Test: test.f12 Normaler Text
			Test: user.f7 Normaler Text
		*/
		
		IConfNode root = new ConfNode();
		NodeHandler hdl = new NodeHandler(root, null);
		
		//
		// Bind the value 'Parameter-1' to the key 'test.init.param1'
		//
		hdl.addconf("test.init.param1", "Parameter-1");
		
		//
		// Copy the binding of the key 'test.init.param1' to the key 'test.init.param2'
		//
		hdl.addconf("test.copy.param1", Protector.wrap("[test.init.param1->test.init.param2]"));
		
		//
		// Resolves the argument 'test.{1}.param3' to 'test.init.param3'
		// using 'test.init.param1' where 'test' = {0}, 'init'={1}, 'param1'={2}
		//
		// and copy the binding of the key 'test.init.param1' to the key 'test.init.param3'
		//
		hdl.addconf("test.copy.param2", Protector.wrap("[test.init.param1->test.{1}.param3]"));
		
		//The parser implements the visitor interface to walk on the tree
		NodeParser parser = new NodeParser(new LinkableResolverFactory());
		VisitorWalker<IConfNode> walker = new VisitorWalker<IConfNode>();
		walker.setRoot(root);
		walker.walk(parser);
		
		Linker linker = new Linker(root);
		linker.process();
		
		assertEquals( "Parameter-1", hdl.getParam("test.init.param1") );
		assertEquals( "Parameter-1", hdl.getParam("test.init.param2") );
		assertEquals( "Parameter-1", hdl.getParam("test.init.param3") );
	}

}
