package br.com.dock.access.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RedisClientTest {

    @Mock
    private RedissonClient redissonClient;

    @InjectMocks
    private RedisClient redisClient;

    @Mock
    private RScript rScript;

    @Test
    void incrementWithLimitShouldCallScriptAndReturnResult() throws IOException {
        // Arrange
        String key = "ACCESS_COUNT";
        long limit = 10L;
        long expectedResult = 42L;

        when(redissonClient.getScript(StringCodec.INSTANCE)).thenReturn(rScript);
        when(rScript.eval(
                eq(RScript.Mode.READ_WRITE),
                anyString(),
                eq(RScript.ReturnType.INTEGER),
                eq(Collections.singletonList(key)),
                eq(String.valueOf(limit))
        )).thenReturn(expectedResult);

        // Act
        long result = redisClient.incrementWithLimit(key, limit);

        // Assert
        assertEquals(expectedResult, result);
        verify(redissonClient).getScript(StringCodec.INSTANCE);
        verify(rScript).eval(
                eq(RScript.Mode.READ_WRITE),
                anyString(),
                eq(RScript.ReturnType.INTEGER),
                eq(Collections.singletonList(key)),
                eq(String.valueOf(limit))
        );
    }
}
