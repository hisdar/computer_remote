package com.cn.hisdar.cra.commnunication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import android.view.MotionEvent;

import com.cn.hisdar.cra.common.Global;

/**
 * Created by Hisdar on 2017/7/22.
 */

public class MotionEventData extends AbstractDataType {

    public MotionEvent motionEvent = null;

    public void setMotionEvent(MotionEvent motionEvent) {
        this.motionEvent = motionEvent;
    }

    public MotionEvent getMotionEvent() {
        return motionEvent;
    }

    @Override
    public int getDataType() {
        return 0;
    }

    @Override
    public byte[] encode() throws IOException {

        ByteArrayOutputStream byOut = new ByteArrayOutputStream();
        int action = motionEvent.getAction();
        int actionIndex = motionEvent.getActionIndex();
        int buttonState = motionEvent.getButtonState();
        int metaState = motionEvent.getMetaState();
        int flags = motionEvent.getFlags();
        int edgeFlags = motionEvent.getEdgeFlags();
        int pointCount = motionEvent.getPointerCount();
        int historySize = motionEvent.getHistorySize();
        long eventTime = motionEvent.getEventTime();
        long downTime = motionEvent.getDownTime();
        int deviceId = motionEvent.getDeviceId();
        int source = motionEvent.getSource();

        byOut.write(intToBytes(action));
        byOut.write(intToBytes(actionIndex));
        byOut.write(intToBytes(buttonState));
        byOut.write(intToBytes(metaState));
        byOut.write(intToBytes(flags));
        byOut.write(intToBytes(edgeFlags));
        byOut.write(intToBytes(pointCount));
        byOut.write(intToBytes(historySize));
        byOut.write(longToBytes(eventTime));
        byOut.write(longToBytes(downTime));
        byOut.write(intToBytes(deviceId));
        byOut.write(intToBytes(source));

        for (int i = 0; i < motionEvent.getPointerCount(); i++) {

            float x = motionEvent.getX(i);
            float y = motionEvent.getY(i);
            int toolType = motionEvent.getToolType(i);

            byOut.write(intToBytes((int)x));
            byOut.write(intToBytes((int)y));
            byOut.write(intToBytes(toolType));
        }

        return byOut.toByteArray();
    }

    @Override
    public boolean decode(byte[] data) {
        ByteArrayInputStream byIn = new ByteArrayInputStream(data);
        byte[] intBytes = new byte[4];
        byte[] longBytes = new byte[8];

        byIn.read(intBytes, 0, 4);
        int action = bytesToInt(intBytes);

        byIn.read(intBytes, 0, 4);
        int actionIndex = bytesToInt(intBytes);

        byIn.read(intBytes, 0, 4);
        int buttonState = bytesToInt(intBytes);

        byIn.read(intBytes, 0, 4);
        int metaState = bytesToInt(intBytes);

        byIn.read(intBytes, 0, 4);
        int flags = bytesToInt(intBytes);

        byIn.read(intBytes, 0, 4);
        int edgeFlags = bytesToInt(intBytes);

        byIn.read(intBytes, 0, 4);
        int pointCount = bytesToInt(intBytes);

        byIn.read(intBytes, 0, 4);
        int historySize = bytesToInt(intBytes);

        byIn.read(longBytes, 0, 8);
        long eventTime = bytesToLong(longBytes);

        byIn.read(longBytes, 0, 8);
        long downTime = bytesToLong(longBytes);

        byIn.read(intBytes, 0, 4);
        int deviceId = bytesToInt(intBytes);

        byIn.read(intBytes, 0, 4);
        int source = bytesToInt(intBytes);

        for (int i = 0; i < pointCount; i++) {
            byIn.read(intBytes, 0, 4);
            int x = bytesToInt(intBytes);

            byIn.read(intBytes, 0, 4);
            int y = bytesToInt(intBytes);

            byIn.read(intBytes, 0, 4);
            int toolType = bytesToInt(intBytes);
        }

        return true;
    }
}
