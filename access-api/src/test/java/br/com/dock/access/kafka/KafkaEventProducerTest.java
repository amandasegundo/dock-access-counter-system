package br.com.dock.access.kafka;

import br.com.dock.access.dto.AccessEventMessage;
import br.com.dock.access.dto.AccessEventResponse;
import br.com.dock.access.dto.DeviceInfo;
import br.com.dock.access.dto.GeoLocation;
import org.apache.kafka.common.KafkaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@ExtendWith(OutputCaptureExtension.class)
class KafkaEventProducerTest {

    private static final String OUTPUT_TOPIC_FIELD = "output";
    private static final String TOPIC = "access-topic";

    @Mock
    private KafkaTemplate<String, AccessEventMessage> kafkaTemplate;

    private KafkaEventProducer producer;

    @BeforeEach
    void setup() {
        producer = new KafkaEventProducer(kafkaTemplate);
        ReflectionTestUtils.setField(producer, OUTPUT_TOPIC_FIELD, TOPIC);
    }

    @Test
    void processMessage_shouldSendMessageAndLogSuccess(CapturedOutput output) {
        // Arrange
        AccessEventMessage payload = new AccessEventMessage();
        payload.setRequestId(UUID.randomUUID());

        // Act
        AccessEventResponse response = producer.processMessage(payload);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Message processed successfully.", response.getMessage());

        // Assert Kafka send
        ArgumentCaptor<Message<AccessEventMessage>> captor =
                ArgumentCaptor.forClass(Message.class);

        verify(kafkaTemplate).send(captor.capture());

        Message<AccessEventMessage> sentMessage = captor.getValue();
        assertEquals(payload, sentMessage.getPayload());
        assertEquals(TOPIC, sentMessage.getHeaders().get(KafkaHeaders.TOPIC));

        // Assert logs
        assertTrue(output.getOut().contains("Message with requestId [" + payload.getRequestId() + "] received"));
        assertTrue(output.getOut().contains("Sending message with requestId [" + payload.getRequestId() + "] to topic ["+ TOPIC +"]"));
        assertTrue(output.getOut().contains("Message with RequestId [" + payload.getRequestId() + "] sent successfully"));

        verifyNoMoreInteractions(kafkaTemplate);
    }

    @Test
    void processMessage_shouldLogErrorAndReturnFailure_whenKafkaThrowsException(CapturedOutput output) {
        // Arrange
        GeoLocation geo = new GeoLocation();
        geo.setLatitude(12.34);
        geo.setLongitude(56.78);

        DeviceInfo device = new DeviceInfo();
        device.setType("MOBILE");
        device.setOs("iOS");
        device.setVersion("14");
        device.setIpAddress("127.0.0.1");

        AccessEventMessage payload = new AccessEventMessage(
                UUID.randomUUID(),
                123,
                "Maria",
                System.currentTimeMillis(),
                geo,
                device
        );

        doThrow(new KafkaException("Error"))
                .when(kafkaTemplate)
                .send(any(Message.class));

        // Act
        AccessEventResponse response = producer.processMessage(payload);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Error processing message.", response.getMessage());

        assertTrue(output.getOut().contains("Message with requestId [" + payload.getRequestId() + "] received"));
        assertTrue(output.getOut().contains("Failed to send message to Kafka"));

        verify(kafkaTemplate).send(any(Message.class));
        verifyNoMoreInteractions(kafkaTemplate);
    }
}
