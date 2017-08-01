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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((ipAddress == null) ? 0 : ipAddress.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        result = prime * result
                + ((serverName == null) ? 0 : serverName.hashCode());
        return result;
    }

    @Override
    public int getDataType() {
        return DATA_TYPE_SERVER_INFO;
    }

    @Override
    public boolean decode(byte[] data) {
        return false;
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }
}
