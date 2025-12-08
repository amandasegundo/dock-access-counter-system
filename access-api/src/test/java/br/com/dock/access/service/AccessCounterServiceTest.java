package br.com.dock.access.service;

import br.com.dock.access.client.RedisClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessCounterServiceTest {

    private static final String KEY = "ACCESS_COUNT";
    private static final String FIELD = "accessLimit";

    @Mock
    private RedisClient redisClient;

    @InjectMocks
    private AccessCounterService service;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(service, FIELD, 1000000L);
    }

    @Test
    void shouldReturnTrueWhenBelowLimit() {
        // Arrange
        when(redisClient.getLong(KEY)).thenReturn(999999L);

        // Act
        boolean result = service.isValid();

        // Assert
        assertTrue(result);
        verify(redisClient).getLong(KEY);
        verifyNoMoreInteractions(redisClient);
    }

    @Test
    void shouldReturnFalseWhenLimitReached() {
        // Arrange
        when(redisClient.getLong(KEY)).thenReturn(1000000L);

        // Act
        boolean result = service.isValid();

        // Assert
        assertFalse(result);
        verify(redisClient).getLong(KEY);
        verifyNoMoreInteractions(redisClient);
    }

    @Test
    void shouldReturnFalseWhenAboveLimit() {
        // Arrange
        when(redisClient.getLong(KEY)).thenReturn(1000001L);

        // Act
        boolean result = service.isValid();

        // Assert
        assertFalse(result);
        verify(redisClient).getLong(KEY);
        verifyNoMoreInteractions(redisClient);
    }
}
