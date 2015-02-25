package algorithm;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import static algorithm.Node.nodeId;
import static algorithm.Node.socketMap;
import static algorithm.Node.writers;

/**
 * Created by priyadarshini on 2/16/15.
 */
public class RicartAgarwalaAlgorithm {



    public static void main(String[] args) throws Exception {
        RequestClients requestClients = new RequestClients();
        AcceptClients acceptClients = new AcceptClients();
        algorithm();
    }

    protected static void algorithm() throws IOException, InterruptedException {

        while (socketMap.size() != (Node.TOTAL_NODES - 1)) {
//            System.out.println("connected sockets " + socketMap.size());
            Thread.sleep(200);
        }

        RicartAgarwala ricartAgarwala = new RicartAgarwala();
        int clientCount = 1;
        for (;clientCount<=Node.TOTAL_NODES;clientCount++)
        {
            System.out.println("clientcount " + clientCount + " nodeid " + nodeId);
            if (clientCount!= nodeId)
            {
                Listener listener = new Listener(socketMap.get(clientCount), ricartAgarwala);
                System.out.println("SocketID"+listener);
                System.out.println("Started thread at "+nodeId+" for listening "+clientCount);
            }

        }

//        if (nodeId == 1)
//        {
//            new Thread()
//            {
//                public void run()
//                {
//                    broadcast("START");
//                }
//            }.start();
            ricartAgarwala.requestCriticalSection();
//        }
    }


    public static void broadcast(String message)
    {
        for(int i=1; i<= Node.TOTAL_NODES; i++)
        {
            if (i!=nodeId)
            {
                try
                {
                    System.out.println("Sending "+message+" to "+i);
                    Socket bs = socketMap.get(i);
                    PrintWriter writer = writers.get(bs);
                    writer.println(message);
                    writer.flush();
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }



}
