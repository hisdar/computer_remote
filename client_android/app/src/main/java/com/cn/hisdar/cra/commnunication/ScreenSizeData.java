package com.cn.hisdar.cra.commnunication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Hisdar on 2017/7/22.
 */

public class ScreenSizeData extends AbstractDataType {

    public int screenWidth = 0;
    public int screenHeight = 0;

    public ScreenSizeData(int widht, int height) {
        this.screenHeight = height;
        this.screenWidth = widht;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    @Override
    public int getDataType() {
        return DATA_TYPE_SCREEN_SIZE;
    }

    @Override
    public byte[] encode() throws IOException {

        ByteArrayOutputStream baOut = new ByteArrayOutputStream();
        baOut.write(intToBytes(screenWidth));
        baOut.write(intToBytes(screenHeight));

        return baOut.toByteArray();
    }

    @Override
    public boolean decode(byte[] data) {
        if (data.length != 8) {
            return false;
        }

        ByteArrayInputStream baIn = new ByteArrayInputStream(data);
        byte[] intBytes = new byte[4];

        baIn.read(intBytes, 0, 4);
        screenWidth = bytesToInt(intBytes);

        baIn.read(intBytes, 0, 4);
        screenHeight = bytesToInt(intBytes);

        return true;
    }
}
