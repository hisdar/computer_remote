package cn.hisdar.cr.communication.handler;

import cn.hisdar.cr.communication.data.ResponseData;

public interface ResponseListener {

	public void responseEvent(ResponseData responseData);
}
