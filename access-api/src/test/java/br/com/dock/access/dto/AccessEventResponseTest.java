package br.com.dock.access.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccessEventResponseTest {

    @Test
    void shouldCreateObjectUsingEmptyConstructorAndSetters() {
        // Arrange
        boolean success = true;
        String message = "";

        AccessEventResponse response = new AccessEventResponse();

        // Act
        response.setSuccess(success);
        response.setMessage(message);

        // Assert
        assertEquals(success, response.isSuccess());
        assertEquals(message, response.getMessage());
    }

    @Test
    void shouldCreateObjectUsingFullConstructor() {
        // Arrange
        boolean success = false;
        String message = "Access limit reached, message was not processed.";

        // Act
        AccessEventResponse response = new AccessEventResponse(success, message);

        // Assert
        assertEquals(success, response.isSuccess());
        assertEquals(message, response.getMessage());
    }

    @Test
    void shouldGenerateValidToString() {
        // Arrange
        AccessEventResponse response = new AccessEventResponse(true, "");

        // Act
        String result = response.toString();

        // Assert
        assertTrue(result.contains("success"));
        assertTrue(result.contains("message"));
        assertTrue(result.contains("true"));
    }
}
