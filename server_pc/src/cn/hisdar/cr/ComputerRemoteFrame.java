package cn.hisdar.cr;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import cn.hisdar.cr.systemtray.ComputerRemoteSystemTray;
import cn.hisdar.cr.systemtray.TrayIconEventListener;
import cn.hisdar.lib.ui.HSplitPane;
import cn.hisdar.lib.ui.UIAdapter;

public class ComputerRemoteFrame extends JFrame implements TrayIconEventListener {

	private static final String COMPUTER_REMOTE_TITLE = "Computer Remote - V1.0";
	private static final int COMPUTER_REMOTE_WIDTH = 850;
	private static final int COMPUTER_REMOTE_HEIGHT = 500;

	private ImageIcon applicationIcon;
	private OutputView outputView;
	private ControlerView controlerView;
	private ConnectView connectView;
	private ServerControlerView serverControlerView;
	private DebugView debugView;
	
	private ComputerRemoteSystemTray systemTray;
	private FrameEventHandler frameEventHandler;
	
	public ComputerRemoteFrame() {
		
		frameEventHandler = new FrameEventHandler();
		addWindowListener(frameEventHandler);
		
		systemTray = ComputerRemoteSystemTray.getInstance();
		systemTray.addTrayIconEventListener(this);
		initUI();
	}
	
	private void initUI() {
		setTitle(COMPUTER_REMOTE_TITLE);
		setSize(COMPUTER_REMOTE_WIDTH, COMPUTER_REMOTE_HEIGHT);
		setLocation(UIAdapter.getCenterLocation(null, this));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		outputView = new OutputView();
		connectView = new ConnectView();
		controlerView = new ControlerView();
		serverControlerView = new ServerControlerView();
		debugView = new DebugView();
		
		JPanel serverControlAndControlerView = new JPanel(new BorderLayout());
		serverControlAndControlerView.add(serverControlerView, BorderLayout.NORTH);
		serverControlAndControlerView.add(controlerView, BorderLayout.CENTER);
		serverControlAndControlerView.add(debugView, BorderLayout.EAST);
		
		HSplitPane outputViewAndMainView = new HSplitPane(HSplitPane.HORIZONTAL_SPLIT);
		outputViewAndMainView.setDividerLocation(0.7);
		outputViewAndMainView.setBottomComponent(outputView);
		outputViewAndMainView.setTopComponent(serverControlAndControlerView);
		
		HSplitPane connectViewAndMainView = new HSplitPane(HSplitPane.VERTICAL_SPLIT);
		connectViewAndMainView.setDividerLocation(0.2);
		connectViewAndMainView.setLeftComponent(connectView);
		connectViewAndMainView.setRightComponent(outputViewAndMainView);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(HSplitPane.DEFAULT_DIVIDER_COLOR);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		mainPanel.add(connectViewAndMainView, BorderLayout.CENTER);
		
		add(mainPanel, BorderLayout.CENTER);
		
		applicationIcon = new ImageIcon("./image/application_icon.png");
		setIconImage(applicationIcon.getImage());
	}

	@Override
	public void trayIconEvent(int event) {
		if (event == ComputerRemoteSystemTray.EVENT_EXIT) {
			systemTray.destroy();
			System.exit(0);
		} else if (event == ComputerRemoteSystemTray.EVENT_SHOW_MAIN_FRAME) {
			setVisible(true);
		} else if (event == ComputerRemoteSystemTray.EVENT_MOUSE_BUTTON1) {
			setVisible(true);			
		}
	}
	
	private class FrameEventHandler extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			// TODO Auto-generated method stub
			super.windowClosing(e);
		}

		@Override
		public void windowIconified(WindowEvent e) {
			setExtendedState(JFrame.NORMAL);
			setVisible(false);
		}
	}
}
