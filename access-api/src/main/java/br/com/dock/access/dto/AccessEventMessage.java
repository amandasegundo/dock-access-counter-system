package br.com.dock.access.dto;

import java.time.Instant;
import java.util.UUID;

public class AccessEventMessage {
    private UUID requestId;
    private Integer clientId;
    private String clientName;
    private Instant timestamp;

    private GeoLocation geolocation;
    private DeviceInfo device;

    public AccessEventMessage() {
    }

    public AccessEventMessage(UUID requestId, Integer clientId, String clientName, Instant timestamp, GeoLocation geolocation, DeviceInfo device) {
        this.requestId = requestId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.timestamp = timestamp;
        this.geolocation = geolocation;
        this.device = device;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public GeoLocation getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(GeoLocation geolocation) {
        this.geolocation = geolocation;
    }

    public DeviceInfo getDevice() {
        return device;
    }

    public void setDevice(DeviceInfo device) {
        this.device = device;
    }
}
