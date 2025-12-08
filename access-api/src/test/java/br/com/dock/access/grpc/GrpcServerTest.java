package br.com.dock.access.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(OutputCaptureExtension.class)
class GrpcServerTest {

    private static final int PORT = 9090;

    @Mock
    private AccessServiceImpl accessService;

    @Test
    void runShouldStartServerAndBlockUntilShutdown(CapturedOutput output) throws Exception {
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

            assertTrue(output.getOut().contains("Starting gRPC server..."));
            assertTrue(output.getOut().contains("gRPC server started on port " + PORT));
        }
    }

    @Test
    void stopShouldShutdownServerWhenServerIsNotNull(CapturedOutput output) {
        // Arrange
        GrpcServer grpcServer = new GrpcServer(accessService);
        Server server = mock(Server.class);
        ReflectionTestUtils.setField(grpcServer, "server", server);

        // Act
        grpcServer.stop();

        // Assert
        verify(server).shutdown();
        verifyNoMoreInteractions(server);

        assertTrue(output.getOut().contains("Shutting down gRPC server..."));
        assertTrue(output.getOut().contains("gRPC server shut down."));
    }

    @Test
    void stopShouldNotFailWhenServerIsNull() {
        GrpcServer grpcServer = new GrpcServer(accessService);
        grpcServer.stop();
    }
}
