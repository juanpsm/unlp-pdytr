package pdytr.example.grpc;

import io.grpc.*;

public class Client
{
  public static void main( String[] args ) throws Exception
  {
    final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080")
            .usePlaintext(true)
            .build();

    GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
    GreetingServiceOuterClass.HelloRequest request =
            GreetingServiceOuterClass.HelloRequest.newBuilder()
                    .setName("Ray")
                    .build();

    GreetingServiceOuterClass.HelloResponse response =
            stub.greeting(request);

    System.out.println(response);

    channel.shutdownNow();
  }
}
