package org.i3xx.util.ctree.impl;

import static org.junit.Assert.*;

import java.io.IOException;

import org.i3xx.util.ctree.ConfNode;
import org.i3xx.util.ctree.IConfNode;
import org.i3xx.util.ctree.TreeBuilder;
import org.i3xx.util.ctree.core.ResolverException;
import org.i3xx.util.ctree.linker.Linker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TFinalTreeTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException, ResolverException {
		
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
		
		IConfNode node = TreeBuilder.doFinal(root);
		
		//The origin nodes
		assertEquals("red", node.get("test.src.f1").rawValue());
		assertEquals("yellow", node.get("test.src.f2").rawValue());
		assertEquals("green", node.get("test.src.f3").rawValue());
		assertEquals("blue", node.get("test.src.f4").rawValue());
		assertEquals("violet", node.get("test.src.f5").rawValue());
		
		//The copies
		assertEquals("red", node.get("test.dest.f1").rawValue());
		assertEquals("yellow", node.get("test.dest.f2").rawValue());
		assertEquals("green", node.get("test.dest.f3").rawValue());
		assertEquals("blue", node.get("test.dest.f4").rawValue());
		assertEquals("violet", node.get("test.dest.f5").rawValue());
		
		assertEquals("red", node.get("test.copy.f1").rawValue());
		assertEquals("yellow", node.get("test.copy.f2").rawValue());
		assertEquals("green", node.get("test.copy.f3").rawValue());
		assertEquals("blue", node.get("test.copy.f4").rawValue());
		assertEquals("violet", node.get("test.copy.f5").rawValue());
		
		assertEquals("red", node.get("copy.dest.f1").rawValue());
		assertEquals("yellow", node.get("copy.dest.f2").rawValue());
		assertEquals("green", node.get("copy.dest.f3").rawValue());
		assertEquals("blue", node.get("copy.dest.f4").rawValue());
		assertEquals("violet", node.get("copy.dest.f5").rawValue());
		
		//It is there.
		assertEquals("red", node.get("nodo.dest.f1").rawValue());
		assertEquals("yellow", node.get("nodo.dest.f2").rawValue());
		assertEquals("green", node.get("nodo.dest.f3").rawValue());
		assertEquals("blue", node.get("nodo.dest.f4").rawValue());
		assertEquals("violet", node.get("nodo.dest.f5").rawValue());
		//It is there too.
		assertEquals("violet", node.get("nodo.dest.f6").rawValue());
		
		//The command nodes
		//The values and the raw values of the command nodes are different
		//because they use the EscapeResolver.
		assertEquals("test.src->*.*", node.get("test.dest.1").value());
		assertEquals("test.src->*.copy", node.get("test.dest.2").value());
		assertEquals("test.src->copy.*", node.get("test.dest.3").value());
		
		assertNotEquals("test.src->*.*", node.get("test.dest.1").rawValue());
		assertNotEquals("test.src->*.copy", node.get("test.dest.2").rawValue());
		assertNotEquals("test.src->copy.*", node.get("test.dest.3").rawValue());
		
		assertEquals(root.get("test.dest.1").rawValue(), node.get("test.dest.1").rawValue());
		assertEquals(root.get("test.dest.2").rawValue(), node.get("test.dest.2").rawValue());
		assertEquals(root.get("test.dest.3").rawValue(), node.get("test.dest.3").rawValue());
		
	}
	
}
