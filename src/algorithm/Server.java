//package algorithm;
//
//import java.io.*;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.*;
//
///**
// * Created by priyadarshini on 2/20/15.
// */
//public class Server extends Thread {
//
//    public static int TOTAL_PEERS = 3;
//    private final double unitDiff;
//    private final boolean isNodeOdd;
//    protected Map connectedSockets;
//    int clientId;
//    Node thisNode;
//    int entryCount;
//    ArrayList<String> messageQueue;
//
//    private int highestSeqNum;
//    private boolean[] replyDeferredTo;
//    private boolean replyDeferred;
//    private boolean criticalSectionRequested;
//    private boolean inCriticalSection;
//    Set replySet;
//    PrintWriter sender;
//    private Set participants;
//
//
//    Server(Node thisNode) throws Exception {
//        System.out.println("Entered server");
//        this.thisNode = thisNode;
//        connectedSockets = new HashMap();
//        clientId = thisNode.id + 1;
//        entryCount = 0;
//        messageQueue = new ArrayList<String>();
//
//        replySet = new HashSet();
//        criticalSectionRequested = false;
//        inCriticalSection = false;
//        highestSeqNum = 0;
//        replyDeferredTo = new boolean[TOTAL_PEERS];
//        participants = new HashSet();
//        if(thisNode.id %2 == 0){
//            unitDiff = 0.5;
//            isNodeOdd = false;
//        }else{
//            unitDiff = 0.25;
//            isNodeOdd = true;
//        }
//        start();
//    }
//
//
//
//    @Override
//    public void run() {
//        ServerSocket soc = null;
//        try {
//            while (true) {
//                System.out.println("Waiting to accept connections & clientID is " + clientId);
//                AcceptClients obClient = new AcceptClients(clientId, CSoc);
//                clientId++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//
////
////    class RequestServer extends Thread {
////        Socket socket;
////        Node node;
////        BufferedReader reader;
////
////
////
////        public void run() {
////            while (true) {
////                try {
////                    byte buf[] = new byte[50];
//////                    reader.read(buf);
////                    String msgFromClient;
//////                    for (int iterator = 0; iterator < splitMsg.length; iterator++) {
////                    while ((msgFromClient = reader.readLine()) != null)
////
////                    {
////                        System.out.println("msgfrmclient" + msgFromClient);
////                        receiveMessage(msgFromClient.trim());
////                    }
//////                    }
////                } catch (Exception ex) {
////                    ex.printStackTrace();
////                }
////            }
////        }
////    }
//}
//
