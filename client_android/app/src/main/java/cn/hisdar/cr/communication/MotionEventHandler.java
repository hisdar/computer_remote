package cn.hisdar.cr.communication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import cn.hisdar.cr.communication.handler.HMotionEvent;
/**
 * Created by Hisdar on 2017/7/22.
 */

public class MotionEventHandler extends AbstractDataHandler {

    public HMotionEvent motionEvent = null;

    public void setMotionEvent(HMotionEvent motionEvent) {
        this.motionEvent = motionEvent;
    }

    public HMotionEvent getMotionEvent() {
        return motionEvent;
    }

    @Override
    public int getDataType() {
        return 0;
    }

    @Override
    public byte[] encode() {

        ByteArrayOutputStream byOut = new ByteArrayOutputStream();
		try {
	        byOut.write(intToBytes(motionEvent.getAction()));
	        byOut.write(intToBytes(motionEvent.getActionIndex()));
	        byOut.write(intToBytes(motionEvent.getButtonState()));
	        byOut.write(intToBytes(motionEvent.getMetaState()));
	        byOut.write(intToBytes(motionEvent.getFlags()));
	        byOut.write(intToBytes(motionEvent.getEdgeFlags()));
	        byOut.write(intToBytes(motionEvent.getPointerCount()));
	        byOut.write(intToBytes(motionEvent.getHistorySize()));
	        byOut.write(longToBytes(motionEvent.getEventTime()));
	        byOut.write(longToBytes(motionEvent.getDownTime()));
	        byOut.write(intToBytes(motionEvent.getDeviceId()));
	        byOut.write(intToBytes(motionEvent.getSource()));
	
	        for (int i = 0; i < motionEvent.getPointerCount(); i++) {
	            byOut.write(intToBytes((int)motionEvent.getX(i)));
	            byOut.write(intToBytes((int)motionEvent.getY(i)));
	            byOut.write(intToBytes(motionEvent.getToolType(i)));
	        }
		} catch (Exception e) {
			
		}
        return byOut.toByteArray();
    }

    @Override
    public boolean decode(byte[] data) {
        ByteArrayInputStream byIn = new ByteArrayInputStream(data);
        byte[] intBytes = new byte[4];
        byte[] longBytes = new byte[8];

        byIn.read(intBytes, 0, 4);
        motionEvent.setAction(bytesToInt(intBytes));

        byIn.read(intBytes, 0, 4);
        motionEvent.setActionIndex(bytesToInt(intBytes));

        byIn.read(intBytes, 0, 4);
        motionEvent.setButtonState(bytesToInt(intBytes));

        byIn.read(intBytes, 0, 4);
        motionEvent.setMetaState(bytesToInt(intBytes));

        byIn.read(intBytes, 0, 4);
        motionEvent.setFlags(bytesToInt(intBytes));

        byIn.read(intBytes, 0, 4);
        motionEvent.setEdgeFlags(bytesToInt(intBytes));

        byIn.read(intBytes, 0, 4);
        motionEvent.setPointerCount(bytesToInt(intBytes));

        byIn.read(intBytes, 0, 4);
        motionEvent.setHistorySize(bytesToInt(intBytes));

        byIn.read(longBytes, 0, 8);
        motionEvent.setEventTime(bytesToLong(longBytes));

        byIn.read(longBytes, 0, 8);
        motionEvent.setDownTime(bytesToLong(longBytes));

        byIn.read(intBytes, 0, 4);
        motionEvent.setDeviceId(bytesToInt(intBytes));

        byIn.read(intBytes, 0, 4);
        motionEvent.setSource(bytesToInt(intBytes));

        for (int i = 0; i < motionEvent.getPointerCount(); i++) {
            byIn.read(intBytes, 0, 4);
            motionEvent.setX(i, bytesToInt(intBytes));

            byIn.read(intBytes, 0, 4);
            motionEvent.setY(i, bytesToInt(intBytes));

            byIn.read(intBytes, 0, 4);
            motionEvent.setToolType(i, bytesToInt(intBytes));
        }

        return true;
    }
}
