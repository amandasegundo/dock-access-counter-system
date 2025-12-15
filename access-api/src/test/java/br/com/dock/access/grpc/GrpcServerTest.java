package br.com.dock.access.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrpcServerTest {

    private static final int PORT = 9090;

    @Mock
    private AccessServiceImpl accessService;

    @Test
    void run_shouldStartServer_andBlockUntilShutdown_whenEnabled() throws Exception {
        // Arrange
        GrpcServer grpcServer = new GrpcServer(accessService);
        ReflectionTestUtils.setField(grpcServer, "port", PORT);
        ReflectionTestUtils.setField(grpcServer, "blockUntilShutdownEnabled", true);

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
    void run_shouldStartServer_andNotBlock_whenDisabled() throws Exception {
        // Arrange
        GrpcServer grpcServer = new GrpcServer(accessService);
        ReflectionTestUtils.setField(grpcServer, "port", PORT);
        ReflectionTestUtils.setField(grpcServer, "blockUntilShutdownEnabled", false);

        ServerBuilder<?> serverBuilder = mock(ServerBuilder.class, Answers.RETURNS_SELF);
        Server server = mock(Server.class);

        try (MockedStatic<ServerBuilder> mockedStatic = mockStatic(ServerBuilder.class)) {
            mockedStatic.when(() -> ServerBuilder.forPort(PORT)).thenReturn(serverBuilder);

            when(serverBuilder.build()).thenReturn(server);
            when(server.start()).thenReturn(server);

            // Act
            grpcServer.run();

            // Assert
            verify(server).start();
            verify(server, never()).awaitTermination();
        }
    }

    @Test
    void run_shouldPropagateInterruptedException_whenAwaitTerminationIsInterrupted() throws Exception {
        // Arrange
        GrpcServer grpcServer = new GrpcServer(accessService);
        ReflectionTestUtils.setField(grpcServer, "port", PORT);
        ReflectionTestUtils.setField(grpcServer, "blockUntilShutdownEnabled", true);

        ServerBuilder<?> serverBuilder = mock(ServerBuilder.class, Answers.RETURNS_SELF);
        Server server = mock(Server.class);

        try (MockedStatic<ServerBuilder> mockedStatic = mockStatic(ServerBuilder.class)) {
            mockedStatic.when(() -> ServerBuilder.forPort(PORT)).thenReturn(serverBuilder);

            when(serverBuilder.build()).thenReturn(server);
            when(server.start()).thenReturn(server);

            doThrow(new InterruptedException("test"))
                    .when(server)
                    .awaitTermination();

            // Act & Assert
            assertThrows(InterruptedException.class, () -> grpcServer.run());

            verify(server).start();
            verify(server).awaitTermination();
        }
    }

    @Test
    void stop_shouldShutdownServer_whenServerIsNotNull() {
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
    void stop_shouldNotFail_whenServerIsNull() {
        // Arrange
        GrpcServer grpcServer = new GrpcServer(accessService);

        // Act & Assert
        assertDoesNotThrow(grpcServer::stop);
    }
}
