package org.i3xx.util.rna.engine;

import java.util.ArrayList;
import java.util.List;

import org.i3xx.util.rna.core.IBrick;
import org.i3xx.util.rna.core.IProxy;
import org.i3xx.util.rna.core.ITimeSlice;


public class GetLinks {

	protected List<LinkInfo> link;
	
	public GetLinks() {
		link = new ArrayList<LinkInfo>();
	}
	
	/**
	 * @return
	 */
	public List<LinkInfo> getList() {
		return link;
	}
	
	/**
	 * @param field The field or index
	 * @param brick The brick
	 * @param type The link type
	 */
	public void add(String field, IBrick brick, int type) {
		if(brick==null)
			return;
		
		IBrick temp = null;
		LinkInfo info = null;
		
		switch(type){
		case LinkInfo.PARENT:
			temp = brick.parent();
			if(temp==null)
				return;
			
			info = new LinkInfo();
			info.sourceID = brick.ID();
			info.targetID = temp.ID();
			info.type = LinkInfo.PARENT;
			info.sourceTransid = brick.transid();
			info.targetTransid = temp.transid();
			info.resource = null;
			
			link.add(info);
			break;
		case LinkInfo.CHILD:
			//implied by parent
			//need for sort
			temp = brick.parent();
			if(temp==null)
				return;
			
			info = new LinkInfo();
			info.sourceID = temp.ID();
			info.targetID = brick.ID();
			info.type = LinkInfo.CHILD;
			info.sourceTransid = temp.transid();
			info.targetTransid = brick.transid();
			info.resource = field;
			
			link.add(info);
			break;
		case LinkInfo.HISTORY_NEXT:
			temp = (IBrick)((ITimeSlice) brick).getNextTimeSlice();
			if(temp==null)
				return;
			
			info = new LinkInfo();
			info.sourceID = brick.ID();
			info.targetID = temp.ID();
			info.type = LinkInfo.HISTORY_NEXT;
			info.sourceTransid = brick.transid();
			info.targetTransid = temp.transid();
			info.resource = null;
			
			link.add(info);
			break;
		case LinkInfo.HISTORY_PREV:
			temp = (IBrick)((ITimeSlice) brick).getPrevTimeSlice();
			if(temp==null)
				return;
			
			info = new LinkInfo();
			info.sourceID = brick.ID();
			info.targetID = temp.ID();
			info.type = LinkInfo.HISTORY_PREV;
			info.sourceTransid = brick.transid();
			info.targetTransid = temp.transid();
			info.resource = null;
			
			link.add(info);
			break;
		case LinkInfo.HISTORY_NEXT_CHANGE:
			temp = (IBrick)((ITimeSlice) brick).getNextChangeTimeSlice();
			if(temp==null)
				return;
			
			info = new LinkInfo();
			info.sourceID = brick.ID();
			info.targetID = temp.ID();
			info.type = LinkInfo.HISTORY_NEXT_CHANGE;
			info.sourceTransid = brick.transid();
			info.targetTransid = temp.transid();
			info.resource = null;
			
			link.add(info);
			break;
		case LinkInfo.HISTORY_PREV_CHANGE:
			temp = (IBrick)((ITimeSlice) brick).getPrevChangeTimeSlice();
			if(temp==null)
				return;
			
			info = new LinkInfo();
			info.sourceID = brick.ID();
			info.targetID = temp.ID();
			info.type = LinkInfo.HISTORY_PREV_CHANGE;
			info.sourceTransid = brick.transid();
			info.targetTransid = temp.transid();
			info.resource = null;
			
			link.add(info);
			break;
		case LinkInfo.UNDEFINED:
			//does nothing
			break;
		default:
		}
	}
	
	/**
	 * @param field
	 * @param brick
	 * @param proxy
	 * @param type
	 */
	public void add(String field, IBrick brick, IProxy proxy, int type) {
		if(brick==null || proxy==null)
			return;
		
		IBrick temp = null;
		LinkInfo info = null;
		
		switch(type){
		case LinkInfo.PROXY:
			temp = proxy.getReference();
			if(temp==null)
				return;
			
			info = new LinkInfo();
			info.sourceID = brick.ID();
			info.targetID = temp.ID();
			info.type = LinkInfo.PROXY;
			info.sourceTransid = brick.transid();
			info.targetTransid = temp.transid();
			info.resource = field + BrickRNA.PROXY_SEPARATOR + proxy.getField() + BrickRNA.PROXY_SEPARATOR + proxy.getString();
			
			link.add(info);
			break;
		default:
		}
	}
}
