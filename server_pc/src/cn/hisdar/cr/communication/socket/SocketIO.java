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
	private SocketWriter socketWriter = null;
	
	public SocketIO(Socket socket) {
		this.socket = socket;
		socketIOEventListeners = new ArrayList<>();

		socketReader = new SocketReader();
		socketReader.startReader();
		
		socketWriter = new SocketWriter();
		socketWriter.startWriter();
	}
	
	public void startSocketIO() {
		
		if (socketReader != null) {
			socketReader.stopReader();
		}
		
		socketReader = new SocketReader();
		socketReader.startReader();
		
		if (socketWriter != null) {
			socketWriter.stopWriter();
		}
		
		socketWriter = new SocketWriter();
		socketWriter.startWriter();
	}
	
	public void stopSocketIO() {
		socketReader.stopReader();
		socketReader = null;
		
		socketWriter.stopWriter();
		socketWriter = null;
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

		public void startReader() {
		    isStop = false;
		    start();
		}
		
		public void stopReader() {
		    isStop = true;
		    
		    for (int i = 0; i < socketIOEventListeners.size(); i++) {
				socketIOEventListeners.get(i).socketDisconnectEvent(socket);
			}
		    
		    socket = null;
		}
		
		// TODO: this function should be recode
		private byte[] readData(InputStream inputStream, int dataLen) {
		
		    int offset = 0;
		    byte[] data = null;
		    
		    try {
		    	data = new byte[dataLen];
		    } catch (NegativeArraySizeException e) {
		    	e.printStackTrace();
		    	return null;
		    }
		    
		    int readLen = 0;
		    while (dataLen > 0 && !isStop) {
		    	try {
		    		readLen = inputStream.read(data, offset, dataLen);
		    		if (readLen < 0) {
						stopReader();
		    			readLen = 0;
						return null;
		    		}
		    	} catch (IOException e) {
		    		e.printStackTrace();

		    		stopReader();
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
	            //System.out.println("dataTime=" + dataTime + ", bytes data:" + AbstractData.arrayToString(dataTimeByte));

	            // read data type 4 bytes
	            byte[] dataTypeByte = readData(inputStream, 4);
	            int dataType = AbstractData.bytesToInt(dataTypeByte);
	            //System.out.println("dataType=" + dataType + ", bytes data:" + AbstractData.arrayToString(dataTypeByte));

	            // read data length 4 bytes
	            byte[] dataLenByte = readData(inputStream, 4);
	            int dataLen = AbstractData.bytesToInt(dataLenByte);
	            //System.out.println("dataLen=" + dataLen + ", bytes data:" + AbstractData.arrayToString(dataLenByte));

	            // read data
	            byte[] dataBuf = readData(inputStream, dataLen);
	
	            // notify data
	            if (!isStop) {
	            	dispatch(dataBuf, dataType);
	            }
		    }
		}
	}
	
	/** 
	 * This method is considered unsafe, if multi thread call this method, 
	 * the data maybe chaos, 
	 * #sendDataMutual was be advised to use
	 * 
	 * @param data
	 * @return
	 */
    public boolean sendData(AbstractData data) {

        if (socket == null) {
            return false;
        }

        try {
            OutputStream out = socket.getOutputStream();

            // write send time 8 bytes
			long currentTime = System.currentTimeMillis();
			byte[] timeBytes = AbstractData.longToBytes(currentTime);
			//Log.i(CRAActivity.TAG, "[Hisdar]data time=" + currentTime + ", bytes data:" + AbstractData.arrayToString(timeBytes));

			out.write(timeBytes);
            out.flush();

			// write data type 4 bytes
			byte[] dataTypeBytes = AbstractData.intToBytes(data.getDataType());
			//Log.i(CRAActivity.TAG, "[Hisdar]data type=" + data.getDataType() + ", bytes data:" + AbstractData.arrayToString(dataTypeBytes));

            out.write(dataTypeBytes);
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
    
	public boolean sendDataMutual(AbstractData data) {
		
		socketWriter.sendDataMutual(data);
		socketWriter.interrupt();
		
		return true;
	}
	
	private class SocketWriter extends Thread {
		
		private boolean isStop = false;
		private ArrayList<AbstractData> socketDatas = null;

		public SocketWriter() {
			socketDatas = new ArrayList<>();
		}
		
		public void stopWriter() {
			isStop = true;
		}

		public void startWriter() {
		    isStop = false;
		    start();
		}
		
		public boolean sendDataMutual(AbstractData data) {
			socketDatas.add(data);
			
			return true;
		}
		
		public void run() {
			
			while (!isStop) {
			
				if (socketDatas.size() <= 0) {
				
					try {
						sleep(5000);
					} catch (InterruptedException e) {}
					
					continue;
				}
				
				AbstractData currentData = socketDatas.get(0);
				socketDatas.remove(0);
				sendData(currentData);
			}
		}
	}
}
