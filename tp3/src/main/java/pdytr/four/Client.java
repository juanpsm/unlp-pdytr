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
    private static final String database = "src" + File.separator
                                        + "main" + File.separator
                                        + "java" + File.separator
                                        + "pdytr" + File.separator
                                        + "four" + File.separator
                                        + "files";

    public static void main( String[] args ) throws Exception
    {

      if (args.length != 1) {
        System.err.println("usage: Client <filename>");
        System.exit(1);
      }
      
      // Channel is the abstraction to connect to a service endpoint
      // Let's use plaintext communication because we don't have certs
      final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080")
        .usePlaintext(true)
        .build();

      try {
        // create file to read
        File file = new File(Client.database, args[0]);

        // create the stub
        FtpServiceBlockingStub stub = newBlockingStub(channel);

        // create a read request of a maximum size of 1MB
        // (there's no need to read the whole file)
        ReadRequest readRequest = ReadRequest.newBuilder()
            .setName(file.getName())
            .setPos(0)
            .setReadBytes((int)file.length() < 1024 ? (int)file.length() : 1024)
            .build();

        // make the call to read using the stub (expect a response from the server)
        ReadResponse readResponse = stub.read(readRequest);

        /*
          * now that the readRequest holds some data, make the server write it
          * (as every client tries to write to the same file, this should boom boom most of the time)
        */

        int pos = 0;
        while (pos < readResponse.getContent().size()) {
            // create the write request
            WriteRequest writeRequest = WriteRequest.newBuilder()
                .setName("file.txt")
                .setBuffer(ByteString.copyFrom(readResponse.getContent().substring(pos, pos + 2).toByteArray()))
                .setWriteBytes(2)
                .build();

            // make the call to write using the stub (expect a response from the server)
            WriteResponse writeResponse = stub.write(writeRequest);

            // update the pos
            pos += 2;
        }

      } catch (Exception e) {
          e.printStackTrace();

      } finally {
          // A Channel should be shutdown before stopping the process.
          channel.shutdownNow();
          channel.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
      }
    }
}
