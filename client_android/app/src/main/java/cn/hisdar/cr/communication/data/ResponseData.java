package cn.hisdar.cr.communication.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ResponseData extends AbstractData {

	private static final String TAG = "ResponseData";

	private long writeTime;
	private int dataLength;
	private int responseDataType;
	
	public ResponseData() {
		
	}
	
	public ResponseData(long wirteTime, int dataLength, int responseDataType) {
		this.writeTime = wirteTime;
		this.dataLength = dataLength;
		this.responseDataType = responseDataType;
	}
	
	public long getWriteTime() {
		return writeTime;
	}

	public void setWriteTime(long writeTime) {
		this.writeTime = writeTime;
	}

	public int getDataLength() {
		return dataLength;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public int getResponseDataType() {
		return responseDataType;
	}

	public void setResponseDataType(int responseDataType) {
		this.responseDataType = responseDataType;
	}

	@Override
	public String toString() {
		return "ResponseData [writeTime=" + writeTime + ", dataLength=" + dataLength + ", responseDataType="
				+ responseDataType + "]";
	}

	@Override
	public int getDataType() {
		return DATA_TYPE_RESPONSE;
	}

	@Override
	public boolean decode(byte[] data) {
		
		if (data == null) {
			return false;
		}
		
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		
		byte[] intBytes = new byte[4];
		byte[] longBytes = new byte[8];
		
		in.read(longBytes, 0, 8);
		writeTime = AbstractData.bytesToLong(longBytes);
		
		in.read(intBytes, 0, 4);
		dataLength = AbstractData.bytesToInt(intBytes);
		
		in.read(intBytes, 0, 4);
		responseDataType = AbstractData.bytesToInt(intBytes);
		
		return true;
	}

	@Override
	public byte[] encode() {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] bytesArray = null;
		try {
			bytesArray = AbstractData.longToBytes(writeTime);
			out.write(bytesArray);
			
			out.write(AbstractData.intToBytes(dataLength));
			out.write(AbstractData.intToBytes(responseDataType));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return out.toByteArray();
	}

}
