package cn.hisdar.cr.communication.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by Hisdar on 2017/8/9.
 */

public class KeyEventData extends AbstractData {

    private int keyCode;
    private int keyAction;

    public KeyEventData() {
        keyCode = 0;
        keyAction = 0;
    }

    public KeyEventData(int code, int action) {
        keyAction = action;
        keyCode = code;
    }

    @Override
    public int getDataType() {
        return DATA_TYPE_KEY_EVENT;
    }

    @Override
    public boolean decode(byte[] data) {
    	
    	ByteArrayInputStream in = new ByteArrayInputStream(data);
    	byte[] intBytes = new byte[4];
    	
    	in.read(intBytes, 0, 4);
    	keyCode = AbstractData.bytesToInt(intBytes);
    	
    	in.read(intBytes, 0, 4);
    	keyAction = AbstractData.bytesToInt(intBytes);
    	
        return true;
    }

    @Override
    public byte[] encode() {
    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	try {
    		out.write(AbstractData.intToBytes(keyCode));
    		out.write(AbstractData.intToBytes(keyAction));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
        return out.toByteArray();
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyAction() {
        return keyAction;
    }

    public void setKeyAction(int keyAction) {
        this.keyAction = keyAction;
    }

    @Override
    public String toString() {
        return "KeyEventData{" +
                "keyCode=" + keyCode +
                ", keyAction=" + keyAction +
                '}';
    }
}


