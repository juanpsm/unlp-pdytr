
import jade.core.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

public class FTPAgent extends Agent {
    private Location currentLocation;
    private Location originLocation;
    private Location remoteLocation;
    private String operation;
    private String localPath;
    private String remotePath;
    private byte[] buffer;
    private long totalBytes;
    private int transferredBytes = 0;
    private long totalRemoteBytes;
    private int transferredRemoteBytes = 0;
    private byte[] originBuffer;
    private byte[] remoteBuffer;
    
    public void setup() {
        Object[] args = getArguments();
        processArguments(args);
        currentLocation = here();
        originLocation = currentLocation;
        initiateMigration();
    }

private void processArguments(Object[] args) {
    if (args.length != 3) {
        System.out.println("Incorrect number of arguments. Expected format: [operation] [localPath] [remotePath]");
        doDelete();
        return;
    }

    operation = (String) args[0];
    localPath = (String) args[1];
    remotePath = (String) args[2];

    try {
        switch (operation) {
            case "write":
                totalBytes = Files.size(Paths.get(localPath));
                buffer = FTPUtils.readFile(localPath, transferredBytes, totalBytes);
                break;
            case "read":
                totalBytes = Files.size(Paths.get(remotePath));
                break;
            case "readwrite":
                totalBytes = Files.size(Paths.get(localPath));
                totalRemoteBytes = totalBytes;
                originBuffer = FTPUtils.readFile(localPath, transferredBytes, totalBytes);
                remoteBuffer = originBuffer;
                break;
            default:
                System.out.println("Invalid operation: " + operation);
                doDelete();
        }
    } catch (IOException e) {
        e.printStackTrace();
        doDelete();
    }
}

    private void initiateMigration() {
        try {
            originLocation = new ContainerID("Main-Container", null);
            remoteLocation = new ContainerID("Container-1", null);
            System.out.println("Migrating agent to " + originLocation.getID());
            doMove(originLocation);
        } catch (Exception e) {
            System.out.println("Migration failed: " + e.getMessage());
            doDelete();
        }
    }

    protected void afterMove() {
        Location current = here();

        System.out.println("I am at: " + current);
        if ("readwrite".equals(operation)) {
            handleReadWrite(current);
        } else if ("write".equals(operation)) {
            handleWrite(current);
        } else if ("read".equals(operation)) {
            handleRead(current);
        }
    }



private void handleReadWrite(Location current) {
    if (current.equals(originLocation)) {
        if (transferredBytes < totalBytes) {
            System.out.println("Reading from origin!");
            originBuffer = FTPUtils.readFile(localPath, transferredBytes, totalBytes);
            transferredBytes += originBuffer.length;
            System.out.printf("Read %d bytes from origin\n", originBuffer.length);
            doMove(new ContainerID(remoteLocation.getName(), null));
        } else {
            System.out.println("Writing copy!");
            String newLocalPath = localPath + ".cpy";
            int writtenBytes = FTPUtils.writeFile(newLocalPath, originBuffer);
            System.out.printf("Written %d bytes to local copy\n", writtenBytes);
            if (transferredBytes >= totalBytes) {
                System.out.println("Read-write operation completed successfully.");
                doDelete();
            }
       }
    } else if (current.equals(remoteLocation)) {
         System.out.print("Writing in remote!");
         if (Files.notExists(Paths.get(remotePath))) {
            int writtenBytes = FTPUtils.writeFile(remotePath, remoteBuffer);
            System.out.printf("Written %d bytes to remote\n", writtenBytes);
            System.out.println("Reading remote file!");
            remoteBuffer = FTPUtils.readFile(remotePath, transferredRemoteBytes, totalRemoteBytes);
            transferredRemoteBytes += remoteBuffer.length;
            System.out.printf("Read %d bytes from remote\n", remoteBuffer.length);
            doMove(new ContainerID(originLocation.getName(), null));
       }
    }
}
private void handleWrite(Location current) {
    if (!current.getName().equals(currentLocation.getName())) {
        FTPUtils.writeFile(remotePath, buffer);
        System.out.printf("Written %d bytes to remote\n", buffer.length);
        if (transferredBytes < totalBytes) {
            buffer = FTPUtils.readFile(localPath, transferredBytes, totalBytes);
            transferredBytes += buffer.length;
            doMove(new ContainerID(currentLocation.getName(), null));
        } else {
            System.out.println("Write operation completed successfully.");
        }
    }
}

private void handleRead(Location current) {
    if (!current.getName().equals(currentLocation.getName())) {
        buffer = FTPUtils.readFile(remotePath, transferredBytes, totalBytes);
        transferredBytes += buffer.length;
        System.out.printf("Read %d bytes from remote\n", buffer.length);
        doMove(new ContainerID(currentLocation.getName(), null));
    } else {
        FTPUtils.writeFile(localPath, buffer);
        System.out.printf("Written %d bytes to local\n", buffer.length);
        if (transferredBytes < totalBytes) {
            doMove(new ContainerID("Main-Container", null));
        } else {
            System.out.println("Read operation completed successfully.");
        }
    }
}

}

