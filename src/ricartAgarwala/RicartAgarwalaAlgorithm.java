package ricartAgarwala;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

/**
 * Created by priyadarshini on 2/16/15.
 */
public class RicartAgarwalaAlgorithm {
    Vector allClients;


    public static void main(String[] args) throws Exception {
//        Server server = new Server();
//        server.serverThread = new Thread(server);
//        server.serverThread.start();
        Node thisNode = new Node();
        thisNode.id = 1;
        thisNode.seqNumber = 0;
        thisNode.ip = "127.0.0.1";
        thisNode.portNumber = 4040;

        ChatServer ob=new ChatServer(thisNode);

        RicartAgarwalaAlgorithm ricartAgarwalaAlgorithm=new RicartAgarwalaAlgorithm();
        ricartAgarwalaAlgorithm.algorithm();
    }


    private void algorithm() throws IOException {
        allClients = new Vector();
        Node node = new Node();
        for(String text: Files.readAllLines(Paths.get("resources/input.txt"), StandardCharsets.UTF_8)){
            System.out.println(text);
            String[] splitText = text.split(" ");
            node.id = Integer.parseInt(splitText[0]);
            node.ip = splitText[1];
            node.portNumber = Integer.parseInt(splitText[2]);
            node.seqNumber = 0;
            Client client=new Client(node);
            allClients.add(client);
            client.clientThread.start();
        }

    }


}
