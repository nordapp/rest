package org.i3xx.util.ctree.impl;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TPatternTest {

	private Pattern patternA;
	private Pattern patternB;
	
	@Before
	public void setUp() throws Exception {
		this.patternA = Pattern.compile("\uFF5B(((\\w+(\\-\\w+)*)+|\\*)(\\.((\\w+(\\-\\w+)*)+|\\*|((\\w+(\\-\\w+)*)+|\\*)\\-\\>((\\w+(\\-\\w+)*)+|\\*)))*)\uFF5D");
		this.patternB = Pattern.compile(".*\uFF5B(((\\w+(\\-\\w+)*)+|\\*)(\\.((\\w+(\\-\\w+)*)+|\\*|((\\w+(\\-\\w+)*)+|\\*)\\-\\>((\\w+(\\-\\w+)*)+|\\*)))*)\uFF5D.*");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testA() {
		Matcher m = patternB.matcher("\uFF5BWORD.WORD\uFF5D");
		assertTrue(m.matches());
	}

	@Test
	public void testB() {
		Matcher m = patternB.matcher("WORD\uFF5BWORD.WORD\uFF5DWORD");
		assertTrue(m.matches());
	}

	@Test
	public void testC() {
		Matcher m = patternB.matcher( Protector.wrap("[WORD.WORD]") );
		assertTrue(m.matches());
	}

	@Test
	public void testD() {
		Matcher m = patternB.matcher( Protector.wrap("WORD[WORD.WORD]WORD") );
		assertTrue(m.matches());
	}

	@Test
	public void testE() {
		Matcher m = patternA.matcher("\uFF5BWORD.WORD\uFF5D");
		assertTrue(m.find());
	}

	@Test
	public void testF() {
		Matcher m = patternA.matcher("WORD\uFF5BWORD.WORD\uFF5DWORD");
		assertTrue(m.find());
	}

	@Test
	public void testG() {
		Matcher m = patternA.matcher( Protector.wrap("[WORD.WORD]") );
		assertTrue(m.find());
	}

	@Test
	public void testH() {
		Matcher m = patternA.matcher( Protector.wrap("WORD[WORD.WORD]WORD") );
		assertTrue(m.find());
	}

}
