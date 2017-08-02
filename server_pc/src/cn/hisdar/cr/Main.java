package cn.hisdar.cr;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import cn.hisdar.cr.communication.handler.MotionEventHandler;
import cn.hisdar.cr.communication.handler.ScreenPictureHandler;
import cn.hisdar.cr.controler.KeyControler;
import cn.hisdar.cr.controler.MouseControler;
import cn.hisdar.cr.event.EventDispatcher;
import cn.hisdar.cr.screen.ScreenHunterServer;
import cn.hisdar.cr.systemtray.ComputerRemoteSystemTray;
import cn.hisdar.lib.commandline.CommandLineAdapter;
import cn.hisdar.lib.log.HLog;

public class Main {

	public static final String COMMAND_AUTO_START = "auto-start";
	public static final String COMMAND_HELP_H = "-h";
	public static final String COMMAND_HELP_HELP = "-help";
	public static final String COMMAND_HELP_SIGN = "?";
	public static final String COMMAND_PORT = "-port";
	
	private static final String[] COMMANDS = {
			COMMAND_AUTO_START,
			COMMAND_HELP_H,
			COMMAND_HELP_HELP,
			COMMAND_HELP_SIGN,
			COMMAND_PORT};
	
	public static void main(String[] args) {
		
		// register log system
		HLog.enableCmdLog();

		// init handler 
		MiscEventHandler miscEventHandler = new MiscEventHandler();
		
		// init command line 
		CommandLineAdapter commandLineAdapter = CommandLineAdapter.getInstance();
		commandLineAdapter.addCommands(COMMANDS);
		commandLineAdapter.init(args);
		
		// set windows style
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			HLog.el(e);
		}
	
		// add system tray
		ComputerRemoteSystemTray.getInstance();
		
		// init controls
		EventDispatcher eventDispatcher = EventDispatcher.getInstance();

		MouseControler mouseControler = new MouseControler();
		eventDispatcher.addHMotionEventListener(mouseControler);
		eventDispatcher.addHMouseEventListener(mouseControler);
		
		KeyControler keyControler = new KeyControler();
		eventDispatcher.addHKeyEventListener(keyControler);
		
		// init ui
		ComputerRemoteFrame computerRemoteFrame = new ComputerRemoteFrame();
		
		if (!commandLineAdapter.isCommanded(Main.COMMAND_AUTO_START)) {
			computerRemoteFrame.setVisible(true);
		}
		
		MotionEventHandler.getInstance();
		ScreenHunterServer.getInstance();
	}
}
