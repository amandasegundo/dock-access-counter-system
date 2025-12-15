package br.com.dock.access.grpc;

import br.com.dock.access.AccessServiceGrpc;
import br.com.dock.access.AddAccessRequest;
import br.com.dock.access.AddAccessResponse;
import br.com.dock.access.Device;
import br.com.dock.access.Geolocation;
import br.com.dock.access.dto.AccessEventMessage;
import br.com.dock.access.dto.AccessEventResponse;
import br.com.dock.access.kafka.KafkaEventProducer;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {
        "grpc.block-until-shutdown=false"
    }
)
class AccessServiceGrpcIT {

    @Value("${grpc.port}")
    private int grpcPort;

    @Autowired
    private TestKafkaEventProducer kafkaEventProducer;

    private ManagedChannel channel;

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        TestKafkaEventProducer testKafkaEventProducer() {
            return new TestKafkaEventProducer();
        }
    }

    static class TestKafkaEventProducer extends KafkaEventProducer {

        private AccessEventMessage lastPayload;

        TestKafkaEventProducer() {
            super(null);
        }

        @Override
        public AccessEventResponse processMessage(AccessEventMessage payload) {
            this.lastPayload = payload;
            return new AccessEventResponse(true, "Message processed successfully.");
        }

        public AccessEventMessage getLastPayload() {
            return lastPayload;
        }
    }

    @AfterEach
    void tearDown() {
        if (channel != null) {
            channel.shutdown();
        }
    }

    @Test
    void addAccessShouldCallKafkaEventProducerAndReturnSuccess() {
        // Arrange
        channel = ManagedChannelBuilder
                .forAddress("localhost", grpcPort)
                .usePlaintext()
                .build();

        AccessServiceGrpc.AccessServiceBlockingStub stub =
                AccessServiceGrpc.newBlockingStub(channel);

        String requestId = UUID.randomUUID().toString();

        Geolocation geo = Geolocation.newBuilder()
                .setLatitude(12.34)
                .setLongitude(56.78)
                .build();

        Device device = Device.newBuilder()
                .setType("MOBILE")
                .setOs("iOS")
                .setVersion("14")
                .setIpAddress("127.0.0.1")
                .build();

        AddAccessRequest request = AddAccessRequest.newBuilder()
                .setRequestId(requestId)
                .setClientId(123)
                .setClientName("Maria")
                .setTimestamp(System.currentTimeMillis())
                .setGeolocation(geo)
                .setDevice(device)
                .build();

        // Act
        AddAccessResponse response = stub.addAccess(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals("Message processed successfully.", response.getMessage());

        AccessEventMessage sent = kafkaEventProducer.getLastPayload();

        assertNotNull(sent);
        assertEquals(requestId, sent.getRequestId().toString());
        assertEquals(123, sent.getClientId());
        assertEquals("Maria", sent.getClientName());
    }
}
