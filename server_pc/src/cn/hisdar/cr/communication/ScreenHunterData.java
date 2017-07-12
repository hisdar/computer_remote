package cn.hisdar.cr.communication;

import java.awt.Point;
import java.awt.image.BufferedImage;

public class ScreenHunterData {

	private BufferedImage screenImage;
	private Point mouseLocation;


	public BufferedImage getScreenImage() {
		return screenImage;
	}

	public void setScreenImage(BufferedImage screenImage) {
		this.screenImage = screenImage;
	}

	public Point getMouseLocation() {
		return mouseLocation;
	}

	public void setMouseLocation(Point mouseLocation) {
		this.mouseLocation = mouseLocation;
	}
	
	
}
