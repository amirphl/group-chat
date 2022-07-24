package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Yana on 23/05/2017.
 */
public class Server extends JFrame {
    private JTextField enterField;
    private JTextArea displayArea;
    private ServerSocket serverSocket;
    private int counter = 0;
    private Interaction[] interactions;
    private final int sizeOfConnections = 100;
    private int serverPort;

    public Server(int serverPort) {
        super("server");
        this.serverPort = serverPort;
        interactions = new Interaction[sizeOfConnections];
        Container container = getContentPane();
        enterField = new JTextField();
        enterField.setEditable(true);
        Handler handler = new Handler();
        enterField.addActionListener(handler);
        container.add(enterField, BorderLayout.NORTH);
        displayArea = new JTextArea();
        container.add(new JScrollPane(displayArea), BorderLayout.CENTER);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(450, 500);
        setVisible(true);
    }

    private class Handler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            sendData(event.getActionCommand());
        }
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(serverPort, 100);
            while (true) {
                waitForConnection();
            }
        } catch (EOFException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitForConnection() throws IOException {
        displayArea.setText("Waiting for connection\n");
        Socket clientSocket = serverSocket.accept();
        displayArea.append("Connection " + (counter + 1) + " received from: " +
                clientSocket.getInetAddress().getHostName());
        // TODO handle overflow
        interactions[counter] = new Interaction(displayArea, enterField, clientSocket, this, counter);
        interactions[counter].start();
        ++counter;
        revalidate();
    }

    private void closeConnection() throws IOException {
        displayArea.append("\nclosed connection");
        enterField.setEnabled(false);
        serverSocket.close();
        revalidate();
    }

    private void sendData(String message) {
        displayArea.append("\nSERVER>>> " + message);
        for (int i = 0; i < counter; i++) {
            interactions[i].sendData("SERVER>>> " + message);
        }
    }
    public void broadcast(int exclude, String message) {
        for (int i = 0; i < counter; i++) {
            if (i == exclude) {
                continue;
            }
            interactions[i].sendData(message);
        }
    }
}
