package myexamples;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.TickerBehaviour;

public class ClienteAgente extends Agent {

    private String targetFileName; // Nombre del archivo objetivo

    protected void setup() {
        // Inicialización del agente
        System.out.println("Hola! ClienteAgente " + getAID().getName() + " está listo.");

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            targetFileName = (String) args[0];
        }

        // Agregar un TickerBehaviour que envía un mensaje cada cierto tiempo
        addBehaviour(new TickerBehaviour(this, 10000) { // se ejecuta cada 10 segundos
            protected void onTick() {
                // Enviar mensaje para leer archivo
                System.out.println("Intentando leer el archivo: " + targetFileName);
                enviarMensajeDeLectura(targetFileName);

                // Enviar mensaje para escribir archivo (simulación)
                String datos = "Datos de ejemplo para escribir";
                System.out.println("Intentando escribir en el archivo: " + targetFileName);
                enviarMensajeDeEscritura(targetFileName, datos);
            }
        });
    }

    private void enviarMensajeDeLectura(String fileName) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new AID("FTPAgent", AID.ISLOCALNAME));
        msg.setContent("Leer:" + fileName);
        msg.setConversationId("operaciones-archivo");
        msg.setReplyWith("msg" + System.currentTimeMillis());
        send(msg);
    }

    private void enviarMensajeDeEscritura(String fileName, String data) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new AID("FTPAgent", AID.ISLOCALNAME));
        msg.setContent("Escribir:" + fileName + ":" + data);
        msg.setConversationId("operaciones-archivo");
        msg.setReplyWith("msg" + System.currentTimeMillis());
        send(msg);
    }

    protected void takeDown() {
        // Despedida
        System.out.println("ClienteAgente " + getAID().getName() + " terminando.");
    }
}

