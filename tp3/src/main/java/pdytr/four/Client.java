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
        System.err.println("usage: runclient.sh four <filename>");
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

        // create a read request to read file
        ReadRequest readRequest = ReadRequest.newBuilder()
            .setName(file.getName())
            .setPos(0)
            .setReadBytes((int)file.length())
            .build();

        // make the call to read using the stub (expect a response from the server)
        ReadResponse readResponse = stub.read(readRequest);

        // write file just readen

        int pos = 0;
        int totalBytes = readResponse.getContent().size();
        int chunkSize = 2;

        // System.out.println("SIZE: " + totalBytes);
        while (pos < totalBytes) {
            int endPos = Math.min(pos + chunkSize, totalBytes); 
            // create the write request
            // System.out.println("POS: " + pos + " END: " + endPos);
            WriteRequest writeRequest = WriteRequest.newBuilder()
                .setName("output")
                .setBuffer(ByteString.copyFrom(readResponse.getContent().substring(pos, endPos).toByteArray()))
                .setWriteBytes(endPos - pos)
                .build();

            // make the call to write using the stub (expect a response from the server)
            WriteResponse writeResponse = stub.write(writeRequest);

            // update the pos
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
