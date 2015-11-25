/**
 * 
 */
package com.i3xx.util.ctree.impl;


import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.i3xx.util.ctree.ConfNode;
import com.i3xx.util.ctree.IConfNode;
import com.i3xx.util.ctree.linker.Linker;

/**
 * @author Administrator
 *
 */
public class TNodeHandler {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
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
	 * Tests the edit functionality of the NodeHandler
	 * 
	 * @throws Exception
	 */
	@Test
	public void TestNodeHandlerIConfNodeIResolverFactory() throws Exception {
		
		IConfNode root = new ConfNode();
		NodeHandler hdl = new NodeHandler(root, new LinkableResolverFactory());
		
		hdl.addconf("test.init.param1", "Parameter-1");
		hdl.addconf("test.init.param2", "Parameter-2");
		hdl.addconf("test.init.param3", "Parameter-3");
		hdl.addconf("test.link.one", Protector.wrap("[test.init.param1]"));
		
		//The parser implements the visitor interface to walk on the tree
		NodeParser parser = new NodeParser(new LinkableResolverFactory());
		VisitorWalker<IConfNode> walker = new VisitorWalker<IConfNode>();
		walker.setRoot(root);
		walker.walk(parser);
		
		Linker linker = new Linker(root);
		linker.process();
		
		//
		//TEST LINKS FROM 'read-links.conf'
		//
		assertEquals("Parameter-1", hdl.getParam("test.link.one"));
		
		//set param to new link
		
		hdl.setParam("test.link.one", "[test.init.param2]" );
		assertEquals("[test.init.param2]", hdl.getParam("test.link.one"));
		
		//
		// This is done by hdl.setParam(String stmt) if the factory is set.
		// NodeHandler hdl = new NodeHandler(root, new LinkableResolverFactory());
		//
		//IConfNode node = root.get("test.link.one");
		//parser = new NodeParser(new LinkableResolverFactory());
		//parser.process(node);
		//linker = new Linker(node);
		//linker.process();
		
		assertEquals("[test.init.param2]", hdl.getParam("test.link.one") );
		
		hdl.setParam("test.link.one", Protector.wrap("[test.init.param2]") );
		assertEquals("Parameter-2", hdl.getParam("test.link.one") );
		
		hdl.editLink("test.link.one", 
				"test.init.param2",
				"test.init.param3");
		
		assertEquals("Parameter-3", hdl.getParam("test.link.one"));
	}

}
