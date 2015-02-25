package algorithm;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

import static algorithm.Node.*;

/**
 * Created by priyadarshini on 2/24/15.
 */
public class RicartAgarwala {

    public static final int TIME_UNIT = 100;

    private Set replySet;
    private boolean criticalSectionRequested;
    private boolean inCriticalSection;
    private int highestSeqNum;
    private boolean[] replyDeferredTo;
    private double unitDiff;
    private boolean isNodeOdd;
    protected Set participants;
    private int entryCount;
    private boolean replyDeferred;

    RicartAgarwala() {
        System.out.println("Entered server");

        replySet = new HashSet();
        criticalSectionRequested = false;
        inCriticalSection = false;
        highestSeqNum = 0;
        replyDeferredTo = new boolean[TOTAL_NODES];
        participants = new HashSet();
        if(nodeId %2 == 0){
            unitDiff = 0.5;
            isNodeOdd = false;
        }else{
            unitDiff = 0.25;
            isNodeOdd = true;
        }
        entryCount = 0;
        participants = new HashSet();
    }

    protected void requestCriticalSection() {
        try {
            System.out.println("re cric section" + participants.toString());
            if(entryCount == 0){
                int channelCount = 1;
                for (; channelCount <= TOTAL_NODES; channelCount++) {
                    if (nodeId != channelCount) {
                        System.out.println("Sending requests " + nodeId + " channel count " + channelCount);
                        sendRequest(channelCount);
                    }
                }
            } else {
//                if(participants.isEmpty()){
//                    enterCriticalSection();
//                    releaseCriticalSection();
//                } else{

                Iterator iterator = participants.iterator();
                while(iterator.hasNext()){
                    Integer participant = (Integer) iterator.next();
                    System.out.println("Sending requests " + nodeId + " channel count " + participant);
                    sendRequest(participant);
//                    }

                }
            }
        } catch (Exception e) {
            System.out.println("Something went wrong ");
            e.printStackTrace();
        }
    }

    protected void sendRequest(int channelCount) {
        criticalSectionRequested = true;
        seqNumber = Math.max(highestSeqNum, seqNumber) + 1;
            System.out.println("send request channel count" + channelCount + " highest seq number " + seqNumber);
            String requestMessage = new StringBuilder().append("REQUEST ")
                    .append(seqNumber)
                    .append(" ")
                    .append(nodeId)
                    .toString();
            System.out.println("Request message " + requestMessage);
            Socket socket = socketMap.get(channelCount);
            PrintWriter sender = Node.writers.get(socket);
            sender.println(requestMessage);

    }

    protected void sendReply(int channelCount) {
        try {
            System.out.println("send reply channel count" + channelCount);
            PrintWriter sender = new PrintWriter(((Socket) socketMap.get(channelCount)).getOutputStream(), true);
            seqNumber = Math.max(highestSeqNum, seqNumber) + 1;


            String replyMessage = new StringBuilder().append("REPLY ")
                    .append(seqNumber)
                    .append(" ")
                    .append(nodeId)
                    .toString();
            System.out.println(replyMessage);
            sender.println(replyMessage);
            participants.add(channelCount);
            System.out.println("Participants from request " + participants.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void receiveMessage(String receivedMessage) throws IOException {
                String[] keyWords = receivedMessage.split(" ");
                if (keyWords[0].equals("REQUEST")) {
                   } else if (keyWords[0].equals("REPLY")) {
                     }
    }

    protected void receiveRequest(int fromNode, int fromSeqNumber) {
        highestSeqNum = Math.max(highestSeqNum, fromSeqNumber);
        replyDeferred = inCriticalSection &&
                criticalSectionRequested &&
                ((fromSeqNumber > seqNumber)
                        || (fromSeqNumber == seqNumber && fromNode > nodeId));
        if (replyDeferred) {
            System.out.println("Deferred sending message to " + fromNode);
            replyDeferredTo[(fromNode - 1)] = true;
        } else {
            if (criticalSectionRequested == true && inCriticalSection == false
                    && entryCount != 0 && !(participants.contains(fromNode)))
            {
                sendRequest(fromNode);
            }
            System.out.println("Sent reply message to " + fromNode);
            sendReply(fromNode);
        }
    }

    protected void receiveReply(int fromNode, int fromSeqNumber) {
        highestSeqNum = Math.max(highestSeqNum, fromSeqNumber);
        System.out.println("Participants from reply " + participants.toString());
        System.out.println(participants.remove(fromNode));
        System.out.println("participants in reply after removing " + participants.toString());
        replySet.add(fromNode);
        System.out.println("reply set from reply " + replySet.toString());
        if ((replySet.size() == (TOTAL_NODES - 1))){
            enterCriticalSection();
            releaseCriticalSection();
        }
    }

    private void enterCriticalSection() {
        inCriticalSection = true;
        try {
            entryCount++;
            System.out.println("Entered Critical section.. ");
            Thread.sleep(3 * TIME_UNIT);
            System.out.println("Exited critical section..");
            replySet.clear();
        } catch (Exception e) {
            System.out.println("Something went wrong in the critical section");
        }
    }


    public void releaseCriticalSection() {
        criticalSectionRequested = false;
        System.out.println("TOTAL_PEERS in release " + TOTAL_NODES);
        for (int i = 0; i < TOTAL_NODES; i++) {
            System.out.println("Reply deferred to " + i  + " value " + replyDeferredTo[i]);
            if (replyDeferredTo[i]) {
                replyDeferredTo[i] = false;
                sendReply(i + 1);
//                participants.add( i + 1);
//                System.out.println("participants added in release " + participants.toString());
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


}
