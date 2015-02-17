package ricartAgarwala;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by priyadarshini on 2/16/15.
 */
public class RicartAgarwalaAlgorithm {
    public static void main(String[] args) throws IOException {
        Node node = new Node();
        for(String text: Files.readAllLines(Paths.get("resources/input.txt"), StandardCharsets.UTF_8)){
            System.out.println(text);
            String[] splitText = text.split(" ");
            node.id = splitText[0];
            node.ip = splitText[1];
            node.portNumber = Integer.parseInt(splitText[2]);
            Client client=new Client(node.id, node.ip, node.portNumber);
            client.clientThread.start();
        }

    }
}
