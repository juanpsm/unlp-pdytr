package myexamples;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.*;

public class ServidorAgente extends Agent {

    protected void setup() {
        System.out.println("ServidorAgente " + getAID().getName() + " está listo.");

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = receive(mt);

                if (msg != null) {
                    String content = msg.getContent();
                    if (content.startsWith("Leer")) {
                        String[] parts = content.split(":");
                        String fileName = parts[1];
                        String fileContent = leerArchivo(fileName);
                        ACLMessage reply = msg.createReply();
                        reply.setContent(fileContent);
                        send(reply);
                    } else if (content.startsWith("Escribir")) {
                        String[] parts = content.split(":");
                        String fileName = parts[1];
                        String data = parts[2];
                        boolean result = escribirArchivo(fileName, data);
                        ACLMessage reply = msg.createReply();
                        reply.setContent(result ? "Escritura exitosa" : "Error en escritura");
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
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String strLine;
            while ((strLine = br.readLine()) != null) {
                contentBuilder.append(strLine).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return "Error en la lectura del archivo";
        }
        return contentBuilder.toString();
    }

    private boolean escribirArchivo(String fileName, String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(data);
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
            return false;
        }
        return true;
    }

    protected void takeDown() {
        System.out.println("ServidorAgente " + getAID().getName() + " terminando.");
    }
}

