package algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static algorithm.Node.*;

/**
 * Created by priyadarshini on 2/24/15.
 */
class AcceptClients extends Thread {

    AcceptClients() throws Exception {
        try {
            start();
        } catch (Exception e) {
            System.out.println("exception while trying to connect " + e.getMessage());
        }
    }


    public void run() {
        ServerSocket server = null;
        try {

            System.out.println("Reading thisNode.txt");
            List<String> nodeDetails = null;
            nodeDetails = Files.readAllLines(Paths.get("resources/thisNode.txt"), StandardCharsets.UTF_8);
            String hostNode = nodeDetails.get(0);
            System.out.println("Host node details " + hostNode);
            String[] hostDetails = hostNode.split(" ");
            nodeId = Integer.parseInt(hostDetails[0]);
            portNumber = Integer.parseInt(hostDetails[1]);
            server = new ServerSocket(portNumber);
            System.out.println("Connected to client with ID " + nodeId);

            System.out.println("Created this node");

            int clientCount = nodeId + 1;
            System.out.println("nodeId " + nodeId + " clientcount " + clientCount);
            while (clientCount <= TOTAL_NODES) {
                Socket socket = server.accept();
                System.out.println("Socket at " + nodeId + " for listening " + clientCount + " " + socket);
                System.out.println("-------------------------");

                socketMap.put(clientCount, socket);
                readers.put(socket, new BufferedReader(new InputStreamReader(socket.getInputStream())));
                writers.put(socket, new PrintWriter(socket.getOutputStream()));
                clientCount++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
