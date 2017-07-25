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
     * �������ü�ͼƬ
     * 
     * @param source
     *            �������ͼƬ��
     * @param startX
     *            ��ʼx����
     * @param startY
     *            ��ʼy����
     * @param endX
     *            ����x����
     * @param endY
     *            ����y����
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
    
    private Rectangle getScreenPictureRect(Point centerPoint) {
    	// 1. �����ֻ��ķֱ��ʱ�������Ļ�ĳߴ磬���һ���ߴ磬����ߴ�ı������ֻ���Ļ�ı���������ߴ��ͼƬ�����������µ�����Ļ��ͼƬ
    	// get picture size
		// �õ��Ե���Ļ�ߴ磬ͨ���ֻ��ķֱ��ʼ��㣬ȡС��һ��
		int phoneScreenWidth = 1080;
		int phoneScreenHeight = 1920;
		double phoneScreenRate = 1.0 * phoneScreenHeight / phoneScreenWidth;

		int pcScreenWidth = 1280;
		int pcScreenHeight = 800;
		
		int imageWidth = pcScreenWidth;
		int imageHeight = (int)(imageWidth * phoneScreenRate);

		if (imageHeight < pcScreenHeight) {
			imageHeight = pcScreenHeight;
			imageWidth = (int)(imageHeight / phoneScreenRate);
		}
    	
    	// ���ݷŴ�ϵ�����ٴμ���ͼ��ߴ�
		int enlargeSize = (int)(pinchSize * 10 * 2);
		
		imageWidth = imageWidth - enlargeSize;
		imageWidth = imageWidth < 0 ? (10 * 2) : imageWidth;
		
		//imageHeight = imageHeight - (int)(enlargeSize * phoneScreenRate);
		imageHeight = (int)(imageWidth * phoneScreenRate);
		imageHeight = imageHeight < 0 ? (10 * 2) : imageHeight;

    	
		HLog.dl("enlargeSize=" + enlargeSize + ", phoneScreenRate" + phoneScreenRate);
		HLog.dl("imageWidth=" + imageWidth + ", imageHeight=" + imageHeight);
		
		// �������ĵ��ͼƬֻ�棬����ͼƬ����ʼλ�úͽ���Ϊֹ
		int startX = centerPoint.x - imageWidth / 2;
		int startY = centerPoint.y - imageHeight / 2;
		
		HLog.dl("startX=" + startX + ", startY=" + startY);
		
		startX = startX < 0 ? 0 : startX;
		startY = startY < 0 ? 0 : startY;
		int endX = startX + imageWidth;
		int endY = startY + imageHeight;
		
		HLog.dl("startX=" + startX + ", startY=" + startY);
		HLog.dl("endX=" + endX + ", endY=" + endY);
		
		// ��Ҫ��ȡ��ͼƬ�ߴ糬����ԴͼƬ�ĳߴ��ʱ�򣬽���ƽ�Ƶ���
		if (endX > pcScreenWidth) {
			int backX = endX - pcScreenWidth;
			backX = backX > startX ? startX : backX;
			startX -= backX;
			endX = startX + imageWidth;
		}
		
		if (endY > pcScreenHeight) {
			int backY = endY - pcScreenHeight;
			backY = backY > startY ? startY : backY;
			startY -= backY;
			endY = startY + imageHeight;
		}
		
		HLog.dl("startX=" + startX + ", startY=" + startY);
		HLog.dl("endX=" + endX + ", endY=" + endY);
		
		// ��� ���ǳ���ԴͼƬ�ߴ磬�Ͱ���ԭͼƬ�ߴ���
		endX = endX > pcScreenWidth ? pcScreenWidth : endX;
		endY = endY > pcScreenHeight ? pcScreenHeight : endY;
		
		Rectangle rectangle = new Rectangle(startX, startY, endX - startX, endY - startY);
		return rectangle;
    }
    
    public ScreenPictureData getScreenHunterData() {
    	int screenWidth = ((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().width);
		int screenHeight = ((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().height); 
		
		BufferedImage screenImage = getScreenShot(0, 0, screenWidth, screenHeight);
		Point point = MouseInfo.getPointerInfo().getLocation();
		
		Graphics g = screenImage.getGraphics();
		
		g.setColor(Color.RED);
		int[] xPoints = {point.x, point.x + 30, point.x + 15, point.x + 15};
		int[] yPoints = {point.y, point.y + 15, point.y + 15, point.y + 30};
		g.fillPolygon(xPoints, yPoints, 4);
		
		g.setColor(Color.BLACK);
		g.drawLine(point.x, point.y, point.x + 30, point.y + 15);
		g.drawLine(point.x, point.y, point.x + 15, point.y + 30);
		
		g.drawLine(point.x + 15, point.y + 30, point.x + 15, point.y + 15);
		g.drawLine(point.x + 30, point.y + 15, point.x + 15, point.y + 15);
		
		g.setColor(Color.GRAY);
		g.drawLine(point.x, point.y, point.x + 15, point.y + 15);
		
		Rectangle rect = getScreenPictureRect(point);
		HLog.il(rect);
		screenImage = crop(screenImage, rect.x, rect.y, rect.width, rect.height);
		
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
