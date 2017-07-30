package cn.hisdar.cr.communication;

/**
 * Created by Hisdar on 2017/7/22.
 */

public class ScreenPictureHandler extends AbstractDataHandler {

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
}
