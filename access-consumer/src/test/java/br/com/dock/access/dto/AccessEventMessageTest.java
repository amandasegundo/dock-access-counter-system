package br.com.dock.access.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccessEventMessageTest {

    @Test
    void shouldCreateObjectUsingEmptyConstructorAndSetters() {
        // Arrange
        UUID requestId = UUID.randomUUID();
        Integer clientId = 123;
        String clientName = "Alice";
        Long timestamp = 1764780100L;

        GeoLocation geo = mock(GeoLocation.class);
        DeviceInfo device = mock(DeviceInfo.class);

        AccessEventMessage msg = new AccessEventMessage();

        // Act
        msg.setRequestId(requestId);
        msg.setClientId(clientId);
        msg.setClientName(clientName);
        msg.setTimestamp(timestamp);
        msg.setGeolocation(geo);
        msg.setDevice(device);

        // Assert
        assertEquals(requestId, msg.getRequestId());
        assertEquals(clientId, msg.getClientId());
        assertEquals(clientName, msg.getClientName());
        assertEquals(timestamp, msg.getTimestamp());
        assertEquals(geo, msg.getGeolocation());
        assertEquals(device, msg.getDevice());
    }

    @Test
    void shouldCreateObjectUsingFullConstructor() {
        // Arrange
        UUID requestId = UUID.randomUUID();
        Integer clientId = 321;
        String clientName = "João";
        Long timestamp = 1764780100L;

        GeoLocation geo = mock(GeoLocation.class);
        DeviceInfo device = mock(DeviceInfo.class);

        // Act
        AccessEventMessage msg = new AccessEventMessage(
                requestId, clientId, clientName, timestamp, geo, device
        );

        // Assert
        assertEquals(requestId, msg.getRequestId());
        assertEquals(clientId, msg.getClientId());
        assertEquals(clientName, msg.getClientName());
        assertEquals(timestamp, msg.getTimestamp());
        assertEquals(geo, msg.getGeolocation());
        assertEquals(device, msg.getDevice());
    }

    @Test
    void shouldGenerateValidToString() {
        // Arrange
        UUID requestId = UUID.randomUUID();
        GeoLocation geo = mock(GeoLocation.class);
        DeviceInfo device = mock(DeviceInfo.class);

        when(geo.toString()).thenReturn("geo");
        when(device.toString()).thenReturn("device");

        AccessEventMessage msg = new AccessEventMessage(
                requestId, 10, "Carlos", 123L, geo, device
        );

        // Act
        String result = msg.toString();

        // Assert
        assertTrue(result.contains("requestId"));
        assertTrue(result.contains("clientId"));
        assertTrue(result.contains("clientName"));
        assertTrue(result.contains("timestamp"));
        assertTrue(result.contains("geo"));
        assertTrue(result.contains("device"));
    }
}
