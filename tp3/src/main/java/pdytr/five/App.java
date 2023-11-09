package pdytr.five;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import time.TimeServiceGrpc;
import time.TimeServiceOuterClass.TimeRequest;
import time.TimeServiceOuterClass.TimeResponse;

import java.io.IOException;

public class App {
    private Server server;

    private void start() throws IOException {
        int port = 50051;
        server = ServerBuilder.forPort(port)
            .addService(new TimeServiceImpl())
            .build()
            .start();
        System.out.println("Server started, listening on " + port);
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

    public static void main(String[] args) throws IOException, InterruptedException {
        final App server = new App();
        server.start();
        server.blockUntilShutdown();
    }

    static class TimeServiceImpl extends TimeServiceGrpc.TimeServiceImplBase {
        @Override
        public void getTime(TimeRequest req, StreamObserver<TimeResponse> responseObserver) {
            int receivedByteSize = req.getData().size();
            System.out.println("Received data size: " + receivedByteSize + " bytes");

            long serverTimestamp = System.currentTimeMillis();
            TimeResponse response = TimeResponse.newBuilder().setServerTimestamp(serverTimestamp).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}

