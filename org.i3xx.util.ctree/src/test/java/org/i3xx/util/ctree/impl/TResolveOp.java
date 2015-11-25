/**
 * 
 */
package org.i3xx.util.ctree.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.i3xx.util.ctree.ConfNode;
import org.i3xx.util.ctree.IConfNode;
import org.i3xx.util.ctree.impl.LinkableResolverFactory;
import org.i3xx.util.ctree.impl.NodeHandler;
import org.i3xx.util.ctree.impl.NodeParser;
import org.i3xx.util.ctree.impl.VisitorWalker;
import org.i3xx.util.ctree.linker.Linker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * @author Administrator
 *
 */
public class TResolveOp {

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
		
		hdl.addconf("test.init.param1", "Parameter-1");
		
		
		hdl.addconf("Text", "Resolved: Text");
		
		hdl.addconf("test.f1", "Normaler Text");
		hdl.addconf("test.f2", "Text");
		hdl.addconf("test.f3", "Normaler [test.f2]");
		hdl.addconf("test.f4", "Normaler");
		hdl.addconf("test.f5", "[test.f4] [test.f2]");
		hdl.addconf("test.f6", "[test.f5->test.f8]");
		hdl.addconf("test.f7", "[test.f8]");
		hdl.addconf("test.f10", "[*.f5->*.f11]");
		hdl.addconf("test.f12", "[test.f11]");
		
		//
		//This doesn't work because the regexp of NodeParser doesn't
		//recognize this. A '.' in the path is necessary.
		//hdl.addconf("x.y", "[test->user]");
		//
		
		//The parser implements the visitor interface to walk on the tree
		NodeParser parser = new NodeParser(new LinkableResolverFactory());
		VisitorWalker<IConfNode> walker = new VisitorWalker<IConfNode>();
		walker.setRoot(root);
		walker.walk(parser);
		
		Linker linker = new Linker(root);
		linker.process();
		
		assertEquals("Normaler Text", hdl.getParam("test.f1"));
		assertEquals("Text", hdl.getParam("test.f2"));
		assertEquals("Normaler Text", hdl.getParam("test.f3"));
		assertEquals("Normaler", hdl.getParam("test.f4"));
		assertEquals("Normaler Text", hdl.getParam("test.f5"));
		assertEquals("test.f5->test.f8", hdl.getParam("test.f6"));
		assertEquals("Normaler Text", hdl.getParam("test.f7"));
		assertEquals("Normaler Text", hdl.getParam("test.f8"));
		assertEquals("*.f5->*.f11", hdl.getParam("test.f10"));
		assertEquals("Normaler Text", hdl.getParam("test.f11"));
		assertEquals("Normaler Text", hdl.getParam("test.f12"));
		
		//print(root);
	}
	
	/*
	private void print(IConfNode root) {
		print("test.f1", root);
		print("test.f2", root);
		print("test.f3", root);
		print("test.f4", root);
		print("test.f5", root);
		print("test.f6", root);
		print("test.f7", root);
		print("test.f8", root);
		print("test.f10", root);
		print("test.f11", root);
		print("test.f12", root);
	}
	*/
}
