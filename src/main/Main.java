package main;

/**
 * Created by Yana on 23/05/2017.
 */
public class Main {
    private static Server server;
    private static Client client;

    public static void main(String[] args) {
        if (args.length == 4) {
            String serverIP = args[1];
            int serverPort = Integer.valueOf(args[2]);
            String clientName = args[3];
            client = new Client(serverIP, serverPort, clientName);
            client.run();
        } else if (args.length == 2) {
            int serverPort = Integer.valueOf(args[1]);
            server = new Server(serverPort);
            server.run();
        } else {
            System.out.println("Not enough args. args format:");
            System.out.println("client <server IP> <server port> <client name>");
            System.out.println("or");
            System.out.println("server <server port>");
        }
    }
}
