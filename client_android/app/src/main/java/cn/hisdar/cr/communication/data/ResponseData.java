package cn.hisdar.cr.communication.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ResponseData extends AbstractData {

	private long writeTime;
	private int dataLength;
	
	public ResponseData() {
		
	}
	
	public ResponseData(long wirteTime, int dataLength) {
		this.writeTime = wirteTime;
		this.dataLength = dataLength;
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
		
		return true;
	}

	@Override
	public byte[] encode() {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			out.write(AbstractData.longToBytes(writeTime));
			out.write(AbstractData.intToBytes(dataLength));
		} catch (IOException e) {}
		
		return out.toByteArray();
	}

}
