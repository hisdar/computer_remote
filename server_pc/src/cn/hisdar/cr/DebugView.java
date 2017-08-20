package cn.hisdar.cr;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JPanel;

import cn.hisdar.cr.communication.data.ResponseData;
import cn.hisdar.cr.communication.data.ScreenPictureData;
import cn.hisdar.cr.communication.handler.ResponseHandler;
import cn.hisdar.cr.communication.handler.ResponseListener;
import cn.hisdar.cr.screen.ScreenHunterServer;
import cn.hisdar.lib.log.HLog;

public class DebugView extends JPanel implements ActionListener, ResponseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5552828602845863594L;
	private JButton sendScreenPicture = null;
	
	public DebugView() {
		sendScreenPicture = new JButton("·¢ËÍÆÁÄ»½ØÍ¼");
		add(sendScreenPicture);
		sendScreenPicture.addActionListener(this);
		
		ResponseHandler.getInstance().addResponseListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == sendScreenPicture) {
			ScreenPictureData shData = ScreenHunterServer.getInstance().getScreenPictureData();
			//CRCSManager.getInstance().screenPictureChangeEvent(shData);
			HLog.il("Send a picture to clients");
		}
	}

	@Override
	public void responseEvent(ResponseData responseData) {
		long writeTime = responseData.getWriteTime();
		long currentTime = new Date().getTime();
		
		long socketDelay = currentTime - writeTime;
		
		HLog.dl("network delay:" + socketDelay + ", writeTime=" + writeTime + ", currateTime=" + currentTime);
		
	}
}
