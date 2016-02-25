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

public class TExplanationWildcardTest {
	
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

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException {
		
		IConfNode root = new ConfNode();
		NodeHandler hdl = new NodeHandler(root, null);
		
		hdl.addconf("test.src.f1", "red");
		hdl.addconf("test.src.f2", "yellow");
		hdl.addconf("test.src.f3", "green");
		hdl.addconf("test.src.f4", "blue");
		hdl.addconf("test.src.f5", "violet");
		
		//
		// The argument '->' may not have any space inside.
		// The '*' is replaced by the part of the node's path 'test.dest'
		//
		
		// The position of '*' is related to the position in the current path.
		// In this example the result has the position 1 'node' and the position 2
		// 'dest' taken from the current path because of the asterisk '*'. The
		// position 3 is defined in the function path as 'b'.
		//
		//        current path
		//        Pos1 Pos2                                Pos1 Pos2 Pos3
		//           v    v                                   v    v v
		hdl.addconf("test.dest.1", Protector.wrap("[test.src->node.*.b]") );
		
		
		NodeParser parser = new NodeParser(new LinkableResolverFactory());
		VisitorWalker<IConfNode> walker = new VisitorWalker<IConfNode>();
		walker.setRoot(root);
		walker.walk(parser);
		
		Linker linker = new Linker(root);
		linker.process();
		
		//The origin nodes
		assertEquals("red", hdl.getParam("test.src.f1"));
		assertEquals("yellow", hdl.getParam("test.src.f2"));
		assertEquals("green", hdl.getParam("test.src.f3"));
		assertEquals("blue", hdl.getParam("test.src.f4"));
		assertEquals("violet", hdl.getParam("test.src.f5"));
		
		//The copies
		assertEquals("red", hdl.getParam("node.dest.b.f1"));
		assertEquals("yellow", hdl.getParam("node.dest.b.f2"));
		assertEquals("green", hdl.getParam("node.dest.b.f3"));
		assertEquals("blue", hdl.getParam("node.dest.b.f4"));
		assertEquals("violet", hdl.getParam("node.dest.b.f5"));

		
	}

}
