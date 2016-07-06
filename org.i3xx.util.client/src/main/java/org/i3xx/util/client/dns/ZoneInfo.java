package org.i3xx.util.client.dns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.ZoneTransferException;
import org.xbill.DNS.ZoneTransferIn;

public class ZoneInfo {
	
	/**  */
	private final Record[] zone;

	/**
	 * @param host
	 * @param zone
	 * @throws IOException
	 * @throws TextParseException
	 * @throws ZoneTransferException
	 */
	public ZoneInfo(String host, String zone) throws IOException, TextParseException, ZoneTransferException {
		List<Record> list = new ArrayList<Record>();
		
		ZoneTransferIn xfr = ZoneTransferIn.newAXFR(new Name(zone), host, null);
		List<?> lrecords = xfr.run();
		Iterator<?> it = lrecords.iterator();
		while ( it.hasNext() ) {
			list.add( (Record)it.next() );
		}//
		
		this.zone = list.toArray(new Record[list.size()]);
	}
	
	/**
	 * @return
	 */
	public Record[] getZone() {
		return zone;
	}
}
