package cn.hisdar.cr.systemtray;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import cn.hisdar.lib.log.HLog;

public class ComputerRemoteSystemTray implements ActionListener {

	public static final int EVENT_MOUSE_BUTTON1 = 0x01;
	public static final int EVENT_MOUSE_BUTTON3 = 0x02;
	public static final int EVENT_EXIT			= 0x03;
	public static final int EVENT_SHOW_MAIN_FRAME = 0x04;
	
	private static ComputerRemoteSystemTray computerRemoteSystemTray = null;
	
	private TrayIconMouseEventHandler trayIconMouseEventHandler;
	private TrayIcon trayIcon = null;
	private SystemTray systemTray = null;
	
	private ArrayList<TrayIconEventListener> trayIconEventListeners;
	
	private PopupMenu trayIconPopupMenu;
	private MenuItem exitMenuItem;
	private MenuItem showMainFrameMenuItem;
	
	private ComputerRemoteSystemTray() {
		
		trayIconEventListeners = new ArrayList<>();
		trayIconMouseEventHandler = new TrayIconMouseEventHandler();
		
		createPopupMenu();
		
		ImageIcon icomImage = new ImageIcon("./image/tray_icon.png");
		trayIcon = new TrayIcon(icomImage.getImage(), "Computer remote", trayIconPopupMenu);
		trayIcon.addMouseListener(trayIconMouseEventHandler);
		
		systemTray = SystemTray.getSystemTray();
		try {
			systemTray.add(trayIcon);
		} catch (AWTException e) {
			HLog.el("Init system tray fail");
			HLog.el(e);
		}
		
	}
	
	public void destroy() {
		systemTray.remove(trayIcon);
	}
	
	public static ComputerRemoteSystemTray getInstance() {
		if (computerRemoteSystemTray == null) {
			synchronized (ComputerRemoteSystemTray.class) {
				if (computerRemoteSystemTray == null) {
					computerRemoteSystemTray = new ComputerRemoteSystemTray();
				}
			}
		}
		
		return computerRemoteSystemTray;
	}

	private void createPopupMenu() {
		exitMenuItem = new MenuItem("退出");
		exitMenuItem.addActionListener(this);
		showMainFrameMenuItem = new MenuItem("显示主界面");
		showMainFrameMenuItem.addActionListener(this);
		
		trayIconPopupMenu = new PopupMenu();
		trayIconPopupMenu.add(showMainFrameMenuItem);
		trayIconPopupMenu.add(exitMenuItem);
	}
	
	private class TrayIconMouseEventHandler extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (arg0.getButton() == MouseEvent.BUTTON1) {
				notifyTrayIconEvent(EVENT_MOUSE_BUTTON1);
			} else if (arg0.getButton() == MouseEvent.BUTTON3) {
				//trayIconPopupMenu.show(null, arg0.getX(), arg0.getY());
			}
		}
	}
	
	public void addTrayIconEventListener(TrayIconEventListener listener) {
		for (int i = 0; i < trayIconEventListeners.size(); i++) {
			if (trayIconEventListeners.get(i) == listener) {
				return;
			}
		}
		
		trayIconEventListeners.add(listener);
	}
	
	public void removeTrayIconEventListener(TrayIconEventListener listener) {
		for (int i = 0; i < trayIconEventListeners.size(); i++) {
			if (trayIconEventListeners.get(i) == listener) {
				trayIconEventListeners.remove(i);
				return;
			}
		}
	}
	
	private void notifyTrayIconEvent(int event) {
		for (int i = 0; i < trayIconEventListeners.size(); i++) {
			trayIconEventListeners.get(i).trayIconEvent(event);
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if (arg0.getSource() == exitMenuItem) {
			notifyTrayIconEvent(EVENT_EXIT);
		} else if (arg0.getSource() == showMainFrameMenuItem) {
			notifyTrayIconEvent(EVENT_SHOW_MAIN_FRAME);
		}
	}
}
