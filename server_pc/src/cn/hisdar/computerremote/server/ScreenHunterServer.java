package cn.hisdar.computerremote.server;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderWriterSpi;

import cn.hisdar.lib.log.HLog;

public class ScreenHunterServer {

	private static ScreenHunterServer screenHunterServer = null;
	private ArrayList<ScreenHunterListener> listeners = null;
	private ScreenHunterThread screenHunterThread = null;
	private boolean isStop = false;
	
	private ScreenHunterServer() {
		listeners = new ArrayList<>();
		startServer();
	}
	
	public static ScreenHunterServer getInstance() {
		if (screenHunterServer == null) {
			synchronized (ScreenHunterServer.class) {
				if (screenHunterServer == null) {
					screenHunterServer = new ScreenHunterServer();
				}
			}
		}
		
		return screenHunterServer;
	}
	
	
	public void addScreenHunterListener(ScreenHunterListener l) {
		for (int i = 0; i < listeners.size(); i++) {
			if (listeners.get(i) == l) {
				return;
			}
		}
		
		listeners.add(l);
	}
	
	public void removeScreenHunterListener(ScreenHunterListener l) {
		for (int i = 0; i < listeners.size(); i++) {
			if (listeners.get(i) == l) {
				listeners.remove(i);
				return;
			}
		}
	}
	
	public void startServer() {
		if (screenHunterThread == null) {
			isStop = false;
			screenHunterThread = new ScreenHunterThread();
			screenHunterThread.start();
		} else {
			return;
		}
	}
	
	public void stopServer() {
		if (screenHunterThread != null) {
			isStop = true;
			screenHunterServer = null;
		}
	}
	
    public BufferedImage getScreenShot(int x, int y, int width, int height) {
        BufferedImage bfImage = null;
        try {
            Robot robot = new Robot();
            bfImage = robot.createScreenCapture(new Rectangle(x, y, width, height));
        } catch (AWTException e) {
            e.printStackTrace();
        }
        return bfImage;
    }
	
	private class ScreenHunterThread extends Thread {
		
		public void run() {
			
			HLog.il("ScreenHunterThread start");
			int screenWidth = ((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().width);
			int screenHeight = ((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().height); 
			
			while (!isStop) {
				BufferedImage screenImage = getScreenShot(0, 0, screenWidth, screenHeight);
				ScreenHunterData screenHunterData = new ScreenHunterData();
				screenHunterData.setScreenImage(screenImage);
				screenHunterData.setMouseLocation(MouseInfo.getPointerInfo().getLocation());
				
				String readFormats[] = ImageIO.getReaderFormatNames();
				for (String string : readFormats) {
					System.out.println(string);
				}
				
				try {
					ImageIO.write(screenImage, "bmp", new File("D:/temp/screen.png"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (int i = 0; i < listeners.size(); i++) {
					listeners.get(i).screenPictureChangeEvent(screenHunterData);
				}
				
				try {
					sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			HLog.il("ScreenHunterThread exit");
		}
	}
}
