package org.i3xx.util.rna.engine;

import java.io.Serializable;

public class LinkInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int UNDEFINED = 0;
	public static final int PARENT = 1;
	public static final int CHILD = 2;
	public static final int PROXY = 3;
	public static final int HISTORY_PREV = 11;
	public static final int HISTORY_NEXT = 12;
	public static final int HISTORY_PREV_CHANGE = 13;
	public static final int HISTORY_NEXT_CHANGE = 14;
	
	protected long sourceID;
	protected long targetID;
	protected long sourceTransid;
	protected long targetTransid;
	protected int type;
	protected String resource;
	
	public LinkInfo() {
		sourceID = 0;
		targetID = 0;
		sourceTransid = 0;
		targetTransid = 0;
		type = UNDEFINED;
		resource = null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return 
			"{\"source\":"+sourceID+
			", \"target\":"+targetID+
			", \"source-transid\":"+sourceTransid+
			", \"target-transid\":"+targetTransid+
			", \"type\":"+type+
			", \"resource\":\""+resource+
			"\"}";
	}
	
	/**
	 * @param info
	 * @return
	 */
	public static final long getSourceId(LinkInfo info) {
		return info.sourceID;
	}
	
	/**
	 * @param info
	 * @return
	 */
	public static final long getTargetId(LinkInfo info) {
		return info.targetID;
	}
	
	/**
	 * @param info
	 * @return
	 */
	public static final long getSourceTransId(LinkInfo info) {
		return info.sourceTransid;
	}
	
	/**
	 * @param info
	 * @return
	 */
	public static final long getTargetTransId(LinkInfo info) {
		return info.targetTransid;
	}
	
	/**
	 * @param info
	 * @return
	 */
	public static final int getType(LinkInfo info) {
		return info.type;
	}
	
	/**
	 * @param info
	 * @return
	 */
	public static final String getResource(LinkInfo info) {
		return info.resource;
	}
}
