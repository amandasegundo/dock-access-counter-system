package br.com.dock.access.dto;

public class DeviceInfo {
    private String type;
    private String os;
    private String version;
    private String ipAddress;

    public DeviceInfo() {
    }

    public DeviceInfo(String type, String os, String version, String ipAddress) {
        this.type = type;
        this.os = os;
        this.version = version;
        this.ipAddress = ipAddress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
