package cn.hisdar.cr.event;

import cn.hisdar.cr.communication.CRClient;
import cn.hisdar.cr.debug.DebugerTimeDataContainer;

public class HEventData {
	
	public String eventData;
	public CRClient client;
	public DebugerTimeDataContainer timeDataContainer;

	public HEventData(HEventData eventData) {
		this.eventData = eventData.eventData;
		this.client = eventData.client;
		this.timeDataContainer = eventData.timeDataContainer;
	}
	
	public HEventData(String string, CRClient client) {
		this.eventData = string;
		this.client = client;
		this.timeDataContainer = new DebugerTimeDataContainer();
	}
	
	public HEventData(String string, CRClient client, DebugerTimeDataContainer timeDataContainer) {
		this.eventData = string;
		this.client = client;
		this.timeDataContainer = timeDataContainer;
	}
}
