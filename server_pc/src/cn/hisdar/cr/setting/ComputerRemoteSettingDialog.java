package cn.hisdar.cr.setting;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import cn.hisdar.computerremote.common.Global;
import cn.hisdar.lib.adapter.FileAdapter;
import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.local.Windows;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.lib.ui.HLinearPanel;

public class ComputerRemoteSettingDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 381591698810620869L;
	private static final int SETTING_DIALIG_WIDTH = 400;
	private static final int SETTING_DIALOG_HEIGHT = 250;

	private ImageIcon applicationIcon;
	
	private JCheckBox autoStartCheckBox;
	private JCheckBox startServerCheckBox;
	
	private HConfig applicationConfig;
	
	public ComputerRemoteSettingDialog() {
		
		applicationIcon = new ImageIcon("./image/application_icon.png");
		setIconImage(applicationIcon.getImage());
		
		setTitle("Setting");
		setSize(SETTING_DIALIG_WIDTH, SETTING_DIALOG_HEIGHT);
	
		setLayout(new BorderLayout());
	
		applicationConfig = HConfig.getInstance(Global.APPLICATION_CONFIG_FILE);
		
		HLinearPanel mainPanel = new HLinearPanel();
		mainPanel.add(getStartPanel());
		
		add(mainPanel, BorderLayout.CENTER);
		initConfig();
	}
	
	private JPanel getStartPanel() {
		
		HLinearPanel startLinearPanel = new HLinearPanel();
		startLinearPanel.setBorder(BorderFactory.createTitledBorder(""));
		
		autoStartCheckBox = new JCheckBox("开机自动启动程序");
		startServerCheckBox = new JCheckBox("应用程序启动的时候启动监听服务");
		startServerCheckBox.addActionListener(this);
		autoStartCheckBox.addActionListener(this);
		
		startLinearPanel.add(autoStartCheckBox);
		startLinearPanel.add(startServerCheckBox);
		
		return startLinearPanel;
		
	}
	
	private void initConfig() {
		boolean isAutoStart = applicationConfig.getConfigValue(Global.AUTION_START_CONFIG_NAME, false);
		boolean isStartServerWhenApplicationStart = 
				applicationConfig.getConfigValue(Global.START_SERVER_WHEN_APPLICATION_START, true);
		
		autoStartCheckBox.setSelected(isAutoStart);
		startServerCheckBox.setSelected(isStartServerWhenApplicationStart);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == autoStartCheckBox) {
			if (autoStartCheckBox.isSelected()) {
				applicationConfig.setConfigItem(new ConfigItem(Global.AUTION_START_CONFIG_NAME, true));
				setAutoStart();
			} else {
				applicationConfig.setConfigItem(new ConfigItem(Global.AUTION_START_CONFIG_NAME, false));
			}
		} else if (arg0.getSource() == startServerCheckBox) {
			if (startServerCheckBox.isSelected()) {
				applicationConfig.setConfigItem(new ConfigItem(Global.START_SERVER_WHEN_APPLICATION_START, true));
			} else {
				applicationConfig.setConfigItem(new ConfigItem(Global.START_SERVER_WHEN_APPLICATION_START, false));
			}
		}
	}
	
	private boolean setAutoStart() {
		
		// get system start up directory
		String userHome = System.getProperty("user.home");
		String startUpDir = FileAdapter.pathCat(userHome, Global.START_UP_PATH);

		// get current folder
		String currentDir = "D:\\Program Files\\Hisdar\\ComputerRemote";//System.getProperty("user.dir");
		
		String applicationName = Global.APPLICATION_NAME + ".jar";
		String applicationPath = currentDir + "\\" + applicationName;//FileAdapter.pathCat(currentDir, applicationName);
		
		HLog.il("application path:" + applicationPath);
		HLog.il("target path:" + startUpDir);
		
		Windows windows = new Windows();
		boolean ret = windows.createShortCut(applicationPath, startUpDir, Global.AUTO_START_COMMAND);
		
		HLog.il("Shortcut create result:" + ret);
		
		return ret;
	}
}
