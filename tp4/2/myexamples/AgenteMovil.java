package myexamples;
import java.net.*;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.Location;
import java.nio.file.*;
import java.util.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AgenteMovil extends Agent {
    private ArrayList<Integer> numbers = new ArrayList<>();
    private String[] containerNames = {"Main-Container", "Container-2", "Container-3"}; 
    private String filePath; 
    private int currentContainerIndex = 0 ;
    private String IP = "172.17.0.1";
    
    public void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0 && args[0] != null) {
            filePath = (String) args[0];
        } else {
           System.out.println("Arguments arrived empty");
           filePath = "./sum.txt";
        }
        if (here().getName().equals("Main-Container")) {
          currentContainerIndex++;
        }
        moveToNextContainer();
    }

    private void moveToNextContainer() {
        try {
            //IP = InetAddress.getLocalHost().getHostAddress(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (currentContainerIndex < containerNames.length) {
            String containerName = containerNames[currentContainerIndex++];
            ContainerID destination = new ContainerID();
            destination.setName(containerName);
            destination.setAddress(IP);
            destination.setPort("1099");
            System.out.println("Migrando el agente a " + destination.getID());
            doMove(destination);
        } else {
            int sum = sumNumbers();
            System.out.println("La suma total es: " + sum);
        }
    }
    protected void afterMove() {
        Location currentLocation = here();
        System.out.println("Agente migrado a " + currentLocation.getID());

        readFileAndStoreNumbers(filePath);

        moveToNextContainer();
    }

    private void readFileAndStoreNumbers(String fileName) {
        try {
            Path path = Paths.get(fileName);
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : lines) {
                try {
                    int number = Integer.parseInt(line.trim());
                    numbers.add(number);
                } catch (NumberFormatException e) {
                    System.out.println("No se pudo parsear el número: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int sumNumbers() {
        return numbers.stream().mapToInt(Integer::intValue).sum();
    }
}
