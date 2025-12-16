package br.com.dock.access.grpc;

import br.com.dock.access.AddAccessRequest;
import br.com.dock.access.AddAccessResponse;
import br.com.dock.access.dto.AccessEventMessage;
import br.com.dock.access.dto.AccessEventResponse;
import br.com.dock.access.kafka.KafkaEventProducer;
import br.com.dock.access.mapper.AccessEventMapper;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessServiceImplTest {

    @Mock
    private KafkaEventProducer kafkaEventProducer;

    @Mock
    private AccessEventMapper accessEventMapper;

    @Mock
    private StreamObserver<AddAccessResponse> responseObserver;

    @InjectMocks
    private AccessServiceImpl service;

    @Test
    void addAccessShouldMapRequestSendToKafkaAndReturnGrpcResponse() {
        // Arrange
        String requestId = UUID.randomUUID().toString();

        AddAccessRequest request = AddAccessRequest.newBuilder()
                .setRequestId(requestId)
                .setClientId(123)
                .setClientName("Alice")
                .setTimestamp(1764780100L)
                .build();

        AccessEventMessage mappedMessage = new AccessEventMessage();
        AccessEventResponse eventResponse = new AccessEventResponse(true, "Message processed successfully.");

        when(accessEventMapper.fromProto(request)).thenReturn(mappedMessage);
        when(kafkaEventProducer.processMessage(mappedMessage)).thenReturn(eventResponse);

        ArgumentCaptor<AddAccessResponse> responseCaptor = ArgumentCaptor.forClass(AddAccessResponse.class);

        // Act
        service.addAccess(request, responseObserver);

        // Assert
        verify(accessEventMapper).fromProto(request);
        verify(kafkaEventProducer).processMessage(mappedMessage);

        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();
        verifyNoMoreInteractions(accessEventMapper, kafkaEventProducer, responseObserver);

        AddAccessResponse grpcResponse = responseCaptor.getValue();
        assertNotNull(grpcResponse);
        assertTrue(grpcResponse.getSuccess());
        assertEquals("Message processed successfully.", grpcResponse.getMessage());
    }
}
