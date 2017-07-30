package cn.hisdar.cr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import cn.hisdar.computerremote.common.Global;
import cn.hisdar.cr.communication.handler.CRServer;
import cn.hisdar.cr.setting.ComputerRemoteSettingDialog;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.ui.UIAdapter;

public class ServerControlerView extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7134310494197378670L;
	
	private JButton startStopServerButton;
	private Icon startServerIcon;
	private Icon stopServerIcon;
	
	private Icon settingIcon;
	
	private JButton settingButton;
	
	private CRServer computerRemoteServer;
	
	private boolean isServerStart;
	
	private ComputerRemoteSettingDialog settingDialog;
	private HConfig applicationConfig;
	
	public ServerControlerView() {
		
		applicationConfig = HConfig.getInstance(Global.APPLICATION_CONFIG_FILE);
		computerRemoteServer = new CRServer(5299);
		
		setOpaque(true);
		setBackground(new Color(0xbcc7d8));

		settingDialog = new ComputerRemoteSettingDialog();
		
		setLayout(new BorderLayout());
		add(getLeftPanel(), BorderLayout.WEST);
		add(getRightPanel(), BorderLayout.EAST);
		
	}
	
	private JPanel getRightPanel() {
		JPanel rightPanel = new JPanel();
		rightPanel.setOpaque(false);
		
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.RIGHT);
		rightPanel.setLayout(layout);
		
		settingIcon = new ImageIcon("./image/setting_icon.png");
		settingButton = new JButton(settingIcon);
		settingButton.addActionListener(this);
		settingButton.setBorder(null);
		settingButton.setOpaque(false);
		
		rightPanel.add(settingButton);
		
		
		return rightPanel;
	}
	
	private JPanel getLeftPanel() {
		JPanel leftPanel = new JPanel();
		leftPanel.setOpaque(false);
		
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);
		leftPanel.setLayout(layout);
		
		startStopServerButton = new JButton();
		startStopServerButton.setOpaque(false);
		startStopServerButton.setBorder(null);
		startStopServerButton.addActionListener(this);
		leftPanel.add(startStopServerButton);
		
		startServerIcon = new ImageIcon("./image/start.png");
		stopServerIcon = new ImageIcon("./image/stop.png");
		
		boolean isStartServerWhenApplicationStart
			= applicationConfig.getConfigValue(Global.START_SERVER_WHEN_APPLICATION_START, true);
		
		if (isStartServerWhenApplicationStart) {
			
			isServerStart = true;
			startStopServerButton.setIcon(stopServerIcon);
			computerRemoteServer.startServer();
		} else {
			isServerStart = false;
			startStopServerButton.setIcon(startServerIcon);
		}
		
		return leftPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == startStopServerButton) {
			// stop server
			if (isServerStart) {
				isServerStart = false;
				startStopServerButton.setIcon(startServerIcon);
				computerRemoteServer.stopServer();
				
			// start server
			} else {
				
				isServerStart = true;
				startStopServerButton.setIcon(stopServerIcon);
				computerRemoteServer.startServer();
			}
		} else if (e.getSource() == settingButton) {
			settingDialog.setLocation(UIAdapter.getCenterLocation(null, settingDialog));
			settingDialog.setModal(true);
			settingDialog.setVisible(true);
		}
	}
	
	
}
