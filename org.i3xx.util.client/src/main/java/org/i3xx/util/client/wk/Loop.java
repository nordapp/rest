package org.i3xx.util.client.wk;

import org.i3xx.util.basic.core.ITimer;
import org.i3xx.util.mutable.MutableLong;

public class Loop implements ITimer {

	final Process process;
	
	final Trigger trigger;
	
	final long interval;
	
	final MutableLong current;
	
	/**
	 * @param process
	 * @param interval
	 */
	private Loop(Process process, long interval, Trigger trigger, MutableLong current) {
		this.process = process;
		this.interval = interval;
		this.trigger = trigger;
		this.current = current;
	}
	
	/**
	 * Creates a new Loop
	 * 
	 * @param process The process to execute
	 * @param interval The interval
	 * @return
	 */
	public static final Loop create(ProcessFx process, long interval) {
		return new Loop( (Process)process.getProcess(), interval, null, new MutableLong(System.currentTimeMillis()) );
	}
	
	/**
	 * @param trigger
	 * @return
	 */
	public Loop trigger(Trigger trigger) {
		return new Loop(process, interval, trigger, current);
	}

	@Override
	public void timerNotify() {
		long t = System.currentTimeMillis();
		if( (t-interval) > current.longValue() ) {
			current.longValue(t);
			if(trigger==null) {
				process.exec();
			}else{
				trigger.exec(process);
			}
		}//fi
	}
}
