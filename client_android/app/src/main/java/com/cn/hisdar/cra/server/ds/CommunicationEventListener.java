package com.cn.hisdar.cra.server.ds;

import com.cn.hisdar.cra.server.HScreenPiture;

/**
 * Created by Hisdar on 2017/3/12.
 */
public interface CommunicationEventListener {

    public void screenPictureEvent(byte[] data);
}
