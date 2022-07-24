package main;

import javax.swing.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Yana on 27/05/2017.
 */
public class Interaction extends Thread {
    private JTextField enterField;
    private JTextArea displayArea;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket clientSocket;
    private Server server;
    private int id;

    public Interaction(JTextArea displayArea, JTextField enterField, Socket clientSocket, Server server, int id) {
        this.id = id;
        this.enterField = enterField;
        this.displayArea = displayArea;
        this.clientSocket = clientSocket;
        this.server = server;
    }

    public void run() {
        try {
            getStreams();
            processConnection();
            closeConnection();
        } catch (EOFException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getStreams() throws IOException {
        output = new ObjectOutputStream(clientSocket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(clientSocket.getInputStream());
        displayArea.append("\nEstablished I/O streams\n");
        String message = "SERVER>>> Connection Successful";
        output.writeObject(message);
        output.flush();
        server.revalidate();
    }

    private void processConnection() throws IOException {
        String message = "";
        do {
            try {
                message = (String) input.readObject();
                displayArea.append("\n" + message);
                displayArea.setCaretPosition(displayArea.getText().length());
                server.broadcast(id, message);
            } catch (ClassNotFoundException e) {
                displayArea.append("\nUnknown object type received");
            }
            server.revalidate();
        } while (!message.contains("TERMINATE"));
    }

    private void closeConnection() throws IOException {
        displayArea.append("\nClient terminated connection");
        output.close();
        input.close();
        clientSocket.close();
        server.revalidate();
    }

    public void sendData(String message) {
        try {
            output.writeObject(message);
            output.flush();
        } catch (IOException e) {
            displayArea.append("\nError writing object");
        }
        server.revalidate();
    }
}
