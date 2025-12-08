package br.com.dock.access.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeviceInfoTest {

    @Test
    void shouldCreateObjectUsingEmptyConstructorAndSetters() {
        // Arrange
        String type = "MOBILE";
        String os = "iOS";
        String version = "14.0";
        String ipAddress = "127.0.0.1";

        DeviceInfo device = new DeviceInfo();

        // Act
        device.setType(type);
        device.setOs(os);
        device.setVersion(version);
        device.setIpAddress(ipAddress);

        // Assert
        assertEquals(type, device.getType());
        assertEquals(os, device.getOs());
        assertEquals(version, device.getVersion());
        assertEquals(ipAddress, device.getIpAddress());
    }

    @Test
    void shouldCreateObjectUsingFullConstructor() {
        // Arrange
        String type = "MOBILE";
        String os = "iOS";
        String version = "11";
        String ipAddress = "127.0.0.1";

        // Act
        DeviceInfo device = new DeviceInfo(type, os, version, ipAddress);

        // Assert
        assertEquals(type, device.getType());
        assertEquals(os, device.getOs());
        assertEquals(version, device.getVersion());
        assertEquals(ipAddress, device.getIpAddress());
    }

    @Test
    void shouldGenerateValidToString() {
        // Arrange
        DeviceInfo device = new DeviceInfo(
                "MOBILE",
                "iOS",
                "17.1",
                "127.0.0.1"
        );

        // Act
        String result = device.toString();

        // Assert
        assertTrue(result.contains("type"));
        assertTrue(result.contains("os"));
        assertTrue(result.contains("version"));
        assertTrue(result.contains("ipAddress"));

        assertTrue(result.contains("MOBILE"));
        assertTrue(result.contains("iOS"));
        assertTrue(result.contains("17.1"));
        assertTrue(result.contains("127.0.0.1"));
    }
}
