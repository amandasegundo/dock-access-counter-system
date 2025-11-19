package br.com.dock.access;

import br.com.dock.access.grpc.AccessServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private Server server;

    private void start() throws IOException {
        logger.info("Creating Application");

        int port = 9090;

        server = ServerBuilder
                .forPort(port)
                .addService(new AccessServiceImpl())
                .build()
                .start();

        logger.info("gRPC server started on port {}", port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server...");
            try {
                Application.this.stop();
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace(System.err);
            }
            logger.info("Server shut down.");
        }));
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args){
        try {
            Application server = new Application();
            server.start();
            server.blockUntilShutdown();
        } catch (Exception e) {
            logger.error("Failed to start server. {}", e.toString());
        }
    }
}