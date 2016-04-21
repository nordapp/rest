package org.i3xx.util.hateoas;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LinkHeaderTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testA() {
		
		LinkHeader.Link link = new LinkImpl();
		link.setCharset("utf-8");
		link.setLinkClass("test123");
		link.setMimetype("text/plain");
		link.setParam("myKey1", "myVal1");
		link.setRelation("self");
		link.setResource("http://test456.com");
		link.setVerb("GET");
		
		assertEquals( link.toString(),
				"<http://test456.com>; rel=\"self\"; " +
				"class=\"test123\"; verb=\"GET\"; " +
				"type=\"text/plain;utf-8\"; myKey1=\"myVal1\"" );
	}

	@Test
	public void testB() {
		
		LinkHeader.Link linkA = new LinkImpl();
		linkA.setRelation("self");
		linkA.setResource("http://test456.com/self");
		
		LinkHeader.Link linkB = new LinkImpl();
		linkB.setRelation("next");
		linkB.setResource("http://test456.com/next");
		
		List<LinkHeader.Link> list = new ArrayList<LinkHeader.Link>();
		list.add(linkA);
		list.add(linkB);
		String hdr = LinkHeader.getLinkHeader(list);
		
		list = LinkHeader.parseLinkHeader(hdr);
		
		assertEquals(list.get(0).toString(), "<http://test456.com/self>; rel=\"self\"");
		assertEquals(list.get(1).toString(), "<http://test456.com/next>; rel=\"next\"");
	}

}
