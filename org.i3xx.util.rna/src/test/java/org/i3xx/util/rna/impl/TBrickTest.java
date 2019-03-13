package org.i3xx.util.rna.impl;

import org.i3xx.util.rna.core.IBrick;
import org.i3xx.util.rna.engine.server.CIdentifier;
import org.junit.After;
import org.junit.Test;

public class TBrickTest {

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		CIdentifier.ID(0x0);
		CIdentifier.IdMask(0xFFFFFFFF);
		CIdentifier.SubID(1);
		CIdentifier.SystemID(1);
		
		CIdentifier.CidMask(0x000010000L, 0x0000FFFF); //65536, 65535
		CIdentifier.TidMask(0x001000000L, 0x00FFFFFF); //16777216, 16777215
		CIdentifier.SidMask(0x200000000L, 0xFFFFFFFF); //
		
		IBrick brick = new CDocument("test", "This is a test", 0L);
		
		System.out.println( brick.ID() );
		
		//fail("Not yet implemented");
	}

}
