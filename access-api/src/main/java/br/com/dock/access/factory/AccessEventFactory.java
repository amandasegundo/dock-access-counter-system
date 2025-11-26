package br.com.dock.access.factory;

import br.com.dock.access.AddAccessRequest;
import br.com.dock.access.dto.AccessEventMessage;
import br.com.dock.access.dto.DeviceInfo;
import br.com.dock.access.dto.GeoLocation;

import java.util.UUID;

public class AccessEventFactory {

    public static AccessEventMessage fromProto(AddAccessRequest proto) {

        AccessEventMessage msg = new AccessEventMessage();
        msg.setRequestId(UUID.fromString(proto.getRequestId()));
        msg.setClientId(proto.getClientId());
        msg.setClientName(proto.getClientName());
        msg.setTimestamp(proto.getTimestamp());
        msg.setGeolocation(parseGeolocation(proto));
        msg.setDevice(parseDevice(proto));

        return msg;
    }

    private static GeoLocation parseGeolocation(AddAccessRequest proto) {
        if (!proto.hasGeolocation()) return null;

        GeoLocation geo = new GeoLocation();
        geo.setLatitude(proto.getGeolocation().getLatitude());
        geo.setLongitude(proto.getGeolocation().getLongitude());
        return geo;
    }

    private static DeviceInfo parseDevice(AddAccessRequest proto) {
        if (!proto.hasDevice()) return null;

        var d = proto.getDevice();
        DeviceInfo device = new DeviceInfo();

        device.setType(d.getType());
        device.setOs(d.getOs());
        device.setVersion(d.getVersion());
        device.setIpAddress(d.getIpAddress());

        return device;
    }
}
