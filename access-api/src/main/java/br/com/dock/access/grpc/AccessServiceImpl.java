package br.com.dock.access.grpc;

import br.com.dock.access.AccessServiceGrpc;
import br.com.dock.access.AddAccessRequest;
import br.com.dock.access.AddAccessResponse;
import br.com.dock.access.factory.AccessEventFactory;
import io.grpc.stub.StreamObserver;

public class AccessServiceImpl extends AccessServiceGrpc.AccessServiceImplBase {

    @Override
    public void addAccess(AddAccessRequest request, StreamObserver<AddAccessResponse> responseObserver) {
        var event = AccessEventFactory.fromProto(request);
        System.out.println(event);

        AddAccessResponse response = AddAccessResponse.newBuilder()
                .setSuccess(true)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
