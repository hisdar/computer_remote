package cn.hisdar.cr.communication.data;

public abstract class AbstractData {
	public static final int DATA_TYPE_SCREEN_PICTURE = 0x10001;
	public static final int DATA_TYPE_SCREEN_SIZE    = 0x10002;
	public static final int DATA_TYPE_COMMON_DATA    = 0x10003;
	public static final int DATA_TYPE_MOTION_EVENT   = 0x10004;
	public static final int DATA_TYPE_REQUEST        = 0x10005;
    public static final int DATA_TYPE_SERVER_INFO    = 0x10006;
	public static final int DATA_TYPE_MOUSE_BUTTON   = 0x10007;
	public static final int DATA_TYPE_KEY_EVENT      = 0x10008;
	public static final int DATA_TYPE_RESPONSE       = 0x10009;  // response data is used to test network delay

    abstract public int getDataType();

	abstract public boolean decode(byte[] data);

	abstract public byte[] encode();

	public static byte[] longToBytes(long data) {
		
		// the length of long is 8 bytes
		byte[] bytesData = new byte[8];
		for (int i = 0; i < bytesData.length; i++) {
			bytesData[i] = (byte) (0xFF & (data >> (i * 8)));
		}

		return bytesData;
	}

	public static long bytesToLong(byte[] bytesData) {
		
		if (bytesData == null) {
			return 0;
		}
		
		// the length of long is 8 bytes
		long number = 0;
		for (int i = 0; i < bytesData.length; i++) {
			number |= (((long)(bytesData[i] & 0xFF)) << (i * 8));
		}

		return number;
	}

	public static byte[] intToBytes(int data) {
		
		// the length of long is 4 bytes
		byte[] bytesData = new byte[4];
		for (int i = 0; i < bytesData.length; i++) {
			bytesData[i] = (byte) (0xFF & (data >> (i * 8)));
		}

		return bytesData;
	}

	public static int bytesToInt(byte[] bytesData) {
		
		if (bytesData == null) {
			return 0;
		}
		
		// the length of long is 4 bytes
		int number = 0;
		for (int i = 0; i < bytesData.length; i++) {
			number |= ((bytesData[i] & 0xFF) << (i * 8));
		}

		return number;
	}
	
	public static String arrayToString(byte[] array) {
		String str = new String();
		
		for (int i = 0; i < array.length; i++) {
			str += String.format("0x%02x", array[i]);
			if (i < array.length - 1) {
				str += " ";
			}
			
			if (i % 8 == 0 && i != 0) {
				str += "\n";
			}
		}
		
		return str;
		
	}
}
