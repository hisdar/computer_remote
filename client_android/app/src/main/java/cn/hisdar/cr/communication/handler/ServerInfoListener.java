package cn.hisdar.cr.communication.handler;

import cn.hisdar.cr.communication.data.ServerInfoData;

/**
 * Created by Hisdar on 2017/8/1.
 */

public interface ServerInfoListener {
    public void serverInfoEvent(ServerInfoData serverInfoData);
}
