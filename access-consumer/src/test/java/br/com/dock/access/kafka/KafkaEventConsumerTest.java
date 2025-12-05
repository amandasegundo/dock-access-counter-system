package br.com.dock.access.kafka;

import br.com.dock.access.dto.AccessEventMessage;
import br.com.dock.access.service.AccessCounterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(OutputCaptureExtension.class)
class KafkaEventConsumerTest {

    @Mock
    private AccessCounterService accessCounterService;

    @InjectMocks
    private KafkaEventConsumer consumer;

    @Test
    void listenShouldDeserializeMessageAndCallProcess() {
        // Arrange
        String jsonMessage = """
            {
              "requestId": "550e8400-e29b-41d4-a716-446655440000",
              "clientId": 123,
              "clientName": "Alice",
              "timestamp": 1700000000,
              "geolocation": {
                "latitude": -26.9189,
                "longitude": -49.0661
              },
              "device": {
                "type": "MOBILE",
                "os": "iOS",
                "version": "14",
                "ipAddress": "192.168.0.10"
              }
            }
            """;

        ArgumentCaptor<AccessEventMessage> messageCaptor =
                ArgumentCaptor.forClass(AccessEventMessage.class);

        // Act
        consumer.listen(jsonMessage);

        // Assert
        verify(accessCounterService).process(messageCaptor.capture());
        verifyNoMoreInteractions(accessCounterService);

        AccessEventMessage captured = messageCaptor.getValue();
        assertNotNull(captured);
        assertEquals(123, captured.getClientId());
        assertEquals("Alice", captured.getClientName());
        assertEquals(1700000000L, captured.getTimestamp());
        assertNotNull(captured.getGeolocation());
        assertNotNull(captured.getDevice());
    }

    @Test
    void listenShouldNotCallProcessWhenJsonIsInvalid(CapturedOutput output) {
        // Arrange
        String invalidJson = "/";

        // Act
        consumer.listen(invalidJson);

        // Assert
        verifyNoInteractions(accessCounterService);

        assertTrue(output.getOut().contains("Error processing message"));
    }
}
