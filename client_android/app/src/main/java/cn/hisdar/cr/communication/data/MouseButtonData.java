package cn.hisdar.cr.communication.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Hisdar on 2017/8/4.
 */

public class MouseButtonData extends AbstractData {

    public static final int BUTTON1 = 1;
    public static final int BUTTON2 = 2;
    public static final int BUTTON3 = 3;

    private int buttioID;
    private int actionCode;

    public MouseButtonData(int buttonID, int actionCode) {
        this.buttioID = buttonID;
        this.actionCode = actionCode;
    }

    public MouseButtonData() {

    }

    public int getButtioID() {
        return buttioID;
    }

    public void setButtioID(int buttioID) {
        this.buttioID = buttioID;
    }

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }

    @Override
    public String toString() {
        return "MouseButtonData{" +
                "buttioID=" + buttioID +
                ", actionCode=" + actionCode +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MouseButtonData that = (MouseButtonData) o;

        if (buttioID != that.buttioID) return false;
        return actionCode == that.actionCode;

    }

    @Override
    public int hashCode() {
        int result = buttioID;
        result = 31 * result + actionCode;
        return result;
    }

    @Override
    public int getDataType() {
        return DATA_TYPE_MOUSE_BUTTON;
    }

    @Override
    public boolean decode(byte[] data) {

        if (data.length != 8) {
            return false;
        }

        ByteArrayInputStream in = new ByteArrayInputStream(data);
        byte[] byteData = new byte[4];

        in.read(byteData, 0, 4);
        buttioID = bytesToInt(byteData);

        in.read(byteData, 0, 4);
        actionCode = bytesToInt(byteData);

        return true;
    }

    @Override
    public byte[] encode() {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            out.write(intToBytes(buttioID));
            out.write(intToBytes(actionCode));
        } catch (IOException e) {
            return null;
        }

        return out.toByteArray();
    }
}
