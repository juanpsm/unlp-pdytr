package pdytr.four;

import java.io.File;
import com.google.protobuf.ByteString;
import pdytr.four.FtpServiceGrpc;
import pdytr.four.FtpServiceOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
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
        System.err.println("use: ./runclient.sh four <file>");
        System.exit(1);
      }
      
      // Channel is the abstraction to connect to a service endpoint
      // Let's use plaintext communication because we don't have certs
      final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080")
        .usePlaintext(true)
        .build();

      try {
        // Create a file to read from args
        File file = new File(Client.database, args[0]);

        // It is up to the client to determine whether to block the call
        // Here we create a blocking stub, but an async stub,
        // or an async stub with Future are always possible.
        FtpServiceGrpc.FtpServiceBlockingStub stub = FtpServiceGrpc.newBlockingStub(channel);

        int pos = 0;
        int totalBytes = (int)file.length();
        int chunkSize = 2;
        FtpServiceOuterClass.ReadResponse readResponse = null;

        while (pos < totalBytes) {
            // Check if endPos is not out of bound
            int endPos = Math.min(pos + chunkSize, totalBytes); 
            // Create a ReadRequest to read the file
            FtpServiceOuterClass.ReadRequest readRequest =
              FtpServiceOuterClass.ReadRequest.newBuilder()
                .setName(file.getName())
                .setPos(pos)
                .setReadBytes(endPos - pos)
                .build();

            // Finally, make the call using the stub
            FtpServiceOuterClass.ReadResponse readResponse = null;
            readResponse =  stub.read(readRequest);
            if (readResponse != null) System.out.println(readResponse);

            // Move the position to the next chunk
            pos += chunkSize;
        }

        // To test the writing of the file, we will write the
        // same file just read.

        pos = 0;
        totalBytes = readResponse.getContent().size();
        chunkSize = 2;

        // System.out.println("SIZE: " + totalBytes);
        while (pos < totalBytes) {
            // Check if endPos is not out of bound
            int endPos = Math.min(pos + chunkSize, totalBytes); 
            // Create the write request
            WriteRequest writeRequest = WriteRequest.newBuilder()
                .setName("output")
                .setBuffer(ByteString.copyFrom(readResponse.getContent().substring(pos, endPos).toByteArray()))
                .setWriteBytes(endPos - pos)
                .build();

            // Finally, make the call using the stub
            WriteResponse writeResponse = stub.write(writeRequest);

            // Move the position to the next chunk
            pos += chunkSize;
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
