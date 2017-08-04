package cn.hisdar.cr.communication.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import cn.hisdar.cr.communication.data.AbstractData;

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
		    
		    for (int i = 0; i < socketIOEventListeners.size(); i++) {
				socketIOEventListeners.get(i).socketDisconnectEvent(socket);
			}
		    
		    socket = null;
		}
		
		// TODO: this function should be recode
		private byte[] readData(InputStream inputStream, int dataLen) {
		
		    int offset = 0;
		    byte[] data = new byte[dataLen];
		    int readLen = 0;
		    while (dataLen > 0 && !isStop) {
		    	try {
		    		readLen = inputStream.read(data, offset, dataLen);
		    		if (readLen < 0) {
		    			if (isSocketClosed(socket)) {
		    				stopServerReader();
		    				return null;
		    			}
		    			readLen = 0;
		    		}
		    	} catch (IOException e) {
		    		e.printStackTrace();

		    		stopServerReader();
		    		break;
		    	}

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
		    	e.printStackTrace();
		        return;
		    }
		
		    // read the data length
		    while (!isStop) {
	            // read send time 8 bytes
	            byte[] dataTimeByte = readData(inputStream, 8);
	            long dataTime = AbstractData.bytesToLong(dataTimeByte);

	            // read data type 4 bytes
	            byte[] dataTypeByte = readData(inputStream, 4);
	            int dataType = AbstractData.bytesToInt(dataTypeByte);

	            // read data length 4 bytes
	            byte[] dataLenByte = readData(inputStream, 4);
	            int dataLen = AbstractData.bytesToInt(dataLenByte);

	            // read data
	            byte[] dataBuf = readData(inputStream, dataLen);
	
	            // notify data
	            if (!isStop) {
	            	dispatch(dataBuf, dataType);
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

            // write send time 8 bytes
            out.write(AbstractData.longToBytes(System.currentTimeMillis()));
            out.flush();
            
            // write data type 4 bytes
            out.write(AbstractData.intToBytes(data.getDataType()));
            out.flush();

            byte[] bytesData = data.encode();
            
            // write data length to client, 4 bytes
            byte[] dataLength = AbstractData.intToBytes(bytesData.length);
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
    
	public boolean isSocketClosed(Socket socket) {
		try {
			socket.sendUrgentData(0xFF);
			return false;
		} catch (Exception se) {
			return true;
		}
	}
}
