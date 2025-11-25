package br.com.dock.access.grpc;

import br.com.dock.access.AccessServiceGrpc;
import br.com.dock.access.AddAccessRequest;
import br.com.dock.access.AddAccessResponse;
import br.com.dock.access.factory.AccessEventFactory;
import br.com.dock.access.kafka.KafkaEventProducer;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;

@Component
public class AccessServiceImpl extends AccessServiceGrpc.AccessServiceImplBase {

    private final KafkaEventProducer kafkaEventProducer;

    public AccessServiceImpl(KafkaEventProducer kafkaEventProducer) {
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @Override
    public void addAccess(AddAccessRequest request, StreamObserver<AddAccessResponse> responseObserver) {
        var eventMessage = AccessEventFactory.fromProto(request);
        kafkaEventProducer.processMessage(eventMessage);

        AddAccessResponse response = AddAccessResponse.newBuilder()
                .setSuccess(true)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
