package org.i3xx.util.rna.engine;

public class DocInfo {

	protected int length;
	protected String mimetype;
	protected long timestamp;
	protected int size;
	protected long crc;
	
	/**
	 * 
	 */
	public DocInfo() {
		this.length = 0;
		this.mimetype = null;
		this.timestamp = 0;
		this.size = 0;
		this.crc = 0;
	}

	/**
	 * @param length
	 * @param mimetype
	 * @param timestamp
	 * @param size
	 */
	public DocInfo(int length, String mimetype, long timestamp, int size) {
		this.length = length;
		this.mimetype = mimetype;
		this.timestamp = timestamp;
		this.size = size;
		this.crc = 0;
	}
}
