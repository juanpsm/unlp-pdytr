
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

public class FTPUtils {

    public static byte[] readFile(String path, int offset, long totalSize) {
        try {
            int chunkSize = 200_000;
            int bytesToRead = Math.min((int) (totalSize - offset), chunkSize);
            byte[] contents = new byte[bytesToRead];

            InputStream in = new FileInputStream(path);
            in.skip(offset);
            in.read(contents, 0, bytesToRead);
            in.close();

            return contents;
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return new byte[0];
        }
    }

    public static int writeFile(String path, byte[] data) {
        try {
            Files.write(Paths.get(path), data, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return data.length;
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
            return -1;
        }
    }
}

