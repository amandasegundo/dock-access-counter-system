package br.com.dock.access.kafka;

import br.com.dock.access.client.RedisClient;
import br.com.dock.access.service.AccessCounterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "app.access-limit=100",
                "grpc.enabled=false",
                "app.kafka.topic=access-topic",
                "spring.kafka.consumer.group-id=access-consumer-group-test",
                "spring.kafka.listener.auto-startup=false"
        }
)
class KafkaEventConsumerIT {

    @Autowired
    private KafkaEventConsumer kafkaEventConsumer;

    @Autowired
    private TestRedisClient redisClient;

    @Autowired
    private AccessCounterService accessCounterService;

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
    void shouldIncrementCounterWhenValidJsonMessage() {
        // Arrange
        redisClient.setValue(0L);

        String json = """
            {
               "clientId": 58217,
               "clientName": "Maria",
               "device": {
                 "ipAddress": "127.0.0.1",
                 "os": "iOS",
                 "type": "MOBILE",
                 "version": "26"
               },
               "geolocation": {
                 "latitude": -26.9189,
                 "longitude": -49.0661
               },
               "requestId": "9f722bc0-5b8a-4e52-95e6-182df45fbe4e",
               "timestamp": 1763559532
            }
            """;

        // Act
        kafkaEventConsumer.listen(json);

        // Assert
        assertEquals(1L, redisClient.getValue());
    }

    @Test
    void shouldNotIncrementCounterWhenInvalidJson() {
        // Arrange
        redisClient.setValue(0L);
        String invalidJson = "{ invalid json";

        // Act
        kafkaEventConsumer.listen(invalidJson);

        // Assert
        assertEquals(0L, redisClient.getValue());
    }
}