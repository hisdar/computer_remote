package cn.hisdar.cr.communication;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import cn.hisdar.lib.log.HLog;

public class ScreenPictureData extends AbstractDataType {

	private BufferedImage screenImage;
	private Point mouseLocation;

    @Override
    public int getDataType() {
        return DATA_TYPE_SCREEN_PICTURE;
    }

    @Override
    public byte[] encode() {
    	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    	
    	try {
			ImageIO.write(screenImage, "png", byteArrayOutputStream);
		} catch (IOException e) {
			HLog.el(e);
			return null;
		}
    	
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public boolean decode(byte[] data) {
    	ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
    	try {
			screenImage = ImageIO.read(byteArrayInputStream);
		} catch (IOException e) {
			HLog.el(e);
			return false;
		}
    	
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
