package br.com.dock.access.service;

import br.com.dock.access.client.RedisClient;
import br.com.dock.access.dto.AccessEventMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void processShouldIncrementAndLogWhenBelowLimit(CapturedOutput output) {
        // Arrange
        AccessEventMessage msg = new AccessEventMessage();
        msg.setRequestId(UUID.randomUUID());

        when(redisClient.getLong(KEY)).thenReturn(3L);
        when(redisClient.increment(KEY)).thenReturn(4L);

        // Act
        service.process(msg);

        // Assert
        verify(redisClient).getLong(KEY);
        verify(redisClient).increment(KEY);

        assertTrue(output.getOut().contains("Message received successfully"));
        assertTrue(output.getOut().contains("Access counted, current count [4]"));
    }

    @Test
    void processShouldNotIncrementAndLogLimitReached(CapturedOutput output) {
        // Arrange
        AccessEventMessage msg = new AccessEventMessage();
        msg.setRequestId(UUID.randomUUID());

        when(redisClient.getLong(KEY)).thenReturn(5L);

        // Act
        service.process(msg);

        // Assert
        verify(redisClient).getLong(KEY);
        verify(redisClient, never()).increment(anyString());

        assertTrue(output.getOut().contains("Access limit reached [5]"));
    }

    @Test
    void isValidShouldReturnTrueWhenBelowLimit() {
        // Arrange
        when(redisClient.getLong(KEY)).thenReturn(2L);

        // Act
        boolean result = service.isValid();

        // Assert
        assertTrue(result);
        verify(redisClient).getLong(KEY);
    }

    @Test
    void isValidShouldReturnFalseWhenAtOrAboveLimit() {
        // Arrange
        when(redisClient.getLong(KEY)).thenReturn(5L);

        // Act
        boolean result = service.isValid();

        // Assert
        assertFalse(result);
        verify(redisClient).getLong(KEY);
    }
}
