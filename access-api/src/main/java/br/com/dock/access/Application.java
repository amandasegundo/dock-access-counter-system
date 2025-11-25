package br.com.dock.access;

import br.com.dock.access.grpc.AccessServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final AccessServiceImpl accessService;

    @Value("${grpc.port}")
    private int port;

    private Server server;

    public Application(AccessServiceImpl accessService) {
        this.accessService = accessService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        start();
        blockUntilShutdown();
    }

    private void start() throws IOException {
        log.info("Starting gRPC server...");

        server = ServerBuilder
                .forPort(port)
                .addService(accessService)
                .build()
                .start();

        log.info("gRPC server started on port {}", port);
    }

    @PreDestroy
    public void stop() {
        log.info("Shutting down gRPC server...");
        if (server != null) {
            server.shutdown();
        }
        log.info("gRPC server shut down.");
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}