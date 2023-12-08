package pdytr.four;

import java.io.File;
import com.google.protobuf.ByteString;
import pdytr.four.FtpServiceGrpc;
import pdytr.four.FtpServiceOuterClass;
import io.grpc.*;

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

        byte[] data = new byte[totalBytes];
        FtpServiceOuterClass.ReadResponse readResponse = null;

        System.out.println("SIZE: " + totalBytes);
        while (pos < totalBytes) {
            // Check if endPos is not out of bound
            System.out.println("POS: " + pos);
            int endPos = Math.min(pos + chunkSize, totalBytes);
            System.out.println("END: " + endPos);
            System.out.println("setReadBytes: " + (endPos - pos));
            // Create a ReadRequest to read the file
            FtpServiceOuterClass.ReadRequest readRequest =
              FtpServiceOuterClass.ReadRequest.newBuilder()
                .setName(file.getName())
                .setPos(pos)
                .setReadBytes(endPos - pos)
                .build();

            // Finally, make the call using the stub
            readResponse =  stub.read(readRequest);
            if (readResponse != null) System.out.println(readResponse);
            
            // readResponse.getContent().copyTo(data);

            ByteString.ByteIterator it = readResponse.getContent().iterator();
            for (int i = 0; i < readResponse.getContent().size(); ++i) {
              byte value = it.nextByte();
              data[pos+i] = value;
              System.out.println("data[" + (pos+i) + "]: " + value);
            }
            // System.out.println("data: " + java.util.Arrays.toString(data));
            // Move the position to the next chunk
            pos += chunkSize;
        }

        // System.out.println("data: " + java.util.Arrays.toString(data));


        // To test the writing of the file, we will write the
        // same file just read.

        pos = 0;
        chunkSize = 2;

        // System.out.println("SIZE: " + totalBytes);
        while (pos < totalBytes) {
            // Check if endPos is not out of bound
            int endPos = Math.min(pos + chunkSize, totalBytes); 
            // Create the write request
            FtpServiceOuterClass.WriteRequest writeRequest =
              FtpServiceOuterClass.WriteRequest.newBuilder()
                .setName("output")
                .setBuffer(ByteString.copyFrom(data, pos, chunkSize))
                .setWriteBytes(endPos - pos)
                .build();

            // Finally, make the call using the stub
            FtpServiceOuterClass.WriteResponse writeResponse =
              stub.write(writeRequest);
            if (writeResponse != null) System.out.println(writeResponse);
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
