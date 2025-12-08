package br.com.dock.access.mapper;

import br.com.dock.access.AddAccessRequest;
import br.com.dock.access.Device;
import br.com.dock.access.Geolocation;
import br.com.dock.access.dto.AccessEventMessage;
import br.com.dock.access.dto.DeviceInfo;
import br.com.dock.access.dto.GeoLocation;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AccessEventMapperTest {

    private final AccessEventMapper mapper = AccessEventMapper.INSTANCE;

    @Test
    void shouldMapFromProtoToAccessEventMessage() {
        // Arrange
        String uuid = UUID.randomUUID().toString();

        Geolocation geo = Geolocation.newBuilder()
                .setLatitude(12.34)
                .setLongitude(56.78)
                .build();

        Device device = Device.newBuilder()
                .setType("MOBILE")
                .setOs("iOS")
                .setVersion("14")
                .setIpAddress("127.0.0.1")
                .build();

        AddAccessRequest proto = AddAccessRequest.newBuilder()
                .setRequestId(uuid)
                .setClientId(111)
                .setClientName("Maria")
                .setTimestamp(1764780100L)
                .setGeolocation(geo)
                .setDevice(device)
                .build();

        // Act
        AccessEventMessage result = mapper.fromProto(proto);

        // Assert
        assertNotNull(result);
        assertEquals(UUID.fromString(uuid), result.getRequestId());
        assertEquals(111, result.getClientId());
        assertEquals("Maria", result.getClientName());
        assertEquals(1764780100L, result.getTimestamp());

        assertNotNull(result.getGeolocation());
        assertEquals(12.34, result.getGeolocation().getLatitude());
        assertEquals(56.78, result.getGeolocation().getLongitude());

        assertNotNull(result.getDevice());
        assertEquals("MOBILE", result.getDevice().getType());
        assertEquals("iOS", result.getDevice().getOs());
        assertEquals("14", result.getDevice().getVersion());
        assertEquals("127.0.0.1", result.getDevice().getIpAddress());
    }

    @Test
    void shouldMapGeolocationToGeoLocation() {
        // Arrange
        Geolocation geo = Geolocation.newBuilder()
                .setLatitude(1.23)
                .setLongitude(4.56)
                .build();

        // Act
        GeoLocation result = mapper.map(geo);

        // Assert
        assertEquals(1.23, result.getLatitude());
        assertEquals(4.56, result.getLongitude());
    }

    @Test
    void shouldMapDeviceToDeviceInfo() {
        // Arrange
        Device device = Device.newBuilder()
                .setType("MOBILE")
                .setOs("iOS")
                .setVersion("17.1")
                .setIpAddress("127.0.0.1")
                .build();

        // Act
        DeviceInfo result = mapper.map(device);

        // Assert
        assertEquals("MOBILE", result.getType());
        assertEquals("iOS", result.getOs());
        assertEquals("17.1", result.getVersion());
        assertEquals("127.0.0.1", result.getIpAddress());
    }

    @Test
    void shouldReturnNullWhenStringUuidIsNullOrBlank() {
        assertNull(mapper.stringToUuid(null));
        assertNull(mapper.stringToUuid(""));
        assertNull(mapper.stringToUuid("   "));
    }

    @Test
    void shouldConvertValidStringToUuid() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        UUID result = mapper.stringToUuid(id.toString());

        // Assert
        assertEquals(id, result);
    }
}
