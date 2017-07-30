package cn.hisdar.cr.communication.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import cn.hisdar.cr.communication.data.AbstractData;
import cn.hisdar.cr.communication.handler.AbstractDataHandler;

public class SocketIO {

	private Socket socket;
	private ArrayList<SocketIOEventListener> socketIOEventListeners = null;
	private SocketReader socketReader = null;
	
	public SocketIO(Socket socket) {
		this.socket = socket;
		socketIOEventListeners = new ArrayList<>();
		socketReader = new SocketReader();
		socketReader.startServerReader();
	}
	
	public void startSocketIO() {
		
		if (socketReader != null) {
			socketReader.stopServerReader();
		}
		
		socketReader = new SocketReader();
		socketReader.startServerReader();
	}
	
	public void stopSocketIO() {
		socketReader.stopServerReader();
		socketReader = null;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public void addSocketIOEventListener(SocketIOEventListener l) {
		for (int i = 0; i < socketIOEventListeners.size(); i++) {
			if (socketIOEventListeners.get(i) == l) {
				return;
			}
		}
		
		socketIOEventListeners.add(l);
	}
	
	public void removeSocketIOEventListener(SocketIOEventListener l) {
		socketIOEventListeners.remove(l);
	}
	
    private void dispatch(byte[] data, int dataType) {
        for (int i = 0; i < socketIOEventListeners.size(); i++) {
			socketIOEventListeners.get(i).socketIOEvent(data, dataType, socket);
		}
    }
	
	private class SocketReader extends Thread {
		private boolean isStop = false;

		public void startServerReader() {
		    isStop = false;
		    start();
		}
		
		public void stopServerReader() {
		    isStop = true;
		}
		
		private byte[] readData(InputStream inputStream, int dataLen) throws IOException {
		
		    int offset = 0;
		    byte[] data = new byte[dataLen];
		
		    while (dataLen > 0) {
		        int readLen = inputStream.read(data, offset, dataLen);
		        offset += readLen;
		        dataLen -= readLen;
		    }
		
		    return data;
		}
		
		public void run() {
		
		    if (socket == null) {
		        return;
		    }

		    InputStream inputStream = null;
		    try {
		        inputStream = socket.getInputStream();
		    } catch (IOException e) {
		        return;
		    }
		
		    // read the data length
		    while (!isStop) {
		        try {
		            // read send time
		            byte[] dataTimeByte = readData(inputStream, 8);
		            long dataTime = AbstractDataHandler.bytesToLong(dataTimeByte);
		
		            // read data type
		            byte[] dataTypeByte = readData(inputStream, 4);
		            int dataType = AbstractDataHandler.bytesToInt(dataTypeByte);

		            // read data length
		            byte[] dataLenByte = readData(inputStream, 4);
		            int dataLen = AbstractDataHandler.bytesToInt(dataLenByte);

		            // read data
		            byte[] dataBuf = readData(inputStream, dataLen);
		
		            // notify data
		            dispatch(dataBuf, dataType);
		        } catch (IOException e) {
		            e.printStackTrace();
		            stopServerReader();
		        }
		    }
		}
	}
	
    public boolean sendData(AbstractData data) {

        if (socket == null) {
            return false;
        }

        try {
            OutputStream out = socket.getOutputStream();

            // write send time
            out.write(AbstractDataHandler.longToBytes(System.currentTimeMillis()));
            out.flush();

            // write data type
            out.write(AbstractDataHandler.intToBytes(data.getDataType()));
            out.flush();

            // write data length to client, the length is 4, sizeof(int)
            byte[] bytesData = data.encode();

            byte[] dataLength = AbstractDataHandler.intToBytes(bytesData.length);
            out.write(dataLength);
            out.flush();

            // 2.write data to client
            out.write(bytesData);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}