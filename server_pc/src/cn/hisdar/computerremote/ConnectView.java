package cn.hisdar.computerremote;

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
import cn.hisdar.computerremote.server.CRCSManager;
import cn.hisdar.computerremote.server.CRClient;
import cn.hisdar.computerremote.server.CRServer;
import cn.hisdar.computerremote.server.ClientEventListener;
import cn.hisdar.computerremote.server.ServerEventListener;
import cn.hisdar.lib.commandline.CommandLineAdapter;
import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.lib.net.HInetAddress;
import cn.hisdar.lib.ui.HLinearPanel;
import cn.hisdar.lib.ui.TitlePanel;
import cn.hisdar.lib.ui.output.HKeyValuePanel;

public class ConnectView extends JPanel implements ClientEventListener, ServerEventListener {

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
	private ArrayList<ClientInforPanel> clientInforPanels = null;
	
	public ConnectView() {
		setLayout(new BorderLayout());
		
		clientInforPanels = new ArrayList<>();
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
		
		/*CRServer cmdServer = CRServerManager.getInstance().getCmdServer();
		cmdServer.addClientEventListener(this);
		cmdServer.addServerEventListener(this);*/
		CRCSManager.getInstance().addServerEventListener(this);
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

	@Override
	public void clientConnectEvent(CRServer crServer, Socket socket) {
		
		HLog.il("Client connect:" + socket.getInetAddress().getHostAddress());
		
		ClientInforPanel currentClientPanel = new ClientInforPanel(socket);
		HLog.il("Create information panel");
		
		currentClientPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		clientInforPanels.add(currentClientPanel);
		clientInforPanel.add(currentClientPanel);
		clientInforPanel.revalidate();
		//clientInforPanel.repaint();
		
		HLog.il("Finish update ui");
	}

	@Override
	public void clientDisconnectEvent(CRClient crClient, Socket socket) {
		
		for (int i = 0; i < clientInforPanels.size(); i++) {
			if (clientInforPanels.get(i).getSocket() == socket) {
		
				HLog.il("Client disconnect:" + socket.getInetAddress().getHostAddress());
				
				clientInforPanel.removeChild(clientInforPanels.get(i));
				clientInforPanels.remove(i);
				
				clientInforPanel.revalidate();
				clientInforPanel.repaint();
			}
		}
	}

	@Override
	public void serverStateEvent(CRServer crServer, int serverState) {
		if (serverState == CRServer.SERVER_STATE_START) {
			serverStatePanel.setValue("启动");
			serverStatePanel.getValueLabel().setForeground(new Color(0x32CD32));
		} else if (serverState == CRServer.SERVER_STATE_STOP) {
			serverStatePanel.setValue("停止");
			serverStatePanel.getValueLabel().setForeground(new Color(0xCD2626));
		}
		
		if (crServer != null) {
			serverPortPanel.setValue(crServer.getServerSocket().getLocalPort() + "");

			try {
				//String currentIpAddress = InetAddress.getLocalHost().getHostAddress();
				String currentIpAddress = HInetAddress.getInetAddress();
				String currentHostName = InetAddress.getLocalHost().getHostName();

				serverIpPanel.setValue(currentIpAddress);
				serverNamePanel.setValue(currentHostName);
				checkServerInfo(currentIpAddress, crServer.getServerSocket().getLocalPort() + "");
			} catch (UnknownHostException e) {
				HLog.el("get local host information fail");
				HLog.el(e);
			}
		}
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
}
