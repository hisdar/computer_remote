package cn.hisdar.cr.communication.handler;

import cn.hisdar.cr.communication.data.AbstractData;

/**
 * Created by Hisdar on 2017/7/26.
 */

public class CommonHandler extends AbstractDataHandler {

    private byte[] data = null;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public int getDataType() {
        return AbstractData.DATA_TYPE_COMMON_DATA;
    }

    @Override
    public boolean decode(byte[] data) {
        this.data = data;
        
        
        
        return true;
    }
}
