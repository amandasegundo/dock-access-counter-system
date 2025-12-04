package br.com.dock.access.kafka;

import br.com.dock.access.dto.AccessEventMessage;
import br.com.dock.access.dto.AccessEventResponse;
import br.com.dock.access.service.AccessCounterService;
import org.apache.kafka.common.KafkaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(OutputCaptureExtension.class)
class KafkaEventProducerTest {

    private static final String OUTPUT_TOPIC_FIELD = "output";
    private static final String TOPIC = "access-topic";
    private static final String MSG_LIMIT_REACHED = "Access limit reached, message was not processed.";

    @Mock
    private KafkaTemplate<String, AccessEventMessage> kafkaTemplate;

    @Mock
    private AccessCounterService accessCounterService;

    @InjectMocks
    private KafkaEventProducer producer;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(producer, OUTPUT_TOPIC_FIELD, TOPIC);
    }

    @Test
    void processMessageShouldSendMessageWhenValid(CapturedOutput output) {
        // Arrange
        AccessEventMessage payload = new AccessEventMessage();
        payload.setRequestId(UUID.randomUUID());

        when(accessCounterService.isValid()).thenReturn(true);

        ArgumentCaptor<Message<AccessEventMessage>> messageCaptor =
                ArgumentCaptor.forClass(Message.class);

        // Act
        AccessEventResponse response = producer.processMessage(payload);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("", response.getMessage());

        verify(accessCounterService).isValid();
        verify(kafkaTemplate).send(messageCaptor.capture());

        Message<AccessEventMessage> captured = messageCaptor.getValue();

        assertEquals(payload, captured.getPayload());
        assertEquals(TOPIC, captured.getHeaders().get("kafka_topic"));

        assertTrue(output.getOut().contains("Message with requestId [" + payload.getRequestId() + "] received"));
        assertTrue(output.getOut().contains("Sending message with requestId [" + payload.getRequestId() + "] to topic ["+ TOPIC +"]"));
        assertTrue(output.getOut().contains("Message with RequestId [" + payload.getRequestId() + "] sent successfully"));

        verifyNoMoreInteractions(kafkaTemplate, accessCounterService);
    }

    @Test
    void processMessageShouldReturnFalseWhenLimitReached(CapturedOutput output) {
        // Arrange
        AccessEventMessage payload = new AccessEventMessage();
        payload.setRequestId(UUID.randomUUID());

        when(accessCounterService.isValid()).thenReturn(false);

        // Act
        AccessEventResponse response = producer.processMessage(payload);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(MSG_LIMIT_REACHED, response.getMessage());
        assertTrue(output.getOut().contains("Message with requestId [" + payload.getRequestId() + "] received"));
        assertTrue(output.getOut().contains(MSG_LIMIT_REACHED));

        verify(accessCounterService).isValid();
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void processMessageShouldThrowKafkaExceptionWhenSendFails() {
        AccessEventMessage payload = new AccessEventMessage();
        payload.setRequestId(UUID.randomUUID());

        when(accessCounterService.isValid()).thenReturn(true);
        doThrow(new KafkaException("Simulated failure"))
                .when(kafkaTemplate)
                .send(any(Message.class));

        assertThrows(KafkaException.class, () -> producer.processMessage(payload));

        verify(accessCounterService).isValid();
        verify(kafkaTemplate).send(any(Message.class));
    }
}
