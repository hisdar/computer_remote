package com.cn.hisdar.cra.commnunication;

/**
 * Created by Hisdar on 2017/7/22.
 */

public class ScreenPictureData extends AbstractDataType {

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
