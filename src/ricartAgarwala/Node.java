package ricartAgarwala;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by priyadarshini on 2/16/15.
 */
public class Node {
    public static final int TOTAL_PEERS = 9;
    int id;
    String ip;
    int portNumber;
    int seqNumber;
    InputStream receiver;
    OutputStream sender;
    Set replySet;
    private int highestSeqNum;
    private boolean[] replyDeferredTo;
    private boolean replyDeferred;
    private boolean criticalSectionRequested;

    Node(){
        replySet = new HashSet();
        criticalSectionRequested = false;
    }


    protected void sendRequest(int channelCount) {
        criticalSectionRequested = true;
        try {
            String requestMessage = new StringBuilder().append("REQUEST ")
                .append(seqNumber)
                .append(" ")
                .append(channelCount)
                .toString();
            sender.write(requestMessage.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void sendReply(int channelCount) {
        try {
            String replyMessage = new StringBuilder().append("REPLY ")
                .append(seqNumber)
                .append(" ")
                .append(channelCount)
                .toString();
            sender.write(replyMessage.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void receiveMessage() throws IOException {
        byte[] message = new byte[50];
        receiver.read(message);
        String receivedMessage = new String(message);
        String[] keyWords = receivedMessage.split(" ");
        if(keyWords[0].equals("REQUEST")){
            System.out.println("Request received from node " + keyWords[2] + " with sequence number- " + keyWords[1]);

        } else if(keyWords[0].equals("REPLY")){
            System.out.println("Reply received from node " + keyWords[2] + " with sequence number- " + keyWords[1]);
            receiveReply(keyWords[1]);
        }
    }

    protected void receiveRequest(int fromNode, int fromSeqNumber) {
        highestSeqNum = Math.max(highestSeqNum, fromSeqNumber);
        replyDeferred = criticalSectionRequested && ((fromSeqNumber > seqNumber) || (fromSeqNumber == seqNumber && fromNode > id));
        if(replyDeferred){
            System.out.println("Deferred sending message to " + fromNode);
            if(fromNode > id)
                replyDeferredTo[fromNode - 2] = true;
            else
                replyDeferredTo[fromNode - 1] = true;
        }
        else{
            System.out.println("Sent reply message to " + fromNode);
            sendReply(fromNode);
        }
    }

    private void receiveReply(String fromNode) {
        replySet.add(fromNode);
        if(replySet.size() == TOTAL_PEERS){
            enterCriticalSection();
            releaseCriticalSection();
        }
    }

    private void enterCriticalSection() {
        try {
            System.out.println("Entered Critical section.. ");
            Thread.sleep(300);
            System.out.println("Exited critical section..");
        }
        catch(Exception e){
            System.out.println("Something went wrong in the critical section");
        }
    }


    public void releaseCriticalSection()
    {
        criticalSectionRequested = false;

        for(int i = 0; i < TOTAL_PEERS; i++){
            if(replyDeferredTo[i]){
                replyDeferredTo[i] = false;
                if(i < (id - 1))
                    sendReply(i + 1);
                else
                    sendReply(i + 2);
            }
        }
    }

}
