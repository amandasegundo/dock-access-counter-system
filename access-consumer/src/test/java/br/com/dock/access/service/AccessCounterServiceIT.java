package br.com.dock.access.service;

import br.com.dock.access.client.RedisClient;
import br.com.dock.access.dto.AccessEventMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "app.access-limit=100"
        }
)
class AccessCounterServiceIT {

    @Autowired
    private AccessCounterService accessCounterService;

    @Autowired
    private TestRedisClient redisClient;

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        TestRedisClient testRedisClient() {
            return new TestRedisClient();
        }
    }

    static class TestRedisClient extends RedisClient {

        private long value;

        public void setValue(long value) {
            this.value = value;
        }

        @Override
        public long getLong(String key) {
            return value;
        }

        @Override
        public long increment(String key) {
            return ++value;
        }
    }

    @Test
    void shouldReturnTrueWhenBelowLimit() {
        // Arrange
        redisClient.setValue(50L);

        // Act
        boolean result = accessCounterService.isValid();

        // Assert
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenAtLimit() {
        // Arrange
        redisClient.setValue(100L);

        // Act
        boolean result = accessCounterService.isValid();

        // Assert
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenAboveLimit() {
        // Arrange
        redisClient.setValue(150L);

        // Act
        boolean result = accessCounterService.isValid();

        // Assert
        assertFalse(result);
    }

    @Test
    void processShouldIncrementWhenBelowLimit() {
        // Arrange
        redisClient.setValue(99L);
        AccessEventMessage message = new AccessEventMessage();
        message.setRequestId(UUID.randomUUID());

        // Act
        accessCounterService.process(message);

        assertTrue(accessCounterService.isValid() == false);
    }

    @Test
    void processShouldNotIncrementWhenAtLimitOrAbove() {
        // Arrange
        redisClient.setValue(100L);
        AccessEventMessage message = new AccessEventMessage();
        message.setRequestId(UUID.randomUUID());

        // Act
        accessCounterService.process(message);

        // Assert
        assertFalse(accessCounterService.isValid());
    }
}
