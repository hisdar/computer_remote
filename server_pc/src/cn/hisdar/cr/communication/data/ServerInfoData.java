package cn.hisdar.cr.communication.data;

/**
 * Created by Hisdar on 2017/8/1.
 */

public class ServerInfoData extends AbstractData {

    private String serverName;
    private String ipAddress;
    private String port;
    private int id;

    public ServerInfoData() {

    }

    public ServerInfoData(String serverName, String ipAddress, String port) {
        this.serverName = serverName;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
	public String toString() {
		return "ServerInfoData [serverName=" + serverName + ", ipAddress=" + ipAddress + ", port=" + port + ", id=" + id
				+ "]";
	}

	@Override
    public int getDataType() {
        return DATA_TYPE_SERVER_INFO;
    }

    @Override
    public boolean decode(byte[] data) {
        serverName = new String(data);
        return true;
    }

    @Override
    public byte[] encode() {
        if (serverName == null) {
            return null;
        }

        return serverName.getBytes();
    }
}
