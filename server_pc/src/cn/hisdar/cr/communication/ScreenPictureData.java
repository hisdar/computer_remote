package cn.hisdar.cr.communication;

import java.awt.Point;
import java.awt.image.BufferedImage;

public class ScreenPictureData extends AbstractDataType {

	private BufferedImage screenImage;
	private Point mouseLocation;
	public byte[] screenPictureData = null;

    public void setScreenPictureData(byte[] data) {
        screenPictureData = data;
    }

    public byte[] getScreenPictureData() {
        return screenPictureData;
    }

    @Override
    public int getDataType() {
        return DATA_TYPE_SCREEN_PICTURE;
    }

    @Override
    public byte[] encode() {
        return screenPictureData;
    }

    @Override
    public boolean decode(byte[] data) {
        screenPictureData = data;
        return true;
    }

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
