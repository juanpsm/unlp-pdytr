package pdytr.five;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import time.TimeServiceGrpc;
import time.TimeServiceOuterClass.TimeRequest;
import time.TimeServiceOuterClass.TimeResponse;

public class Client {
    private final TimeServiceGrpc.TimeServiceBlockingStub blockingStub;

    public Client(ManagedChannel channel) {
        blockingStub = TimeServiceGrpc.newBlockingStub(channel);
    }

    public void getTime(int byteSize) {
        byte[] dataToSend = new byte[byteSize];
        long start = System.currentTimeMillis();
        TimeRequest request = TimeRequest.newBuilder()
          .setClientTimestamp(start)
          .setData(ByteString.copyFrom(dataToSend))
          .build();
        TimeResponse response = blockingStub.getTime(request);
        long end = System.currentTimeMillis();
        long serverTimestamp = response.getServerTimestamp();
        double roundTripTimeInSeconds = (end - start) / 1000.0 / 2;
        String formattedTime = String.format("%.4f seconds", roundTripTimeInSeconds);
        System.out.println("trip time: " + formattedTime);
        System.out.println("Server timestamp: " + serverTimestamp);
    }

    public static void main(String[] args) throws Exception {
        String target = "localhost";
        ManagedChannel channel = ManagedChannelBuilder.forAddress(target, 50051)
            .usePlaintext(true)
            .build();
        int byteSize = 1024;
        if (args.length > 0) {
            try {
                byteSize = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid byte size argument, using default value: " + byteSize);
            }
        }
        try {
            Client client = new Client(channel);
            client.getTime(byteSize);
        } finally {
            channel.shutdownNow().awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
        }
    }
}

