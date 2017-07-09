package com.cn.hisdar.cra;

public class HResponseData {

	public long sendTime;
	public long readTime;
	
	public void setSendTime(String sendTime) {
		this.sendTime = 0;
		
		try {
			this.sendTime = Long.parseLong(sendTime);
		} catch (Exception e) {}
	}
}
