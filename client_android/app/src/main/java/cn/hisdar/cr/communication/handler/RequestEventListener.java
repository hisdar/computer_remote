package cn.hisdar.cr.communication.handler;

import cn.hisdar.cr.communication.data.RequestData;

public interface RequestEventListener {

	public void requestEvent(RequestData requestData);
}
