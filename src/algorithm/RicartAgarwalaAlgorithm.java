package algorithm;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;

/**
 * Created by priyadarshini on 2/16/15.
 */
public class RicartAgarwalaAlgorithm {
    Vector allClients;


    public static void main(String[] args) throws Exception {
        Node thisNode = new Node();
        System.out.println("Reading thisNode.txt");
        List<String> nodeDetails = Files.readAllLines(Paths.get("resources/thisNode.txt"), StandardCharsets.UTF_8);
        String hostNode = nodeDetails.get(0);
        System.out.println("Host node details " + hostNode);
        String[] hostDetails = hostNode.split(" ");
        thisNode.id = Integer.parseInt(hostDetails[0]);
        thisNode.portNumber = Integer.parseInt(hostDetails[1]);
        thisNode.ip = "127.0.0.1";
        System.out.println("Created this node");

        Server server=new Server(thisNode);
        server.algorithm();
    }




}
