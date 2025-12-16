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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "app.access-limit=3"
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

        public long getValue() {
            return value;
        }

        @Override
        public long incrementWithLimit(String key, long limit) {
            if (value >= limit) {
                return -1L;
            }
            return ++value;
        }
    }

    @Test
    void processShouldIncrementWhenBelowLimit() {
        // Arrange
        redisClient.setValue(1L);
        AccessEventMessage message = new AccessEventMessage();
        message.setRequestId(UUID.randomUUID());

        // Act
        accessCounterService.process(message);

        // Assert
        assertEquals(2L, redisClient.getValue());
    }

    @Test
    void processShouldNotIncrementWhenAtLimit() {
        // Arrange
        redisClient.setValue(3L);
        AccessEventMessage message = new AccessEventMessage();
        message.setRequestId(UUID.randomUUID());

        // Act
        accessCounterService.process(message);

        // Assert
        assertEquals(3L, redisClient.getValue());
    }

    @Test
    void processShouldNotIncrementWhenAboveLimit() {
        // Arrange
        redisClient.setValue(5L);
        AccessEventMessage message = new AccessEventMessage();
        message.setRequestId(UUID.randomUUID());

        // Act
        accessCounterService.process(message);

        // Assert
        assertEquals(5L, redisClient.getValue());
    }
}