package ricartAgarwala;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by priyadarshini on 2/10/15.
 */
public class Client implements Runnable {
    Socket sock;
    Thread clientThread;
    String id;

    Client(String id, String ip, int portNumber) throws  IOException {
        sock = new Socket(ip, portNumber);
        this.id = id;
        clientThread = new Thread(this);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        Server server = new Server();
        server.serverThread = new Thread(server);
        server.serverThread.start();

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
            try {
                OutputStream req = sock.getOutputStream();
                //req.write("hello server".getBytes());
                String requ = new String();
                /*InputStreamReader isr=new InputStreamReader(System.in);
                BufferedReader br=new BufferedReader(isr);
                requ=br.readLine();*/
                req.write(("hello " + id).getBytes());
                System.out.println("client printing done");
                Thread.sleep(5000);

            } catch (Exception e) {
                System.out.println("The error is in client.. " + e);
            }
        }
    }

}

class Server implements Runnable {
    ServerSocket server;
    Thread serverThread;

    Server() throws IOException {
        server = new ServerSocket(4040);
    }

    public void run() {
        try {
            while (true) {
                Socket s = server.accept();

                System.out.println("entering server");
                System.out.println(s);


                InputStream in = s.getInputStream();
                byte buf[] = new byte[50];
                in.read(buf);
                String request = new String(buf);
                System.out.println("server msg: " + request);
                //String reqarr[]=new String[2];
                //reqarr=request1.split("-");
                //System.out.println(reqarr[0]);
                //String request=reqarr[1];

            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}

