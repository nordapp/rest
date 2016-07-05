package org.i3xx.util.client.wk;

import java.util.ArrayList;
import java.util.List;

import org.i3xx.util.basic.core.ITimer;

/**
 * Singleton implementation
 * 
 * @author stefan
 *
 */
public class Timer implements Runnable {
	
	private volatile boolean cont;
	private List<ITimer> list;
	
	private Timer() {
		cont = true;
		list = new ArrayList<ITimer>();
	}
	
	public void addTimer(ITimer timer) {
		//do not add during the general cleanup
		if(!cont)
			return;
		
		synchronized(list) {
			list.add(timer);
		}
	}
	
	public void removeTimer(ITimer timer) {
		//do not remove during the general cleanup
		if(!cont)
			return;
		
		synchronized(list) {
			list.remove(timer);
		}
	}
	
	/**
	 * 
	 */
	public void stop() {
		cont = false;
	}
	
	/**
	 * 
	 */
	public void stop_and_block() {
		cont = false;
		
		synchronized(this) {
			while(list!=null) {
				try{
					wait(100);
				}catch(InterruptedException e) {
					//does nothing
				}
			}//while
		}
	}
	
	@Override
	public void run() {
		
		while(cont) {
			
			synchronized(list) {
				//Needs an array to avoid concurrent modification exception.
				ITimer[] tm = list.toArray( new ITimer[list.size()] );
				for(int i=0;i<tm.length;i++) {
					tm[i].timerNotify();
				}//for
			}
			
			try{
				Thread.sleep(1000);
			}catch(InterruptedException e) {
				//does nothing
			}
		}//while
		
		synchronized(list) {
			for(ITimer t : list) {
				if(t instanceof ICleanable) {
					((ICleanable)t).cleanup();
				}//fi
			}//for
		}
		list = null;
		
		//restartable
		Timer.timer = null;
	}
	
	private static Timer timer = null;
	
	public static Timer instance(){
		if(timer==null) {
			timer = new Timer();
			Thread t = new Thread(timer);
			t.start();
		}
		
		return timer;
	}
}
