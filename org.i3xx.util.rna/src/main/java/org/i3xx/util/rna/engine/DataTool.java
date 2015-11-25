package org.i3xx.util.rna.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

import org.i3xx.util.rna.core.ConcurrentAccessException;
import org.i3xx.util.rna.core.IBrick;
import org.i3xx.util.rna.core.IBrickInterna;
import org.i3xx.util.rna.core.IDocument;
import org.i3xx.util.rna.core.IIdsDocument;
import org.i3xx.util.rna.core.IIdsStore;


public class DataTool {

	/**
	 * Returns an array of all indexes from IBrick.getIndex(int i)
	 * 
	 * @param brick The brick
	 * @return The index array
	 */
	public static String[] getIndex(IBrick brick) {
		
		int n = Integer.parseInt( brick.getIndex(-1) );
		String[] x = new String[n];
		for(int i=0;i<n;i++){
			x[i] = brick.getIndex(i);
			x[i] = x[i]==null?"":x[i];
		}
		return x;
	}
	
	/**
	 * Returns two arrays of long in a field of 2 containing the rights.
	 * The field at 0 is the key field of the rights map.
	 * The field at 1 is the value field of the rights map.
	 * 
	 * @param brick The brick
	 * @return The two fields
	 */
	@SuppressWarnings("unchecked")
	public static long[][] getRights(IBrick brick) {
		Map<Long, Long> m = (Map<Long, Long>)((IBrickInterna) brick).rights();
		long[] key = new long[m==null?0:m.size()];
		long[] val = new long[m==null?0:m.size()];
		if(m!=null){
			int i=0;
			for(Long k : m.keySet()){
				Long v = m.get(k);
				key[i] = k.longValue();
				val[i] = v.longValue();
				i++;
			}
		}//fi
		return new long[][]{key, val};
	}
	
	/**
	 * Returns the info of the versions of the document
	 * 
	 * @param brick
	 * @param fCrc <true> to get the crc32 hash of the document
	 */
	public static List<DocInfo> getDocumentInfo(IBrick brick, boolean fCrc) {
		List<DocInfo> list = new ArrayList<DocInfo>();
		
		IIdsDocument d = ((IDocument)brick).getDocument();
		if(d==null){
			//does nothing
		}else{
			for(int i=0;i<=d.last();i++){
				IIdsStore s = d.get(i);
				
				DocInfo info = new DocInfo(s.length(), s.mimetype(), s.TS(), s.size());
				list.add( info );
				
				if(fCrc){
					CRC32 crc = new CRC32();
					for(int ii=0;ii<s.size();ii++){
						try {
							crc.update(s.persist(ii));
						} catch (ConcurrentAccessException e) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException ee) {}
						}
					}//for
					info.crc = crc.getValue();
				}//fi
			}//for
		}//fi
		
		return list;
	}
	
	/**
	 * Sets the data record fields
	 * 
	 * @param brick The brick
	 * @param records The file records
	 */
	public static void setFileRecord(IBrick brick, String[] records) {
		for(int i=0;i<records.length;i++){
			brick.appendFileRecord(records[i]);
		}
	}
	
	/**
	 * Sets the index fields
	 * 
	 * @param brick The brick
	 * @param index The index
	 */
	public static void setIndex(IBrick brick, String[] index) {
		for(int i=0;i<index.length;i++){
			brick.setIndex(index[i], i);
		}
	}
	
	/**
	 * Sets the rights map
	 * 
	 * @param brick The brick
	 * @param key The key array
	 * @param val The value array
	 */
	@SuppressWarnings("unchecked")
	public static void setRights(IBrick brick, long[] key, long[] val) {
		Map<Long, Long> m = (Map<Long, Long>)((IBrickInterna) brick).rights();
		for(int i=0;i<key.length;i++){
			m.put(new Long(key[i]), new Long(val[i]));
		}
	}
}
