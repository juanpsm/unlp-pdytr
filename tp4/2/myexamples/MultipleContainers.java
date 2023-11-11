package myexamples;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.core.Runtime;
import java.util.*;
import java.net.*;

public class MultipleContainers {

    public static void main(String[] args) {
        try {
            String IP = "localhost";
             // InetAddress.getLocalHost().getHostAddress();
            Runtime runtime = Runtime.instance();
            Profile mainProfile = new ProfileImpl();
            mainProfile.setParameter(Profile.CONTAINER_NAME, "Main-Container");
            mainProfile.setParameter(Profile.MAIN_HOST, IP);
            mainProfile.setParameter(Profile.MAIN_PORT, "1099");
            mainProfile.setParameter(Profile.GUI, "true");
            AgentContainer mainContainer = runtime.createMainContainer(mainProfile);


            Profile additionalProfile;
            AgentContainer additionalContainer;

            for (int i = 1; i <= 3; i++) {
                additionalProfile = new ProfileImpl();
                additionalProfile.setParameter(Profile.CONTAINER_NAME, "Container-" + i);
                additionalProfile.setParameter(Profile.MAIN_HOST, IP);
                additionalProfile.setParameter(Profile.MAIN_PORT, "1099");
                additionalContainer = runtime.createAgentContainer(additionalProfile);
            }
            
//            AgentController agente = mainContainer.createNewAgent("AgenteMovil", "myexamples.AgenteMovil", args);
  //          agente.start();
//       } catch (StaleProxyException e) {
 //           e.printStackTrace(); 
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

