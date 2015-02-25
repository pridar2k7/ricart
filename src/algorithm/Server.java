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
    public static final int TIME_UNIT = 100;
    private final double unitDiff;
    private final boolean isNodeOdd;
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
    private Set participants;
    private boolean inCriticalSection;


    Server(Node thisNode) throws Exception {
        System.out.println("Entered server");
        this.thisNode = thisNode;
        connectedSockets = new HashMap();
        clientId = thisNode.id + 1;
        entryCount = 0;
        inCriticalSection = false;

        replySet = new HashSet();
        participants = new HashSet();
        criticalSectionRequested = false;
        highestSeqNum = 0;
        replyDeferredTo = new boolean[TOTAL_PEERS];
        if(thisNode.id %2 == 0){
            unitDiff = 0.5;
            isNodeOdd = false;
        }else{
            unitDiff = 0.25;
            isNodeOdd = true;
        }
        start();
        System.out.println("start "+currentThread().toString());
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

        /*if this is node number 4.. there should have been 3 client sockets and 6 server sockets*/

        while (connectedSockets.size() != (TOTAL_PEERS - 1)) {
            System.out.println("connected sockets " + connectedSockets.size());
            Thread.sleep(2 * TIME_UNIT);
        }

        requestCriticalSection();
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
            System.out.println("Participants before add "+ participants.toString() + currentThread().toString());
            participants.add(channelCount);
            System.out.println("Participants after add"+ participants.toString() + currentThread().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void receiveMessage(String receivedMessage) throws IOException, InterruptedException {
        String[] keyWords = receivedMessage.split(" ");
        if (keyWords[0].equals("REQUEST")) {
            System.out.println("Request received from node " + keyWords[2] + " with sequence number- " + keyWords[1]);
            receiveRequest(Integer.parseInt(keyWords[2]), Integer.parseInt(keyWords[1]));
        } else if (keyWords[0].equals("REPLY")) {
            System.out.println("Reply received from node " + keyWords[2].trim() + " with sequence number- " + keyWords[1]);
            receiveReply(Integer.parseInt(keyWords[2]), Integer.parseInt(keyWords[1]));
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

    private void receiveReply(int fromNode, int fromSeqNumber) throws InterruptedException {
        highestSeqNum = Math.max(highestSeqNum, fromSeqNumber);
        System.out.println("replyset before add "+ replySet.toString() + "from node" + fromNode );
        replySet.add(fromNode);
        System.out.println("replyset after add "+ replySet.toString() );
        if(inCriticalSection){
            Thread.sleep(500);
        }
        if (replySet.size() == (TOTAL_PEERS - 1)) {
            enterCriticalSection();
            releaseCriticalSection();
            replySet.clear();
        }
        System.out.println("Participants before remove "+ participants.toString() + currentThread().toString());
        participants.remove(fromNode);
        System.out.println("Participants after remove " + participants.toString() + currentThread().toString());
    }

    private void enterCriticalSection() {
        try {
            inCriticalSection = true;
            entryCount++;
            System.out.println("Entered Critical section.. " + currentThread().toString());
            Thread.sleep(3 * TIME_UNIT);
            System.out.println("Exited critical section.." + currentThread().toString());
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
                sendReply(i + 1);
            }
        }
        inCriticalSection = false;
        makeRequest();
    }

    private void makeRequest() {
        try {
            System.out.println("EntryCount " + entryCount);
            int timeToSleep = 0;
            if (entryCount <= 20) {
                timeToSleep = (int)((10+ (entryCount*unitDiff)) * TIME_UNIT);
                Thread.sleep(timeToSleep);
                requestCriticalSection();
            } else if (entryCount <= 40) {
                if (isNodeOdd) {
                    timeToSleep = (int)((10 + (entryCount*unitDiff)) * TIME_UNIT);
                    Thread.sleep(timeToSleep);
                    requestCriticalSection();
                } else {
                    timeToSleep = (int)((40+ ((entryCount%20)*unitDiff)) * TIME_UNIT) ;
                    Thread.sleep(timeToSleep);
                    requestCriticalSection();
                }
            }
            System.out.println("timeToSleeep " + timeToSleep);
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
                    while ((msgFromClient = reader.readLine()) != null) {
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
                    while ((msgFromClient = reader.readLine()) != null)

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

