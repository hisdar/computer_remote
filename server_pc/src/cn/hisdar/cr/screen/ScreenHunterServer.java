package cn.hisdar.cr.screen;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import cn.hisdar.cr.communication.CRCSManager;
import cn.hisdar.cr.communication.ScreenPictureData;
import cn.hisdar.cr.controler.GestureListener;
import cn.hisdar.cr.controler.GestureParser;
import cn.hisdar.cr.event.EventDispatcher;
import cn.hisdar.cr.event.HMotionEvent;
import cn.hisdar.cr.event.HMotionEventListener;
import cn.hisdar.lib.log.HLog;

public class ScreenHunterServer implements HMotionEventListener, GestureListener {

	private static ScreenHunterServer screenHunterServer = null;
	private ArrayList<ScreenHunterListener> listeners = null;
	private ScreenHunterThread screenHunterThread = null;
	private boolean isStop = false;

	private boolean sendFlag = false;
	private Thread screenPictureSendThread = null;
	private double pinchSize = 0;
	
	private ScreenHunterServer() {
		
		GestureParser gestureParser = GestureParser.getInstance();
		gestureParser.addGestureListener(this);
		
		EventDispatcher eventDispatcher = EventDispatcher.getInstance();
		eventDispatcher.addHMotionEventListener(this);
		
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
    
    /**
     * 按比例裁剪图片
     * 
     * @param source
     *            待处理的图片流
     * @param startX
     *            开始x坐标
     * @param startY
     *            开始y坐标
     * @param endX
     *            结束x坐标
     * @param endY
     *            结束y坐标
     * @return
     */
    public static BufferedImage crop(BufferedImage source, int startX, int startY, int endX, int endY) {
       int width = source.getWidth();
       int height = source.getHeight();

       if (startX <= -1) {
          startX = 0;
       }
       if (startY <= -1) {
          startY = 0;
       }
       if (endX <= -1) {
          endX = width - 1;
       }
       if (endY <= -1) {
          endY = height - 1;
       }
       BufferedImage result = new BufferedImage(endX, endY , source.getType());
       for (int y = startY; y < endY+startY; y++) {
          for (int x = startX; x < endX+startX; x++) {
             int rgb = source.getRGB(x, y);
             result.setRGB(x - startX, y - startY, rgb);
          }
       }
       return result;
    }
    
    public ScreenPictureData getScreenHunterData() {
    	int screenWidth = ((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().width);
		int screenHeight = ((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().height); 
		
		BufferedImage screenImage = getScreenShot(0, 0, screenWidth, screenHeight);
		Point point = MouseInfo.getPointerInfo().getLocation();
		
		Graphics graphics = screenImage.getGraphics();
		graphics.setColor(Color.RED);
		graphics.drawOval(point.x - 50, point.y - 50, 100, 100);
		
		// get picture size
		int imageHeight = 1920 - (int)(pinchSize * 10 * 2);
		int imageWidth = 1080 - (int)(pinchSize * 10 * 2);
		
		imageHeight = imageHeight > screenImage.getHeight() ? screenImage.getHeight() : imageHeight;
		imageWidth = imageWidth > screenImage.getWidth() ? screenImage.getWidth() : imageWidth;
		
		// get start index and end index
		int startX = point.x - imageWidth / 2;
		int startY = point.y - imageHeight / 2;
		
		int endX = startX + imageWidth;
		int endY = startY + imageHeight;
		
		if (startX < 0) {
			startX = 0;
			endX = imageWidth;
		}
		
		if (startY < 0) {
			startY = 0;
			endY = imageHeight;
		}
		
		if (endX > screenImage.getWidth()) {
			endX = screenImage.getWidth();
			startX = endX - imageWidth;
		}
		
		if (endY > screenImage.getHeight()) {
			endY = screenImage.getHeight();
			startY = endY - imageHeight;
		}
		
		HLog.il("x=" + startX + ", y=" + startY + ",width=" + screenWidth + ",height=" + screenHeight);
		
		//screenImage = crop(screenImage, startX, startY, imageWidth, imageHeight);
		
		ScreenPictureData screenHunterData = new ScreenPictureData();
		screenHunterData.setScreenImage(screenImage);
		screenHunterData.setMouseLocation(point);
		
		return screenHunterData;
    }
	
	private class ScreenHunterThread extends Thread {
		
		public void run() {
			
			HLog.il("ScreenHunterThread start");
			
//			while (!isStop) {
//				
//				ScreenPictureData screenHunterData = getScreenHunterData();
//				for (int i = 0; i < listeners.size(); i++) {
//					listeners.get(i).screenPictureChangeEvent(screenHunterData);
//				}
//				
//				try {
//					sleep(100);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			HLog.il("ScreenHunterThread exit");
		}
	}

	@Override
	public void motionEvent(HMotionEvent event) {
		sendFlag = true;
		if (screenPictureSendThread == null || !screenPictureSendThread.isAlive()) {
			screenPictureSendThread = new Thread(new ScreenPictureSendRunnable());
			screenPictureSendThread.start();
		}
	}

	@Override
	public void pinckBiggerEvemt(double value) {
		pinchSize += value;
	}

	@Override
	public void pinckSmallerEvemt(double value) {
		pinchSize -= value;
		if (pinchSize < 0) {
			pinchSize = 0;
		}
	}
	
	private class ScreenPictureSendRunnable implements Runnable {

		@Override
		public void run() {
			while (sendFlag == true) {
				sendFlag = false;
				
				ScreenPictureData shData = ScreenHunterServer.getInstance().getScreenHunterData();
				CRCSManager.getInstance().screenPictureChangeEvent(shData);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

	
}
