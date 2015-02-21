package ricartAgarwala;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

/**
 * Created by priyadarshini on 2/10/15.
 */
public class Client implements Runnable {
    Socket sock;
    Thread clientThread;
    Node node;
    OutputStream req;
    InputStream ins;

    Client(Node node) throws  IOException {
        this.node= node;
        sock = new Socket(node.ip, node.portNumber);
        clientThread = new Thread(this);
        req = sock.getOutputStream();
        ins = sock.getInputStream();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {


//        for (int clientCount = 0; clientCount < 5; clientCount++) {
//            ArrayList<ricartAgarwala.Client> clients = new ArrayList<ricartAgarwala.Client>();
//            clients.add(new ricartAgarwala.Client("127.0.0.1", 4000));
//            System.out.println("client number"+ clientCount);
//            clients.get(0).clientThread = new Thread(clients.get(0));
//            clients.get(0).clientThread.start();
//        }
//        ArrayList<ricartAgarwala.Client> clients = new ArrayList<ricartAgarwala.Client>();
//        clients.add(new ricartAgarwala.Client("127.0.0.1", 4000));
//        clients.get(0).clientThread = new Thread(clients.get(0));
//        clients.get(0).clientThread.start();
        // TODO code application logic here
    }

    public void run() {

        while (true) {
        }
    }




}

//class Server implements Runnable {
//    ServerSocket server;
//    Thread serverThread;
//    Node node;
//
//    Server() throws IOException {
//        server = new ServerSocket(4040);
//    }
//
//    public void run() {
//        try {
//            while (true) {
//                Socket s = server.accept();
//
//                System.out.println("entering server");
//                System.out.println(s);
//
//
//                InputStream in = s.getInputStream();
//                byte buf[] = new byte[50];
//                in.read(buf);
//                String request = new String(buf);
//                System.out.println("server msg: " + request);
//                //String reqarr[]=new String[2];
//                //reqarr=request1.split("-");
//                //System.out.println(reqarr[0]);
//                //String request=reqarr[1];
//
//            }
//        }
//        catch(Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
//}
//
