package cn.hisdar.computerremote;

import java.awt.GridLayout;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JPanel;

import cn.hisdar.lib.ui.output.HKeyValuePanel;

public class ClientInforPanel extends JPanel {

	private Socket socket = null;
	private HKeyValuePanel clientIpPanel = null;
	private HKeyValuePanel clientNamePanel = null;
	
	public ClientInforPanel(Socket socket) {
		this.socket = socket;
		
		InetAddress inetAddress = socket.getInetAddress();
		
		clientIpPanel = new HKeyValuePanel("客户端IP：", inetAddress.getHostAddress());
		clientNamePanel = new HKeyValuePanel();//"客户端名：", inetAddress.getHostName());
		
		setLayout(new GridLayout(2, 1, 5, 5));
		add(clientIpPanel);
		add(clientNamePanel);
	}
	
	public Socket getSocket() {
		return socket;
	}
}
