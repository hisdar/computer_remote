package cn.hisdar.computerremote.server;

public interface ClientEventListener {

	public void clientConnectEvent(ComputerRemoteClient computerRemoteClient);
	public void clientDisconnectEvent(ComputerRemoteClient computerRemoteClient);
}
