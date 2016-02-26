package org.i3xx.util.ctree.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;

import org.apache.log4j.PropertyConfigurator;
import org.i3xx.util.ctree.ConfNode;
import org.i3xx.util.ctree.IConfNode;
import org.i3xx.util.ctree.linker.Linker;
import org.i3xx.util.ctree.parser.LineReader;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
	public void testA() throws IOException {
		
		IConfNode root = new ConfNode();
		NodeHandler hdl = new NodeHandler(root, null);
		
		hdl.addconf("test.src", "colors");
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
		assertEquals("colors", hdl.getParam("test.src"));
		assertEquals("red", hdl.getParam("test.src.f1"));
		assertEquals("yellow", hdl.getParam("test.src.f2"));
		assertEquals("green", hdl.getParam("test.src.f3"));
		assertEquals("blue", hdl.getParam("test.src.f4"));
		assertEquals("violet", hdl.getParam("test.src.f5"));
		
		//The copies
		assertEquals("colors", hdl.getParam("node.dest.b"));
		assertEquals("red", hdl.getParam("node.dest.b.f1"));
		assertEquals("yellow", hdl.getParam("node.dest.b.f2"));
		assertEquals("green", hdl.getParam("node.dest.b.f3"));
		assertEquals("blue", hdl.getParam("node.dest.b.f4"));
		assertEquals("violet", hdl.getParam("node.dest.b.f5"));
	}

	@Ignore
	public void testB() throws IOException {
		
		IConfNode root = new ConfNode();
		NodeHandler hdl = new NodeHandler(root, null);
		
		StringBuffer buf = new StringBuffer();
		buf.append("test.src.f1 red").append('\n');
		buf.append("test.src.f2 yellow").append('\n');
		buf.append("test.src.f3 green").append('\n');
		buf.append("test.src.f4 blue").append('\n');
		buf.append("test.src.f5 violet").append('\n');
		buf.append("test.dest.1 [test.src->node.*.b]").append('\n');
		
		LineNumberReader in = new LineNumberReader(new StringReader(buf.toString()));
		
		DefaultParser defParse = new DefaultParser("");
		defParse.setRules(root);
		
		LineReader reader = new LineReader(in);
		reader.setParams(new HashMap<String, String>());
		reader.getParams().put("filename", "<file>");
		reader.getParams().put("mimetype", "text/plain");
		reader.getParams().put("datatype", "line");
		defParse.process(reader);
		reader.close();
		
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

	@Ignore
	public void testC() throws IOException {
		
		IConfNode root = new ConfNode();
		NodeHandler hdl = new NodeHandler(root, null);
		
		hdl.addconf("test.src.f1", "red");
		hdl.addconf("test.src.f2", "yellow");
		hdl.addconf("test.src.f3", "green");
		hdl.addconf("test.src.f4", "blue");
		hdl.addconf("test.src.f5", "violet");
		
		//
		//
		//
		
		hdl.addconf("temp.1", Protector.wrap("[test->copy]") );
		
		
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
		assertEquals("red", hdl.getParam("copy.src.f1"));
		assertEquals("yellow", hdl.getParam("copy.src.f2"));
		assertEquals("green", hdl.getParam("copy.src.f3"));
		assertEquals("blue", hdl.getParam("copy.src.f4"));
		assertEquals("violet", hdl.getParam("copy.src.f5"));
	}

}
