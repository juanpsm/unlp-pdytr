package myexamples;
import java.net.*;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.Location;
import java.nio.file.*;
import java.util.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import jade.util.leap.Serializable;

public class AgenteMovil extends Agent {
    private String[] containerNames;
    private ArrayList<ContainerInfo> containerInfos = new ArrayList<>();
    {
      containerNames[0] = "Main-Container";
      for (int i = 0; i < 10; i++) {
        containerNames[i] = "Container-" + (i - 1);
      }
    }
    private String filePath; 
    private int currentContainerIndex = 0 ;
    private String IP = "localhost";
    private long startTime;    
    
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
            
          try {
            startTime = System.currentTimeMillis();
            doMove(destination);
          } catch (Exception e) {
            System.out.println("Error, no se pudo migrar el agente");
          }
    }
    protected void afterMove() {
        Location currentLocation = here();
        System.out.println("Agente " + getLocalName() + " migrado a " + currentLocation.getID());
        System.out.println("Agente calificado: " + getName());
        
        if (!currentLocation.getName().equals("Main-Container")) {
          long startContainerTime = System.currentTimeMillis();
          ContainerInfo currentContainerInfo = new ContainerInfo();
          currentContainerInfo.setFreeMemory(java.lang.Runtime.getRuntime().freeMemory());
          currentContainerInfo.setName(currentLocation.getName());

          containerInfos.add(currentContainerInfo);
          long finishContainerTime = System.currentTimeMillis() - startContainerTime;
          currentContainerInfo.setProcessingTime(finishContainerTime);
          OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
          currentContainerInfo.setProcessLoad(osBean.getProcessCpuLoad());
        }

        if (currentContainerIndex <= containerNames.length) {
            moveToNextContainer();
        } else if (currentLocation.getName().equals("Main-Container")) {
           calculateTotalTime();
           for (ContainerInfo container : containerInfos) System.out.println(container.getInfoAsString());
           doDelete();
        }
    }

    private void calculateTotalTime() {
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Tiempo total transcurrido: " + totalTime + "ms");
    }


	private class ContainerInfo implements Serializable {
		private long freeMemory;
		private String name;
		private long processingTime;
		private double processLoad;

		public long getFreeMemory() {
			return freeMemory;
		}

		public void setFreeMemory(long freeMemory) {
			this.freeMemory = freeMemory;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public long getProcessingTime() {
			return processingTime;
		}

		public void setProcessingTime(long processingTime) {
			this.processingTime = processingTime;
		}

		public void setProcessLoad(double processLoad){
			this.processLoad = processLoad;
		}

		public double getProcessLoad() {
			return processLoad;
		}
                
		public String getInfoAsString() {
			String str = "";

			str += "Container " + getName() + " information:";
			str += "\n\tFree memory: " + (getFreeMemory() / 1024) / 1024 + "Mb";
			str += "\n\tProcessing time: " + getProcessingTime() + "ms";
			str += "\n\tProcessing load: " + processLoad + "%";
                        
			return str;
		}
	}

}
