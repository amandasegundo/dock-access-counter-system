package br.com.dock.access.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisClientTest {

    private static final String KEY = "ACCESS_COUNT";

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RAtomicLong atomicLong;

    @InjectMocks
    private RedisClient redisClient;

    @Test
    void incrementShouldIncreaseValueAndReturnNewValue() {
        // Arrange
        when(redissonClient.getAtomicLong(KEY)).thenReturn(atomicLong);
        when(atomicLong.incrementAndGet()).thenReturn(123L);

        // Act
        long result = redisClient.increment(KEY);

        // Assert
        assertEquals(123L, result);
        verify(redissonClient).getAtomicLong(KEY);
        verify(atomicLong).incrementAndGet();
        verifyNoMoreInteractions(redissonClient, atomicLong);
    }

    @Test
    void shouldReturnValueFromRedis() {
        // Arrange
        when(redissonClient.getAtomicLong(KEY)).thenReturn(atomicLong);
        when(atomicLong.get()).thenReturn(123L);

        // Act
        long result = redisClient.getLong(KEY);

        // Assert
        assertEquals(123L, result);
        verify(redissonClient).getAtomicLong(KEY);
        verify(atomicLong).get();
        verifyNoMoreInteractions(redissonClient, atomicLong);
    }
}
