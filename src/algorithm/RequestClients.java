package algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static algorithm.Node.*;

/**
 * Created by priyadarshini on 2/24/15.
 */
class RequestClients{
    Socket clientSocket;
    BufferedReader reader;
    private int seqNumber;
    int nodeId, portNumber;
    String ip;


    RequestClients() throws IOException {
        run();
    }

    public void run() {
        try {
            for (String text : Files.readAllLines(Paths.get("resources/input.txt"), StandardCharsets.UTF_8)) {
                System.out.println("input read " + text);
                String[] splitText = text.split(" ");
                nodeId = Integer.parseInt(splitText[0]);
                ip = splitText[1];
                portNumber = Integer.parseInt(splitText[2]);
                seqNumber = 0;
                clientSocket = new Socket(ip, portNumber);
                socketMap.put(nodeId, clientSocket);
                readers.put(clientSocket, new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));
                writers.put(clientSocket, new PrintWriter(clientSocket.getOutputStream()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
