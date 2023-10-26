package pdytr.four;

import java.io.File;

import com.google.protobuf.ByteString;
import pdytr.four.FtpServiceGrpc.FtpServiceBlockingStub;
import static pdytr.four.FtpServiceGrpc.newBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import pdytr.four.FtpServiceOuterClass.ReadRequest;
import pdytr.four.FtpServiceOuterClass.ReadResponse;
import pdytr.four.FtpServiceOuterClass.WriteRequest;
import pdytr.four.FtpServiceOuterClass.WriteResponse;

public class Client
{
    public static void main( String[] args ) throws Exception
    {
      // Channel is the abstraction to connect to a service endpoint
      // Let's use plaintext communication because we don't have certs
      final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080")
        .usePlaintext(true)
        .build();

      // It is up to the client to determine whether to block the call
      // Here we create a blocking stub, but an async stub,
      // or an async stub with Future are always possible.
      GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
      GreetingServiceOuterClass.HelloRequest request =
        GreetingServiceOuterClass.HelloRequest.newBuilder()
          .setName("Ray")
          .build();

      // Finally, make the call using the stub
      GreetingServiceOuterClass.HelloResponse response = 
        stub.greeting(request);

      System.out.println(response);

      // A Channel should be shutdown before stopping the process.
      channel.shutdownNow();
    }
}
