package br.com.dock.access.factory;

import br.com.dock.access.AddAccessRequest;
import br.com.dock.access.dto.AccessEvent;
import br.com.dock.access.dto.DeviceInfo;
import br.com.dock.access.dto.GeoLocation;
import br.com.dock.access.helper.InstantHelper;

import java.util.UUID;

public class AccessEventFactory {

    public static AccessEvent fromProto(AddAccessRequest proto) {

        return AccessEvent.builder()
                .requestId(UUID.fromString(proto.getRequestId()))
                .clientId(proto.getClientId())
                .clientName(proto.getClientName())
                .timestamp(InstantHelper.parseInstant(proto.getTimestamp()))
                .geolocation(parseGeolocation(proto))
                .device(parseDevice(proto))
                .build();
    }

    private static GeoLocation parseGeolocation(AddAccessRequest proto) {
        if (!proto.hasGeolocation()) return null;

        return GeoLocation.builder()
                .latitude(proto.getGeolocation().getLatitude())
                .longitude(proto.getGeolocation().getLongitude())
                .build();
    }

    private static DeviceInfo parseDevice(AddAccessRequest proto) {
        if (!proto.hasDevice()) return null;

        var d = proto.getDevice();

        return DeviceInfo.builder()
                .type(d.getType())
                .os(d.getOs())
                .version(d.getVersion())
                .ipAddress(d.getIpAddress())
                .build();
    }
}
