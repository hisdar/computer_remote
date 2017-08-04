package cn.hisdar.cr;

import java.net.InetAddress;
import java.net.UnknownHostException;

import cn.hisdar.cr.communication.data.AbstractData;
import cn.hisdar.cr.communication.data.RequestData;
import cn.hisdar.cr.communication.data.ServerInfoData;
import cn.hisdar.cr.communication.handler.RequestEventListener;
import cn.hisdar.cr.communication.handler.RequestHandler;
import cn.hisdar.cr.communication.socket.SocketIOManager;
import cn.hisdar.lib.log.HLog;

public class MiscEventHandler implements RequestEventListener {

	public MiscEventHandler() {
		RequestHandler.getInstance().addRequestEventListener(this);
	}
	
	@Override
	public void requestEvent(RequestData requestData) {
		
		if (requestData.getRequestDataType() == AbstractData.DATA_TYPE_SERVER_INFO) {
			String serverName = "Unknow";
			try {
				serverName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e1) {}
			
			ServerInfoData serverInfoData = new ServerInfoData();
			serverInfoData.setServerName(serverName);
			
			SocketIOManager.getInstance().sendDataToClient(serverInfoData, null);
			
			HLog.dl("Send server info to client:" + serverInfoData);
		}
	}

}
