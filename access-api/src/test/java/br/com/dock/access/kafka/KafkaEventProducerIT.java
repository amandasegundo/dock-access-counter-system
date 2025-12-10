package br.com.dock.access.kafka;

import br.com.dock.access.dto.AccessEventMessage;
import br.com.dock.access.dto.AccessEventResponse;
import br.com.dock.access.dto.DeviceInfo;
import br.com.dock.access.dto.GeoLocation;
import br.com.dock.access.service.AccessCounterService;
import org.apache.kafka.common.KafkaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaEventProducerIT {

    private KafkaTemplate<String, AccessEventMessage> kafkaTemplate;
    private AccessCounterService accessCounterService;

    private KafkaEventProducer kafkaEventProducer;

    @BeforeEach
    void setup() {
        kafkaTemplate = mock(KafkaTemplate.class);
        accessCounterService = mock(AccessCounterService.class);

        kafkaEventProducer = new KafkaEventProducer(kafkaTemplate, accessCounterService);

        ReflectionTestUtils.setField(kafkaEventProducer, "output", "access-topic");
    }

    private AccessEventMessage buildPayload() {
        GeoLocation geo = new GeoLocation();
        geo.setLatitude(12.34);
        geo.setLongitude(56.78);

        DeviceInfo device = new DeviceInfo();
        device.setType("MOBILE");
        device.setOs("iOS");
        device.setVersion("14");
        device.setIpAddress("127.0.0.1");

        return new AccessEventMessage(
            UUID.randomUUID(),
            123,
            "Maria",
            System.currentTimeMillis(),
            geo,
            device
        );
    }

    @Test
    void processMessageShouldSendToKafkaWhenAccessIsValid() {
        // Arrange
        AccessEventMessage payload = buildPayload();

        when(accessCounterService.isValid()).thenReturn(true);
        when(kafkaTemplate.send(any(Message.class))).thenReturn(null);

        // Act
        AccessEventResponse response = kafkaEventProducer.processMessage(payload);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("", response.getMessage());

        ArgumentCaptor<Message<AccessEventMessage>> captor =
                ArgumentCaptor.forClass((Class<Message<AccessEventMessage>>) (Class<?>) Message.class);

        verify(kafkaTemplate, times(1)).send(captor.capture());

        Message<AccessEventMessage> sentMessage = captor.getValue();
        assertNotNull(sentMessage);
        assertEquals("access-topic", sentMessage.getHeaders().get(KafkaHeaders.TOPIC));
        assertEquals(payload, sentMessage.getPayload());
    }

    @Test
    void processMessageShouldNotSendToKafkaWhenAccessLimitReached() {
        // Arrange
        AccessEventMessage payload = buildPayload();

        when(accessCounterService.isValid()).thenReturn(false);

        // Act
        AccessEventResponse response = kafkaEventProducer.processMessage(payload);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Access limit reached, message was not processed.", response.getMessage());

        verify(kafkaTemplate, never()).send(any(Message.class));
    }

    @Test
    void processMessageShouldThrowKafkaExceptionWhenSendFails() {
        // Arrange
        AccessEventMessage payload = buildPayload();

        when(accessCounterService.isValid()).thenReturn(true);
        when(kafkaTemplate.send(any(Message.class)))
                .thenThrow(new KafkaException("Simulated failure"));

        // Act & Assert
        assertThrows(KafkaException.class, () -> kafkaEventProducer.processMessage(payload));

        verify(kafkaTemplate, times(1)).send(any(Message.class));
    }
}
