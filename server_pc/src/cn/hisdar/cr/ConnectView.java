package cn.hisdar.cr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.hisdar.computerremote.common.Global;
import cn.hisdar.cr.communication.socket.SocketDisconnectListener;
import cn.hisdar.cr.communication.socket.SocketIOManager;
import cn.hisdar.lib.commandline.CommandLineAdapter;
import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.lib.net.HInetAddress;
import cn.hisdar.lib.ui.HLinearPanel;
import cn.hisdar.lib.ui.TitlePanel;
import cn.hisdar.lib.ui.output.HKeyValuePanel;

public class ConnectView extends JPanel implements SocketAccepterListener, SocketDisconnectListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5964789222548553400L;
	
	private TitlePanel serverInfoTitlePanel = null;
	private TitlePanel clientInfoTitlePanel = null;
	
	private HKeyValuePanel serverIpPanel = null;
	private HKeyValuePanel serverPortPanel = null;
	private HKeyValuePanel serverNamePanel = null;
	private HKeyValuePanel serverStatePanel = null;
	private HKeyValuePanel autoStartPanel = null;
	
	private HLinearPanel clientInforPanel = null;
	private ArrayList<ClientInforPanel> clientInforItems = null;
	
	public ConnectView() {
		setLayout(new BorderLayout());
		
		clientInforItems = new ArrayList<>();
		clientInforPanel = new HLinearPanel();
		
		serverInfoTitlePanel = new TitlePanel("服务器信息");
		clientInfoTitlePanel = new TitlePanel("客户端信息");

		JPanel serverInfo = getServerInforPanel();
		serverInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JPanel serverInforPanel = new JPanel(new BorderLayout());
		serverInforPanel.add(serverInfoTitlePanel, BorderLayout.NORTH);
		serverInforPanel.add(serverInfo, BorderLayout.CENTER);
		//serverInforPanel.setPreferredSize(new Dimension(0, 50));
		
		JPanel clientInforMainPanel = new JPanel(new BorderLayout());
		clientInforMainPanel.add(clientInfoTitlePanel, BorderLayout.NORTH);
		clientInforMainPanel.add(clientInforPanel, BorderLayout.CENTER);
		
		HLinearPanel mainPanel = new HLinearPanel();
		mainPanel.add(serverInforPanel);
		mainPanel.add(clientInforMainPanel);
		
		add(mainPanel, BorderLayout.CENTER);
		
		SocketAccepter.getInstance().addSocketAccepterListener(this);
		SocketIOManager.getInstance().addSocketDisconnectListener(this);
	}
	
	private JPanel getServerInforPanel() {
		JPanel serverInfoPanel = new JPanel(new GridLayout(5, 1, 5, 5));
		
		serverNamePanel = new HKeyValuePanel("服务器名：  ", "NA");
		serverIpPanel = new HKeyValuePanel("服务器IP：  ", "NA");
		serverPortPanel = new HKeyValuePanel("服务器端口：", "NA");
		serverStatePanel = new HKeyValuePanel("服务器状态：", "NA");
		autoStartPanel = new HKeyValuePanel("开机自启动：", "NA");
		
		serverInfoPanel.add(serverNamePanel);
		serverInfoPanel.add(serverIpPanel);
		serverInfoPanel.add(serverPortPanel);
		serverInfoPanel.add(serverStatePanel);
		serverInfoPanel.add(autoStartPanel);
		
		return serverInfoPanel;
	}
	
	private void checkServerInfo(String serverIpAddrese, String port) {
		
		// check is this is auto start up
		CommandLineAdapter commandLineAdapter = CommandLineAdapter.getInstance();
		if (!commandLineAdapter.isCommanded(Main.COMMAND_AUTO_START)) {
			HLog.il("Not auto start");
			return;
		}
		
		HLog.il("Auto start application");
		
		// check if ip address or port is changed, show dialog to user
		HConfig applicationConfig = HConfig.getInstance(Global.APPLICATION_CONFIG_FILE);
		String lastIpAddress = applicationConfig.getConfigValue(Global.APPLICATION_LAST_IP_ADDRESS, null);
		String lastPort = applicationConfig.getConfigValue(Global.APPLICATION_LAST_PORT, null);
		
		if (lastIpAddress == null
				|| lastPort == null
				|| !lastIpAddress.equals(serverIpAddrese)
				|| !lastPort.equals(port)) {
			
			applicationConfig.setConfigItem(new ConfigItem(Global.APPLICATION_LAST_IP_ADDRESS, serverIpAddrese));
			applicationConfig.setConfigItem(new ConfigItem(Global.APPLICATION_LAST_PORT, port));
			
			JOptionPane.showMessageDialog(
					null,
					"IP地址：" + serverIpAddrese + "\n端口：    " + port,
					Global.APPLICATION_NAME + "消息",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	@Override
	public void clientConnectEvent(Socket clientSocket) {
		HLog.il("Client connect:" + clientSocket.getInetAddress().getHostAddress());
		
		ClientInforPanel currentClientPanel = new ClientInforPanel(clientSocket);
		HLog.il("Create information panel");
		
		currentClientPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		clientInforItems.add(currentClientPanel);
		clientInforPanel.add(currentClientPanel);
		clientInforPanel.revalidate();
		//clientInforPanel.repaint();
		
		HLog.il("Finish update ui");
	}

	@Override
	public void socketAccepterEvent(int state) {
		if (state == SocketAccepter.SERVER_STATE_START) {
			serverStatePanel.setValue("启动");
			serverStatePanel.getValueLabel().setForeground(new Color(0x32CD32));
		} else if (state == SocketAccepter.SERVER_STATE_STOP) {
			serverStatePanel.setValue("停止");
			serverStatePanel.getValueLabel().setForeground(new Color(0xCD2626));
		}
		
		String serverPort = String.format("%d", 5299);
		serverPortPanel.setValue(serverPort);

		try {
			//String currentIpAddress = InetAddress.getLocalHost().getHostAddress();
			String currentIpAddress = HInetAddress.getInetAddress();
			String currentHostName = InetAddress.getLocalHost().getHostName();

			serverIpPanel.setValue(currentIpAddress);
			serverNamePanel.setValue(currentHostName);
			checkServerInfo(currentIpAddress, serverPort);
		} catch (UnknownHostException e) {
			HLog.el("get local host information fail");
			HLog.el(e);
		}
	}

	@Override
	public void socketDisconnectEvent(Socket socket) {
		
		HLog.dl("socket disconnect event");
		ClientInforPanel clientInfoItem = null;
		for (ClientInforPanel ciPanel : clientInforItems) {
			if (ciPanel.getSocket() == socket) {
				clientInfoItem = ciPanel;
			}
		}
		
		if (clientInfoItem == null) {
			HLog.dl("socket not found");
			return;
		}

		clientInforPanel.removeChild(clientInfoItem);
	}
}
