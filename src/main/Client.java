package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Yana on 23/05/2017.
 */
public class Client extends JFrame {
    private JTextField enterField;
    private JTextArea displayArea;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket clientSocket;
    private String serverIP;
    private int serverPort;
    private String name;

    public Client(String serverIP, int serverPort, String name) {
        super(name);
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.name = name;
        Container container = getContentPane();
        enterField = new JTextField();
        enterField.setEditable(true);
        enterField.addActionListener(new Handler());
        container.add(enterField, BorderLayout.NORTH);
        displayArea = new JTextArea();
        container.add(new JScrollPane(displayArea), BorderLayout.CENTER);
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
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        try {
            connectToServer();
            getStreams();
            processConnection();
            closeConnection();
        } catch (EOFException e) {
            System.out.println("Server terminated connection.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectToServer() throws IOException {
        displayArea.setText("Attempting connection\n");
        clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress(serverIP, serverPort));
        displayArea.append("Connected to: " + clientSocket.getInetAddress().getHostName());
        revalidate();
    }

    private void getStreams() throws IOException {
        output = new ObjectOutputStream(clientSocket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(clientSocket.getInputStream());
        displayArea.append("\nEstablished I/O streams\n");
        revalidate();
    }

    private void processConnection() throws IOException {
        String message = "";
        do {
            try {
                message = (String) input.readObject();
                displayArea.append("\n" + message);
                displayArea.setCaretPosition(displayArea.getText().length());
            } catch (ClassNotFoundException e) {
                displayArea.append("\nUnknown object type received");
            }
            revalidate();
        } while (!message.equals("SERVER>>> TERMINATE"));
    }

    private void closeConnection() throws IOException {
        displayArea.append("\nClosed connection");
        revalidate();
        output.close();
        input.close();
        clientSocket.close();
    }

    private void sendData(String message) {
        try {
            output.writeObject(name + ">>> " + message);
            output.flush();
            displayArea.append("\n" + name + ">>> " + message);
        } catch (IOException e) {
            displayArea.append("\nError writing object");
        }
    }
}
