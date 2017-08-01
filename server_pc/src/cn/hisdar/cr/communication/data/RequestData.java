package cn.hisdar.cr.communication.data;

public class RequestData extends AbstractData {

	@Override
	public int getDataType() {
		return DATA_TYPE_REQUEST;
	}

	public int requestDataType = DATA_TYPE_REQUEST;
	
	public RequestData() {
		
	}
	
	public RequestData(int dataType) {
		this.requestDataType = dataType;
	}
	
	public int getRequestDataType() {
		return requestDataType;
	}

	public void setRequestDataType(int requestDataType) {
		this.requestDataType = requestDataType;
	}

	@Override
	public boolean decode(byte[] data) {
		requestDataType = bytesToInt(data);
		return true;
	}

	@Override
	public byte[] encode() {
		return intToBytes(requestDataType);
	}

}
