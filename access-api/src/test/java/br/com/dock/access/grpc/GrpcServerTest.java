package br.com.dock.access.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrpcServerTest {

    private static final int PORT = 9090;

    @Mock
    private AccessServiceImpl accessService;

    @Test
    void runShouldStartServerAndBlockUntilShutdown() throws Exception {
        // Arrange
        GrpcServer grpcServer = new GrpcServer(accessService);
        ReflectionTestUtils.setField(grpcServer, "port", PORT);

        ServerBuilder<?> serverBuilder = mock(ServerBuilder.class, Answers.RETURNS_SELF);
        Server server = mock(Server.class);

        try (MockedStatic<ServerBuilder> mockedStatic = mockStatic(ServerBuilder.class)) {
            mockedStatic.when(() -> ServerBuilder.forPort(PORT)).thenReturn(serverBuilder);

            when(serverBuilder.build()).thenReturn(server);
            when(server.start()).thenReturn(server);

            // Act
            grpcServer.run();

            // Assert
            mockedStatic.verify(() -> ServerBuilder.forPort(PORT));
            verify(serverBuilder).addService(accessService);
            verify(serverBuilder).build();
            verify(server).start();
            verify(server).awaitTermination();
            verifyNoMoreInteractions(serverBuilder, server);
        }
    }

    @Test
    void stopShouldShutdownServerWhenServerIsNotNull() {
        // Arrange
        GrpcServer grpcServer = new GrpcServer(accessService);
        Server server = mock(Server.class);
        ReflectionTestUtils.setField(grpcServer, "server", server);

        // Act
        grpcServer.stop();

        // Assert
        verify(server).shutdown();
        verifyNoMoreInteractions(server);
    }

    @Test
    void stopShouldNotFailWhenServerIsNull() {
        GrpcServer grpcServer = new GrpcServer(accessService);
        grpcServer.stop();
    }
}
