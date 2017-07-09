package cn.hisdar.computerremote.event;

import cn.hisdar.computerremote.debug.DebugerTimeDataContainer;
import cn.hisdar.computerremote.server.ComputerRemoteClient;

public class HEventData {
	
	public String eventData;
	public ComputerRemoteClient client;
	public DebugerTimeDataContainer timeDataContainer;

	public HEventData(HEventData eventData) {
		this.eventData = eventData.eventData;
		this.client = eventData.client;
		this.timeDataContainer = eventData.timeDataContainer;
	}
	
	public HEventData(String string, ComputerRemoteClient client) {
		this.eventData = string;
		this.client = client;
		this.timeDataContainer = new DebugerTimeDataContainer();
	}
	
	public HEventData(String string, ComputerRemoteClient client, DebugerTimeDataContainer timeDataContainer) {
		this.eventData = string;
		this.client = client;
		this.timeDataContainer = timeDataContainer;
	}
}
