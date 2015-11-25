package org.i3xx.util.rna.engine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.CRC32;

public class CrcWriter implements IDataWriter {

	private CRC32 crc;
	private ByteArrayOutputStream out;
	private ObjectOutputStream source;
	
	public CrcWriter(CRC32 crc) throws IOException {
		this.crc = crc;
		out = new ByteArrayOutputStream();
		source = new ObjectOutputStream( out );
	}
	
	private void update() throws IOException {
		source.flush();
		
		byte[] buf = out.toByteArray();
		crc.update(buf);
		
		source.reset();
		out.reset();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.engine.rna.IDataWriter#write(java.lang.String[])
	 */
	public void write(String field, String[] value) throws IOException {
		if(value==null)
			value = new String[0];
		
		source.writeObject(value);
		update();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.engine.rna.IDataWriter#write(java.lang.String, long[])
	 */
	public void write(String field, long[] value) throws IOException {
		if(value==null)
			value = new long[0];
		
		source.writeObject(value);
		update();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.engine.rna.IDataWriter#write(java.lang.String, int[])
	 */
	public void write(String field, int[] value) throws IOException {
		if(value==null)
			value = new int[0];
		
		source.writeObject(value);
		update();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.engine.rna.IDataWriter#write(java.lang.String, double[])
	 */
	public void write(String field, double[] value) throws IOException {
		if(value==null)
			value = new double[0];
		
		source.writeObject(value);
		update();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.engine.rna.IDataWriter#write(byte[])
	 */
	public void write(String field, byte[] value) throws IOException {
		if(value==null)
			value = new byte[0];
		
		source.writeObject(value);
		update();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.engine.rna.IDataWriter#write(java.lang.Object)
	 */
	public void write(String field, Object value) throws IOException {
		if(value==null)
			value = new Object();
		
		source.writeObject(value);
		update();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.engine.rna.IDataWriter#write(java.lang.String)
	 */
	public void write(String field, String value) throws IOException {
		if(value==null)
			value = "";
		
		//source.writeUTF(value);
		source.writeChars(value);
		update();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.engine.rna.IDataWriter#write(long)
	 */
	public void write(String field, long value) throws IOException {
		source.writeLong(value);
		update();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.engine.rna.IDataWriter#write(int)
	 */
	public void write(String field, int value) throws IOException {
		source.writeInt(value);
		update();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.engine.rna.IDataWriter#write(double)
	 */
	public void write(String field, double value) throws IOException {
		source.writeDouble(value);
		update();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.engine.rna.IDataWriter#write(java.lang.String, boolean)
	 */
	public void write(String field, boolean value) throws IOException {
		source.writeBoolean(value);
		update();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.engine.rna.IDataWriter#getSubWriter()
	 */
	public IDataWriter getSubWriter(String field) throws IOException {
		return new CrcWriter(crc);
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.engine.rna.IDataWriter#flush()
	 */
	public void flush() throws IOException {
		source.flush();
	}
	
	public void finish() throws IOException {
		flush();
	}
	
	/* (non-Javadoc)
	 * @see com.i3xx.ob.engine.rna.IDataWriter#close()
	 */
	public void close() throws IOException {
		try {
			flush();
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				source.close();
			} catch (IOException ee) {
				throw ee;
			}
		}
	}
}
