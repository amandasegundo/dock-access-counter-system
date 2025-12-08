package br.com.dock.access.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeoLocationTest {

    @Test
    void shouldCreateObjectUsingEmptyConstructorAndSetters() {
        // Arrange
        double latitude = -27.5920;
        double longitude = -48.5530;

        GeoLocation geo = new GeoLocation();

        // Act
        geo.setLatitude(latitude);
        geo.setLongitude(longitude);

        // Assert
        assertEquals(latitude, geo.getLatitude());
        assertEquals(longitude, geo.getLongitude());
    }

    @Test
    void shouldCreateObjectUsingFullConstructor() {
        // Arrange
        double latitude = 34.0522;
        double longitude = -118.2437;

        // Act
        GeoLocation geo = new GeoLocation(latitude, longitude);

        // Assert
        assertEquals(latitude, geo.getLatitude());
        assertEquals(longitude, geo.getLongitude());
    }

    @Test
    void shouldGenerateValidToString() {
        // Arrange
        GeoLocation geo = new GeoLocation(10.5, 20.5);

        // Act
        String result = geo.toString();

        // Assert
        assertTrue(result.contains("latitude"));
        assertTrue(result.contains("longitude"));
        assertTrue(result.contains("10.5"));
        assertTrue(result.contains("20.5"));
    }
}
