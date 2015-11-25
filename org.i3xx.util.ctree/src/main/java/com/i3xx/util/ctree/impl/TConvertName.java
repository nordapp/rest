package com.i3xx.util.ctree.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class TConvertName {

	@Test
	public void test() {
		
		assertEquals( ConvertName.toCamelCase("mein-test-name"), "meinTestName");
		assertEquals( ConvertName.toCamelCase("mein-Test-name"), "meinTestName");
		assertEquals( ConvertName.toCamelCase("meinTest-name"), "meinTestName");
	}
}
