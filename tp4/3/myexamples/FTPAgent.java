package myexamples;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.*;

public class FTPAgent extends Agent {

    protected void setup() {
        System.out.println("FTPAgent " + getAID().getName() + " está listo.");

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                // Filtrar mensajes de tipo REQUEST
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = myAgent.receive(mt);

                if (msg != null) {
                    String content = msg.getContent();
                    if (content.startsWith("Leer")) {
                        // Manejar solicitud de lectura
                        String[] parts = content.split(":");
                        String fileName = parts[1];
                        String fileContent = leerArchivo(fileName);
                        ACLMessage reply = msg.createReply();
                        reply.setContent(fileContent);
                        send(reply);
                    } else if (content.startsWith("Escribir")) {
                        // Manejar solicitud de escritura
                        String[] parts = content.split(":");
                        String fileName = parts[1];
                        String data = parts[2];
                        escribirArchivo(fileName, data);
                        ACLMessage reply = msg.createReply();
                        reply.setContent("Escritura exitosa en " + fileName);
                        send(reply);
                    }
                } else {
                    block();
                }
            }
        });
    }

    private String leerArchivo(String fileName) {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                contentBuilder.append(strLine).append("\n");
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return "Error en la lectura del archivo";
        }
        return contentBuilder.toString();
    }

    private void escribirArchivo(String fileName, String data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    protected void takeDown() {
        System.out.println("FTPAgent " + getAID().getName() + " terminando.");
    }
}

