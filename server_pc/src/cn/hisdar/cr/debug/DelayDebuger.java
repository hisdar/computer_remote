package cn.hisdar.cr.debug;

import java.util.Date;

public class DelayDebuger {

	private long startTime;
	private long endTime;
	
	public DelayDebuger() {
		startTime = new Date().getTime();
	}
	
	public long getDelay() {
		endTime = new Date().getTime();
		return endTime - startTime;
	}
	
	public void reset() {
		startTime = new Date().getTime();
	}
}
