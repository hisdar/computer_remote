package cn.hisdar.cr.communication.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ScreenSizeData extends AbstractData {

	private int width = 0;
	private int height = 0;

	public ScreenSizeData() {

	}

	public ScreenSizeData(int widht, int height) {
		this.width = widht;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public int getDataType() {
		return DATA_TYPE_SCREEN_SIZE;
	}

	@Override
	public boolean decode(byte[] data) {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
		byte[] bytesData = new byte[4];

		byteArrayInputStream.read(bytesData, 0, 4);
		width = bytesToInt(bytesData);

		byteArrayInputStream.read(bytesData, 0, 4);
		height = bytesToInt(bytesData);

		return false;
	}

	@Override
	public byte[] encode() {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			byteArrayOutputStream.write(intToBytes(width));
			byteArrayOutputStream.write(intToBytes(height));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return byteArrayOutputStream.toByteArray();
	}

	@Override
	public String toString() {
		return "ScreenSizeData [width=" + width + ", height=" + height + "]";
	}

}
