package cn.hisdar.computerremote.server;

import cn.hisdar.computerremote.common.Global;
import cn.hisdar.lib.log.HLog;

public class Communication {

	private static final String XML_FILE_HEAD = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
	
	private String packageData(String data) {
		
		String xmlFileData = XML_FILE_HEAD + data + "\n";
		
		return Global.DATA_BEGIN_FLAG + "\n" + xmlFileData + Global.DATA_END_FLAG + "\n";
	}
	
	private String packageXmlFileData(String data) {
		return String.format(Global.CONTROL_DATA_LABEL_1, data);
	}
	
	public String packageExitEventData() {

		String dataType = String.format(Global.DATA_TYPE_FORMAT, Global.SERVER_EVENT_EXIT);
		String xmlFileData = packageXmlFileData(dataType);
		String messageData = packageData(xmlFileData);
		
		return messageData;
	}
}
