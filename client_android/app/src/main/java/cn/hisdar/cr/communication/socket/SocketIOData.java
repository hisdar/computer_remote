package cn.hisdar.cr.communication.socket;

import java.util.Arrays;

public class SocketIOData {

	private long writeTime;
	private int dataType;
	private int dataLength;
	private byte[] payload;
	
	public SocketIOData() {
		writeTime = 0;
		dataType = 0;
		dataLength = 0;
		payload = null;
	}
	
	public SocketIOData(long writeTime, int dataType, int dataLength, byte[] payload) {
		this.writeTime = writeTime;
		this.dataType = dataType;
		this.dataLength = dataLength;
		this.payload = payload;
	}
	
	public long getWriteTime() {
		return writeTime;
	}
	public void setWriteTime(long writeTime) {
		this.writeTime = writeTime;
	}
	public int getDataType() {
		return dataType;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	public int getDataLength() {
		return dataLength;
	}
	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}
	public byte[] getPayload() {
		return payload;
	}
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dataLength;
		result = prime * result + dataType;
		result = prime * result + Arrays.hashCode(payload);
		result = prime * result + (int) (writeTime ^ (writeTime >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SocketIOData other = (SocketIOData) obj;
		if (dataLength != other.dataLength)
			return false;
		if (dataType != other.dataType)
			return false;
		if (!Arrays.equals(payload, other.payload))
			return false;
		if (writeTime != other.writeTime)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "SocketIOData [writeTime=" + writeTime + ", dataType=" + dataType + ", dataLength=" + dataLength + "]";
	}
}
