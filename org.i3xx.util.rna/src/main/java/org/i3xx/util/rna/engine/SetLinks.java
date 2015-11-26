package org.i3xx.util.rna.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.i3xx.util.rna.core.IBrick;
import org.i3xx.util.rna.core.IBrickData;
import org.i3xx.util.rna.core.IBrickHistory;
import org.i3xx.util.rna.core.IBrickInterna;
import org.i3xx.util.rna.core.IProxy;
import org.i3xx.util.rna.core.ITimeSlice;
import org.i3xx.util.rna.core.QueryException;
import org.i3xx.util.rna.core.XMLException;

/**
 * 
 * DryRun:
 * Here is the interest at the exception thrown to fix the error.
 * 
 * @author Stefan
 *
 */
public class SetLinks implements ISetLinks {
	
	protected boolean dryRun;
	protected List<LinkInfo> link;
	protected BrickRNA rna;
	
	public SetLinks(BrickRNA rna) {
		this.dryRun = false;
		this.link = new ArrayList<LinkInfo>();
		this.rna = rna;
	}
	
	/**
	 * @return
	 */
	public List<LinkInfo> getList() {
		return link;
	}
	/**
	 * @param info
	 */
	public void add(LinkInfo info) {
		link.add(info);
	}
	
	/**
	 * @param info
	 * @param hdl
	 * @throws QueryException 
	 * @throws XMLException 
	 */
	public void set(LinkInfo info, ILinkHandler hdl) throws XMLException, QueryException {
		
		switch( info.type ){
		case LinkInfo.CHILD:
		{
			//
			//implied by parent
			//
			//This is a sort process only. The child is moved to the position of
			//info.resource only if the position is in the range of the list and
			//the child is member of the list.
			//
			IBrick brick = hdl.get( info.sourceID, info.sourceTransid );
			IBrick child = hdl.get( info.targetID, info.targetTransid );
			if(!dryRun){
				@SuppressWarnings("unchecked")
				Collection<Object> col = ((IBrickInterna) brick).vector();
				if(col instanceof List<?>){
					List<Object> list = (List<Object>)col;
					try{
						int pos = Integer.parseInt(info.resource);
						int idx = list.indexOf(child);
						
						if(idx>-1){
							//child is available
							if(pos<list.size()){
								//sort
								Object f = list.get(pos);
								list.set(pos, child);
								list.set(idx,  f);
							}else if(rna.getRuleRawLink()){
								//do nothing
							}else{
								throw new IllegalStateException("The brick:"+info.sourceID+"."+info.sourceTransid+
										" doesn't match the position current:"+idx+", new:"+pos+", size:"+list.size());
							}
						}else{
							//child is not available
							if(rna.getRuleRawLink()){
								list.add(child);
							}else{
								throw new IllegalStateException("The brick:"+info.sourceID+"."+info.sourceTransid+
										" doesn't match the position current:"+idx+", new:"+pos+", size:"+list.size());
							}
						}
					}catch(NumberFormatException e){
						throw e;
					}
				}//else doesn't make sense
			}
			break;
		}
		case LinkInfo.PARENT:
		{
			// replace former setting or add new setting (by CBrick.add)
			
			// parent <-(1:n) brick
			IBrick brick = hdl.get( info.sourceID, info.sourceTransid );
			IBrick parent = hdl.get( info.targetID, info.targetTransid );
			if(!dryRun){
				if(rna.getRuleRawLink()){
					brick.parent(parent);
				}else{
					//don't work. Use child link instead
					parent.add(brick);
				}
			}
			break;
		}
		case LinkInfo.PROXY:
		{
			// replace former setting or add new setting (by RefHandler.updateReference)
			// uses always raw links
			
			//the brick
			IBrick brick = hdl.get( info.sourceID, info.sourceTransid );
			//the referenced brick to link by proxy
			IBrick target = hdl.get( info.targetID, info.targetTransid );
		
			// brick [field] ->(n:1) proxy ->(1:1) target [tgtfield]
			String[] tmp = info.resource.split("\\"+BrickRNA.PROXY_SEPARATOR);
			if(tmp.length<2)
				return;
			
			String field = tmp[0]; //the field of the destination
			String tgtField = tmp[1]; //the field of the target
			String newValue = tmp.length<3 ? "" : tmp[2]; //the value may be empty
			
			if(dryRun){
				//search the reference
				CCommandInstallProxy cmd = new CCommandInstallProxy(tgtField, target, 0);
		        cmd.execute(brick); //finds a proxy or creates a new one
		        IProxy newproxy = cmd.getProxy();
		        if(newproxy == null){   // no reference available,
		            hdl.get( 0 );		// use ref handler for error handling
		        }
			}else{
				//get the proxy from the data map(common: null)
				IProxy proxy = (IProxy)((IBrickData)brick).getValue(field);
				
				CCommandInstallProxy cmd = new CCommandInstallProxy(tgtField, target, 0);
				
				RefHandler rh = new RefHandler();
				proxy = rh.updateReference(proxy, brick, newValue, cmd);
				
				if(field==null)
					throw new IllegalArgumentException("The field may not be null.");
				
				//add the proxy to the data map
				boolean fD = (brick.dirty()==IBrick.DIRTY) && ((IBrickInterna) brick).updates()!=null;
				if(fD){
					((IBrickData)brick).setValue(field, proxy);
				}else{
					brick.edit();
					((IBrickData)brick).setValue(field, proxy);
					brick.clean();
				}
			}//fi
			break;
		}
		case LinkInfo.HISTORY_NEXT:
		{
			// replace former setting or add new setting
			
			if(rna.getSkipHistoryBricks())
				break;
			
			IBrick brick = hdl.get( info.sourceID, info.sourceTransid );
			ITimeSlice target = (ITimeSlice)hdl.get( info.targetID, info.targetTransid );
			if(!dryRun){
				((IBrickHistory)brick).setNextTimeSlice(target);
			}
			break;
		}
		case LinkInfo.HISTORY_PREV:
		{
			// replace former setting or add new setting
			
			if(rna.getSkipHistoryBricks())
				break;
			
			IBrick brick = hdl.get( info.sourceID, info.sourceTransid );
			ITimeSlice target = (ITimeSlice)hdl.get( info.targetID, info.targetTransid );
			if(!dryRun){
				((IBrickHistory)brick).setPrevTimeSlice(target);
			}
			break;
		}
		case LinkInfo.HISTORY_NEXT_CHANGE:
		{
			// replace former setting or add new setting
			
			if(rna.getSkipHistoryBricks())
				break;
			
			IBrick brick = hdl.get( info.sourceID, info.sourceTransid );
			ITimeSlice target = (ITimeSlice)hdl.get( info.targetID, info.targetTransid );
			if(!dryRun){
				((IBrickHistory)brick).setNextChangeTimeSlice(target);
			}
			break;
		}
		case LinkInfo.HISTORY_PREV_CHANGE:
		{
			// replace former setting or add new setting
			
			if(rna.getSkipHistoryBricks())
				break;
			
			IBrick brick = hdl.get( info.sourceID, info.sourceTransid );
			ITimeSlice target = (ITimeSlice)hdl.get( info.targetID, info.targetTransid );
			if(!dryRun){
				((IBrickHistory)brick).setPrevChangeTimeSlice(target);
			}
			break;
		}
		case LinkInfo.UNDEFINED:
		default:
		}
	}

	/**
	 * @return the dryRun
	 */
	public boolean isDryRun() {
		return dryRun;
	}

	/**
	 * @param dryRun the dryRun to set
	 */
	public void setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
	}
	
}
