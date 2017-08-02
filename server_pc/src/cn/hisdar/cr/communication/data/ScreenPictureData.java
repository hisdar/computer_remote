package cn.hisdar.cr.communication.data;

public class ScreenPictureData extends AbstractData {

	private byte[] pictureData = null;
	
	public ScreenPictureData() {
		
	}
	
	public ScreenPictureData(byte[] screenData) {
		this.pictureData = screenData;
	}
	
	@Override
	public int getDataType() {
		return DATA_TYPE_SCREEN_PICTURE;
	}

	@Override
	public boolean decode(byte[] data) {
		pictureData = data;
		return true;
	}

	@Override
	public byte[] encode() {
		return pictureData;
	}

}
