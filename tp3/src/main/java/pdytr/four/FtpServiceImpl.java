package pdytr.four;

import io.grpc.stub.StreamObserver;
import com.google.protobuf.ByteString;

import pdytr.four.FtpServiceOuterClass.ReadRequest;
import pdytr.four.FtpServiceOuterClass.ReadResponse;
import pdytr.four.FtpServiceOuterClass.WriteRequest;
import pdytr.four.FtpServiceOuterClass.WriteResponse;
import pdytr.four.FtpServiceGrpc.FtpServiceImplBase;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.io.File;

public class FtpServiceImpl extends FtpServiceImplBase {
  private static final String database = "src" + File.separator
                                        + "main" + File.separator
                                        + "java" + File.separator
                                        + "pdytr" + File.separator
                                        + "four" + File.separator
                                        + "files";

  public void read(ReadRequest request,
        StreamObserver<ReadResponse> responseObserver) {

    try {
      File file = new File(this.database, request.getName());

      // Check if file exists
      if (file.exists() && file.isFile()) {

        // Create stream
        FileInputStream stream = new FileInputStream(file);

        // create data array
        byte[] data = new byte[(int)(request.getReadBytes())];

        // int rc = stream.read(data);
        // while(rc != -1)
        // {
        //   // rc should contain the number of bytes read in this operation.
        //   // do stuff...

        //   // next read
        //   rc = stream.read(data); 
        // }

        System.out.println("request.getPos(): " + request.getPos());
        System.out.println("request.getReadBytes(): " + request.getReadBytes());

        // Read file into array
        stream.skip(request.getPos());
        stream.read(data);
        System.out.println("data: " + Arrays.toString(data));
        // Load data into a response
        responseObserver.onNext(ReadResponse.newBuilder().setContent(ByteString.copyFrom(data)).setLength(request.getReadBytes()).build());

        // Close stream
        stream.close();

      } else {
          // If file doesn't exist load an empty response
          responseObserver.onNext(ReadResponse.newBuilder().setContent(null).setLength(0).build());
      }

    } catch (Exception e) {
        e.printStackTrace();

    } finally {
        // When you are done, you must call onCompleted.
        responseObserver.onCompleted();
    }
  }

  public void write(WriteRequest request, StreamObserver<WriteResponse> responseObserver)
  {
    try {
        File file = new File(this.database, request.getName());
        FileOutputStream stream = new FileOutputStream(file, true);

        stream.write(request.getBuffer().toByteArray(), 0, request.getWriteBytes());
        stream.close();

        responseObserver.onNext(WriteResponse.newBuilder().setLength(request.getWriteBytes()).build());

    } catch (Exception e) {
        e.printStackTrace();

    } finally {
        responseObserver.onCompleted();
    }
  }
}