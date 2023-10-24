package pdytr.example.grpc;
import io.grpc.*;

public class App
{
  public static void main( String[] args ) throws Exception
  {
    Server server = ServerBuilder.forPort(8080)
            .addService(new GreetingServiceImpl())
            .build();
    Thread.sleep(10000); // sleep for 10 seconds
    server.start();
    System.out.println("Server started");
    server.awaitTermination();
  }
}

