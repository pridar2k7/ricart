package algorithm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by priyadarshini on 2/20/15.
 */
public class Server extends Thread {

    public static final int TOTAL_PEERS = 3;
    protected Map connectedSockets;
    int clientId;
    Node thisNode;
    int entryCount;

    private int highestSeqNum;
    private boolean[] replyDeferredTo;
    private boolean replyDeferred;
    private boolean criticalSectionRequested;
    Set replySet;
    PrintWriter sender;


    Server(Node thisNode) throws Exception {
        System.out.println("Entered server");
        this.thisNode = thisNode;
        connectedSockets = new HashMap();
        clientId = thisNode.id + 1;
        entryCount = 0;

        replySet = new HashSet();
        criticalSectionRequested = false;
        highestSeqNum = 0;
        replyDeferredTo = new boolean[TOTAL_PEERS];
        start();
    }

    protected void algorithm() throws IOException, InterruptedException {
        Node node = new Node();
        Vector allClients = new Vector();
        System.out.println("Reading input.txt");

        for (String text : Files.readAllLines(Paths.get("resources/input.txt"), StandardCharsets.UTF_8)) {
            System.out.println(text);
            String[] splitText = text.split(" ");
            node.id = Integer.parseInt(splitText[0]);
            node.ip = splitText[1];
            node.portNumber = Integer.parseInt(splitText[2]);
            node.seqNumber = 0;
            RequestServer client = new RequestServer(node);
            allClients.add(client);
        }

        /*this node number 4.. there should have been 3 client sockets and 6 server sockets*/

//        for (int count = 0; count < allClients.size(); count++) {
        while (connectedSockets.size() != (TOTAL_PEERS - 1)) {
            System.out.println("connected sockets " + connectedSockets.size());
            Thread.sleep(25000);
        }

        requestCriticalSection();
//        }
    }

    private void requestCriticalSection() {
        try {
            int channelCount = 1;
            for (; channelCount <= TOTAL_PEERS; channelCount++) {
                System.out.println("Sending requests " + thisNode + " channel count " + channelCount);
                if (thisNode.id != channelCount) {
                    sendRequest(channelCount);
                }
            }
        } catch (Exception e) {
            System.out.println("Something went wrong ");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        ServerSocket soc = null;
        try {
            soc = new ServerSocket(thisNode.portNumber);

            while (true) {
                System.out.println("Waiting to accept connections & clientID is " + clientId);
                Socket CSoc = soc.accept();
                AcceptClient obClient = new AcceptClient(clientId, CSoc);
                clientId++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void sendRequest(int channelCount) {
        criticalSectionRequested = true;
        thisNode.seqNumber = Math.max(highestSeqNum, thisNode.seqNumber) + 1;
        try {
            System.out.println("send request channel count" + channelCount + " highest seq number " + thisNode.seqNumber);
            sender = new PrintWriter(((Socket) connectedSockets.get(channelCount)).getOutputStream(), true);
            String requestMessage = new StringBuilder().append("REQUEST ")
                    .append(thisNode.seqNumber)
                    .append(" ")
                    .append(thisNode.id)
                    .toString();
            System.out.println("Request message " + requestMessage);
            sender.println(requestMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void sendReply(int channelCount) {
        try {
            System.out.println("send reply channel count" + channelCount);
            System.out.println(connectedSockets.toString());
            sender = new PrintWriter(((Socket) connectedSockets.get(channelCount)).getOutputStream(), true);
            thisNode.seqNumber = Math.max(highestSeqNum, thisNode.seqNumber) + 1;


            String replyMessage = new StringBuilder().append("REPLY ")
                    .append(thisNode.seqNumber)
                    .append(" ")
                    .append(thisNode.id)
                    .toString();
            System.out.println(replyMessage);
            sender.println(replyMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void receiveMessage(String receivedMessage) throws IOException {
        String[] keyWords = receivedMessage.split(" ");
        if (keyWords[0].equals("REQUEST")) {
            System.out.println("Request received from node " + keyWords[2] + " with sequence number- " + keyWords[1]);
            receiveRequest(Integer.parseInt(keyWords[2]), Integer.parseInt(keyWords[1]));
        } else if (keyWords[0].equals("REPLY")) {
            System.out.println("Reply received from node " + keyWords[2].trim() + " with sequence number- " + keyWords[1]);
            receiveReply(keyWords[2]);
        }
    }

    protected void receiveRequest(int fromNode, int fromSeqNumber) {
        highestSeqNum = Math.max(highestSeqNum, fromSeqNumber);
        replyDeferred = criticalSectionRequested && ((fromSeqNumber > thisNode.seqNumber) || (fromSeqNumber == thisNode.seqNumber && fromNode > thisNode.id));
        if (replyDeferred) {
            System.out.println("Deferred sending message to " + fromNode);
            replyDeferredTo[(fromNode - 1)] = true;
        } else {
            System.out.println("Sent reply message to " + fromNode);
            sendReply(fromNode);
        }
    }

    private void receiveReply(String fromNode) {
        replySet.add(fromNode);
        if (replySet.size() == (TOTAL_PEERS - 1)) {
            enterCriticalSection();
            releaseCriticalSection();
        }
    }

    private void enterCriticalSection() {
        try {
            entryCount++;
            System.out.println("Entered Critical section.. ");
            Thread.sleep(300);
            System.out.println("Exited critical section..");
            replySet.clear();
        } catch (Exception e) {
            System.out.println("Something went wrong in the critical section");
        }
    }


    public void releaseCriticalSection() {
        criticalSectionRequested = false;

        for (int i = 0; i < TOTAL_PEERS; i++) {
            if (replyDeferredTo[i]) {
                replyDeferredTo[i] = false;
//                if(i < (thisNode.id - 1))
                sendReply(i + 1);
//                else
//                    sendReply(i + 2);
            }
        }
        makeRequest();
    }

    private void makeRequest() {
        try {
            System.out.println("EntryCount " + entryCount);
            if (entryCount <= 20) {
                Thread.sleep(2000);
                requestCriticalSection();
            }
            if (entryCount <= 40) {
                if ((entryCount % 2) == 0) {
                    Thread.sleep(2000);
                    requestCriticalSection();
                } else {
                    Thread.sleep(4000);
                    requestCriticalSection();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    class AcceptClient extends Thread {
        Socket clientSocket;
        BufferedReader reader;

        AcceptClient(int clientId, Socket CSoc) throws Exception {
            System.out.println("Connected to client with ID " + clientId);
            try {
                clientSocket = CSoc;

                connectedSockets.put(clientId, clientSocket);
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                start();
            } catch (Exception e) {
                System.out.println("exception" + e.getMessage());
            }
        }

        public void run() {
            while (true) {
                try {
//                    byte buf[] = new byte[50];
//                    reader.read(buf);
                    String msgFromClient;
//                    for (int iterator = 0; iterator < splitMsg.length; iterator++) {
                    while(( msgFromClient = reader.readLine() ) != null)
                    {
                            System.out.println("msgfrmclient" + msgFromClient);
                            receiveMessage(msgFromClient.trim());
                    }
//                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    class RequestServer extends Thread {
        Socket socket;
        Node node;
        BufferedReader reader;


        RequestServer(Node node) throws IOException {
            socket = new Socket(node.ip, node.portNumber);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connectedSockets.put(node.id, socket);
            this.node = node;
            start();
        }

        public void run() {
            while (true) {
                try {
                    byte buf[] = new byte[50];
//                    reader.read(buf);
                    String msgFromClient;
//                    for (int iterator = 0; iterator < splitMsg.length; iterator++) {
                    while(( msgFromClient = reader.readLine() ) != null)

                    {
                        System.out.println("msgfrmclient" + msgFromClient);
                        receiveMessage(msgFromClient.trim());
                    }
//                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}

