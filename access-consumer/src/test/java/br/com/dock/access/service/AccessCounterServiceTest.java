package br.com.dock.access.service;

import br.com.dock.access.client.RedisClient;
import br.com.dock.access.dto.AccessEventMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(OutputCaptureExtension.class)
class AccessCounterServiceTest {

    private static final String KEY = "ACCESS_COUNT";

    @Mock
    private RedisClient redisClient;

    @InjectMocks
    private AccessCounterService service;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(service, "accessLimit", 5L);
    }

    @Test
    void processShouldIncrementAndLogWhenBelowLimit(CapturedOutput output) throws IOException {
        // Arrange
        AccessEventMessage msg = new AccessEventMessage();
        msg.setRequestId(UUID.randomUUID());
        when(redisClient.incrementWithLimit(KEY, 5L)).thenReturn(3L);

        // Act
        service.process(msg);

        // Assert
        verify(redisClient).incrementWithLimit(KEY, 5L);
        assertTrue(output.getOut().contains("Message received successfully"));
        assertTrue(output.getOut().contains("Access counted, current count [3]"));
    }

    @Test
    void processShouldLogLimitReachedWhenAtLimit(CapturedOutput output) throws IOException {
        // Arrange
        AccessEventMessage msg = new AccessEventMessage();
        msg.setRequestId(UUID.randomUUID());
        when(redisClient.incrementWithLimit(KEY, 5L)).thenReturn(-1L);

        // Act
        service.process(msg);

        // Assert
        verify(redisClient).incrementWithLimit(KEY, 5L);
        assertTrue(output.getOut().contains("Access limit reached [5]"));
    }

    @Test
    void processShouldLogErrorWhenException(CapturedOutput output) throws IOException {
        // Arrange
        AccessEventMessage msg = new AccessEventMessage();
        msg.setRequestId(UUID.randomUUID());
        doThrow(new IOException("Redis error")).when(redisClient).incrementWithLimit(KEY, 5L);

        // Act
        service.process(msg);

        // Assert
        verify(redisClient).incrementWithLimit(KEY, 5L);
        assertTrue(output.getOut().contains("Error when incrementing"));
    }
}
