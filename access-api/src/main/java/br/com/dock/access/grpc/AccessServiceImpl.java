package br.com.dock.access.grpc;

import br.com.dock.access.AccessServiceGrpc;
import br.com.dock.access.AddAccessRequest;
import br.com.dock.access.AddAccessResponse;
import br.com.dock.access.kafka.KafkaEventProducer;
import br.com.dock.access.mapper.AccessEventMapper;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;

@Component
public class AccessServiceImpl extends AccessServiceGrpc.AccessServiceImplBase {

    private final KafkaEventProducer kafkaEventProducer;
    private final AccessEventMapper accessEventMapper;

    public AccessServiceImpl(KafkaEventProducer kafkaEventProducer,
                             AccessEventMapper accessEventMapper) {
        this.kafkaEventProducer = kafkaEventProducer;
        this.accessEventMapper = accessEventMapper;
    }

    @Override
    public void addAccess(AddAccessRequest request, StreamObserver<AddAccessResponse> responseObserver) {
        var eventMessage = accessEventMapper.fromProto(request);
        var eventResponse = kafkaEventProducer.processMessage(eventMessage);

        AddAccessResponse response = AddAccessResponse.newBuilder()
                .setSuccess(eventResponse.isSuccess())
                .setMessage(eventResponse.getMessage())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
