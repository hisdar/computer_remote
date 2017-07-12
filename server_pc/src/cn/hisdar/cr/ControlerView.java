package cn.hisdar.cr;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import cn.hisdar.cr.communication.ScreenSizeEventListener;
import cn.hisdar.cr.event.EventDispatcher;
import cn.hisdar.cr.event.HMotionEvent;
import cn.hisdar.cr.event.HMotionEventListener;
import cn.hisdar.cr.event.Pointer;

public class ControlerView extends JPanel implements HMotionEventListener, ScreenSizeEventListener {

	private static final Color[] COLOR_LIST = {
			new Color(0xB22222),
			new Color(0x32CD32),
			new Color(0x0000CD),
			new Color(0x90EE90),
			new Color(0x00C5CD),
			new Color(0xF0FFF0),
			new Color(0xFFA500),
			new Color(0x00CED1),
			new Color(0x458B00),
			new Color(0xFFDAB9),
	};
	
	private EventDispatcher eventDispatcher = null;
	private BufferedImage touchEventImage = null;
	private Point screenSize = null;
	
	private ArrayList<HMotionEvent> motionEvents = null;
	private HMotionEvent currentMotionEvent = null;
	private TouchEventDrawTherad touchEventDrawTherad = null;
	
	public ControlerView() {
		
		motionEvents = new ArrayList<>();
		
		eventDispatcher = EventDispatcher.getInstance();
		eventDispatcher.addHMotionEventListener(this);
		eventDispatcher.addScreenSizeEventListener(this);
		
		touchEventDrawTherad = new TouchEventDrawTherad();
		touchEventDrawTherad.start();
	}

	@Override
	public void motionEvent(HMotionEvent event) {
		currentMotionEvent = event;
		touchEventDrawTherad.interrupt();
		
		switch (event.getAction()) {
		case HMotionEvent.ACTION_UP:
			while (motionEvents.size() > 0) {
				motionEvents.remove(0);
			}
			
			motionEvents.add(event);
			
			break;
		case HMotionEvent.ACTION_DOWN:
			motionEvents.add(event);
			
			break;
		default:
			motionEvents.add(event);
			break;
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		if (touchEventImage == null) {
			return;
		}
		
		int startIndexX = (getWidth() - touchEventImage.getWidth()) / 2;
		int startIndexY = (getHeight() - touchEventImage.getHeight()) / 2;
		
		g.drawImage(touchEventImage, startIndexX, startIndexY, null);
	}
	
	// get image size
	private Point getTouchEventImageSize(Point screenSize) {
		double screenWidthHeightRate = screenSize.x * 1.0 / screenSize.y;
		double viewWidthHeightRate = getWidth() * 1.0 / getHeight();
		
		Point imageSize = new Point();
		if (screenWidthHeightRate < viewWidthHeightRate) {
			// 
			imageSize.y = getHeight();
			imageSize.x = (int)(getHeight() * screenWidthHeightRate);
		} else {
			imageSize.x = getWidth();
			imageSize.y = (int)(getWidth() / screenWidthHeightRate);
		}
		
		return imageSize;
	}

	@Override
	public void screenSizeEvent(Point screenSize) {
		this.screenSize = screenSize;
	}
	
	private BufferedImage getTouchEventImage() {
		
		if (screenSize == null) {
			return null;
		}
		
		Point imageSize = getTouchEventImageSize(screenSize);
		if (touchEventImage == null || touchEventImage.getHeight() != imageSize.y || touchEventImage.getWidth() != imageSize.x) {
			touchEventImage = new BufferedImage(imageSize.x, imageSize.y, BufferedImage.TYPE_INT_ARGB);
		}
		
		return touchEventImage;
	}
	
	private Point coordinateRransformation(Pointer srcPointer, Point screenSize, Point imageSize) {
		double xRate = imageSize.getX() / screenSize.getX();
		double yRate = imageSize.getY() / screenSize.getY();
		
		Point outputPoint = new Point();
		outputPoint.x = (int)(srcPointer.getX() * xRate);
		outputPoint.y = (int)(srcPointer.getY() * yRate);
		
		return outputPoint;
	}
	
	private class TouchEventDrawTherad extends Thread {
		
		public void run() {
			while (true) {
				
				try {
					sleep(100000);
				} catch (InterruptedException e) {}
				
				if (screenSize == null) {
					continue;
				}
				
				touchEventImage = getTouchEventImage();
				if (touchEventImage == null) {
					continue;
				}
				
				if (currentMotionEvent == null) {
					continue;
				}
				
				ArrayList<Pointer> touchPointers = currentMotionEvent.getPointers();
				Graphics imageGraphics = touchEventImage.getGraphics();
				
				imageGraphics.setColor(Color.BLACK);
				imageGraphics.fillRect(0, 0, touchEventImage.getWidth(), touchEventImage.getHeight());
				
				Point imageSize = new Point(touchEventImage.getWidth(), touchEventImage.getHeight());
				for (int i = 0; i < touchPointers.size(); i++) {
					if (i < COLOR_LIST.length) {
						imageGraphics.setColor(COLOR_LIST[i]);
					} else {
						imageGraphics.setColor(COLOR_LIST[COLOR_LIST.length - 1]);
					}
					
					Point localPoint = coordinateRransformation(touchPointers.get(i), screenSize, imageSize);

					//imageGraphics.fillArc(localPoint.x, localPoint.y, 5, 5, 5, 5);
					imageGraphics.fillRoundRect(localPoint.x, localPoint.y, 8, 8, 4, 4);
					//imageGraphics.fillOval(localPoint.x, localPoint.y, 5, 5);
					//imageGraphics.fillRect((int)touchPointers.get(i).x, (int)touchPointers.get(i).y, 5, 5);
				}
				
				// draw border to touch eventImage
				imageGraphics.setColor(Color.WHITE);
				imageGraphics.drawRect(0, 0, imageSize.x - 1, imageSize.y - 1);
				
				repaint();
			}
		}
	}
}
