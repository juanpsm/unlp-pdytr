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
    private List<Integer> containerSums = new ArrayList<>();
    private String[] containerNames = {"Main-Container", "Container-1", "Container-2", "Container-3"}; 
    private String filePath; 
    private int currentContainerIndex = 0 ;
    private String IP = "localhost";
    
    public void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0 && args[0] != null) {
            filePath = args[0].toString();
        }
        if (here().getName().equals("Main-Container")) {
          currentContainerIndex++;
        }
        moveToNextContainer();
    }

    private void moveToNextContainer() {
          String containerName = containerNames[currentContainerIndex % containerNames.length];
          currentContainerIndex++;
          ContainerID destination = new ContainerID();
          destination.setName(containerName);
          destination.setAddress(IP);
          destination.setPort("1099");
          System.out.println("Migrando el agente a " + destination.getID());
          doMove(destination);
    }
    protected void afterMove() {
        Location currentLocation = here();
        System.out.println("Agente migrado a " + currentLocation.getID());

        if (!currentLocation.getName().equals("Main-Container")) {
            int containerSum = readFileAndStoreNumbers(filePath);
            containerSums.add(containerSum);
            System.out.println("Suma en " + currentLocation.getName() + ": " + containerSum);
        }

        if (currentContainerIndex <= containerNames.length) {
            moveToNextContainer();
        } else if (currentLocation.getName().equals("Main-Container")) {
            int totalSum = sumNumbers();
            System.out.println("Suma total en todos los contenedores: " + totalSum);
            doDelete();
        }
    }
    private int readFileAndStoreNumbers(String fileName) {
       try {
            Path path = Paths.get(fileName);
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            return lines.stream()
                        .mapToInt(line -> {
                            try {
                                return Integer.parseInt(line.trim());
                            } catch (NumberFormatException e) {
                                System.out.println("No se pudo parsear el número: " + line);
                                return 0;
                            }
                        })
                        .sum();
        } catch (IOException e) {
            System.out.println("Error leyendo el archivo: " + e.getMessage());
            return 0;
        }
    }

    private int sumNumbers() {
        return containerSums.stream().mapToInt(Integer::intValue).sum();
    }
}
