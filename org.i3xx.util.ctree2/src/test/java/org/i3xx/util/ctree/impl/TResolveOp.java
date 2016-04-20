/**
 * 
 */
package org.i3xx.util.ctree.impl;

import static org.junit.Assert.assertEquals;

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


/**
 * @author Administrator
 *
 */
public class TResolveOp {
	
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
		
		hdl.addconf("test.init.param1", "Parameter-1");
		
		
		hdl.addconf("Text", "Resolved: Text");
		
		//This is a normal configuration entry
		hdl.addconf("test.f1", "Normaler Text");
		//This is a normal configuration entry
		hdl.addconf("test.f2", "Text");
		//This is a configuration entry with a link to another entry
		hdl.addconf("test.f3", Protector.wrap("Normaler [test.f2]") );
		//This is a normal configuration entry
		hdl.addconf("test.f4", "Normaler");
		//This is a configuration entry with two links to another entry
		//you can use as many as you need
		hdl.addconf("test.f5", Protector.wrap("[test.f4] [test.f2]") );
		//This is a configuration entry that copies the value from
		//'test.f5' to 'test.f8'
		hdl.addconf("test.f6", Protector.wrap("[test.f5->test.f8]") );
		//This is a configuration entry with a link to another entry
		//(uses the copy of 'test.f5' as a link
		hdl.addconf("test.f7", Protector.wrap("[test.f8]") );
		//This is a configuration entry that copies everything matching
		//'*.f5' to '*.f11'
		hdl.addconf("test.f10", Protector.wrap("[*.f5->*.f11]") );
		//This is a configuration entry with a link to another entry
		//(uses the copy of 'test.f5' as a link
		hdl.addconf("test.f12", Protector.wrap("[test.f11]") );
		
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
	
	@Test
	public void testLinks() throws IOException {
		
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
		
		hdl.addconf("test.dest.1", Protector.wrap("[test.src->*.*]") );
		hdl.addconf("test.dest.2", Protector.wrap("[test.src->*.copy]") );
		hdl.addconf("test.dest.3", Protector.wrap("[test.src->copy.*]") );
		
		//
		// You can do bad things - be careful,
		// because nobody including yourself understands your configuration.
		//
		// If you need this, make a good clearly arranged documentation.
		//
		// It is not clear what 'test.dest.4' is doing
		// It is not clear what 'test.dest.5' is doing
		// If you use 'nodo.dest.f6' in your program, where the f*** is this defined.
		//
		// 'test.dest.vararg' defines the command 'test.src->nodo.*'
		// 'test.dest.4' defines the command 'nodo.dest.f5->nodo.dest.f6'
		// 'test.dest.5' resolves 'test.dest.vararg' and then
		// 'test.dest.5' resolves 'test.src->nodo.*' what means 
		//     it copies 'test.src.f1' to 'nodo.test.f1' and so on.
		// 'test.dest.4' than resolves 'nodo.dest.f5->nodo.dest.f6'
		//     and copies 'nodo.dest.f5' to 'nodo.dest.f6' that is a
		//     copy of 'test.src.f5'
		//
		// This is a three lines play. Have much fun playing with 2387 ones
		// in a real world configuration built during a few years and spread
		// over many modules working at one of your servers and your boss
		// wants quick a little simple change today.
		//
		hdl.addconf("test.dest.vararg", Protector.wrap("[test.src->nodo.*]") );
		hdl.addconf("test.dest.4", Protector.wrap("[nodo.dest.f5->nodo.dest.f6]") );
		hdl.addconf("test.dest.5", Protector.wrap("[test.dest.vararg]") );
		
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
		assertEquals("red", hdl.getParam("test.dest.f1"));
		assertEquals("yellow", hdl.getParam("test.dest.f2"));
		assertEquals("green", hdl.getParam("test.dest.f3"));
		assertEquals("blue", hdl.getParam("test.dest.f4"));
		assertEquals("violet", hdl.getParam("test.dest.f5"));
		
		assertEquals("red", hdl.getParam("test.copy.f1"));
		assertEquals("yellow", hdl.getParam("test.copy.f2"));
		assertEquals("green", hdl.getParam("test.copy.f3"));
		assertEquals("blue", hdl.getParam("test.copy.f4"));
		assertEquals("violet", hdl.getParam("test.copy.f5"));
		
		assertEquals("red", hdl.getParam("copy.dest.f1"));
		assertEquals("yellow", hdl.getParam("copy.dest.f2"));
		assertEquals("green", hdl.getParam("copy.dest.f3"));
		assertEquals("blue", hdl.getParam("copy.dest.f4"));
		assertEquals("violet", hdl.getParam("copy.dest.f5"));
		
		//It is there.
		assertEquals("red", hdl.getParam("nodo.dest.f1"));
		assertEquals("yellow", hdl.getParam("nodo.dest.f2"));
		assertEquals("green", hdl.getParam("nodo.dest.f3"));
		assertEquals("blue", hdl.getParam("nodo.dest.f4"));
		assertEquals("violet", hdl.getParam("nodo.dest.f5"));
		//It is there too.
		assertEquals("violet", hdl.getParam("nodo.dest.f6"));
		
		//The command nodes
		assertEquals("test.src->*.*", hdl.getParam("test.dest.1"));
		assertEquals("test.src->*.copy", hdl.getParam("test.dest.2"));
		assertEquals("test.src->copy.*", hdl.getParam("test.dest.3"));
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
