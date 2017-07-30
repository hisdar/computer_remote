package cn.hisdar.cr;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import cn.hisdar.cr.communication.handler.ScreenPictureHandler;
import cn.hisdar.cr.screen.ScreenHunterServer;
import cn.hisdar.lib.log.HLog;

public class DebugView extends JPanel implements ActionListener {

	private JButton sendScreenPicture = null;
	public DebugView() {
		sendScreenPicture = new JButton("·¢ËÍÆÁÄ»½ØÍ¼");
		add(sendScreenPicture);
		sendScreenPicture.addActionListener(this);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == sendScreenPicture) {
			ScreenPictureHandler shData = ScreenHunterServer.getInstance().getScreenHunterData();
			//CRCSManager.getInstance().screenPictureChangeEvent(shData);
			HLog.il("Send a picture to clients");
		}
	}
}
