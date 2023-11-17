
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
                buffer = FTPUtils.readFile(localPath, transferredBytes, totalBytes);
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
            remoteLocation = new ContainerID("Main-Container", null);
            System.out.println("Migrating agent to " + remoteLocation.getID());
            doMove(remoteLocation);
        } catch (Exception e) {
            System.out.println("Migration failed: " + e.getMessage());
            doDelete();
        }
    }

    protected void afterMove() {
        Location current = here();

        System.out.println("originLocation: " +originLocation);
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
            buffer = FTPUtils.readFile(localPath, transferredBytes, totalBytes);
            transferredBytes += buffer.length;
            System.out.printf("Read %d bytes from origin\n", buffer.length);
            doMove(new ContainerID(remoteLocation.getName(), null));
    } else {
        String newLocalPath = localPath + ".cpy";
        buffer = FTPUtils.readFile(remotePath, 0, totalBytes); 
        int writtenBytes = FTPUtils.writeFile(newLocalPath, buffer);
        transferredBytes += writtenBytes;
        System.out.printf("Written %d bytes to local copy\n", writtenBytes);
        if (transferredBytes < 2 * totalBytes) {
            doMove(new ContainerID(remoteLocation.getName(), null));
        } else {
            System.out.println("Read-write operation completed successfully.");
            doDelete();
        }
    }
        } else if (current.equals(remoteLocation)) {
        FTPUtils.writeFile(remotePath, buffer);
        System.out.printf("Written %d bytes to remote\n", buffer.length);
        doMove(new ContainerID(originLocation.getName(), null));
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

