package algorithm;

import java.io.BufferedReader;
import java.net.Socket;

/**
 * Created by priyadarshini on 2/24/15.
 */
public class Listener extends Thread   {

    private Socket socket;
    private RicartAgarwala ricartAgarwala;
    private BufferedReader bufferedReader;

    public Listener(Socket socket, RicartAgarwala ricartAgarwala) {
        this.socket = socket;
        this.ricartAgarwala = ricartAgarwala;
        this.bufferedReader = Node.readers.get(socket);
        start();
    }

    public void run(){
        String message;
        try
        {
            System.out.println("listener");
            while((message = bufferedReader.readLine() ) != null)
            {
                System.out.println("Message read " + message);
                String tokens[] = message.split(" ");
                String messageType = tokens[0];
                if(messageType.equals("START"))
                {
                    ricartAgarwala.requestCriticalSection();
                }
                if(messageType.equals("REPLY"))
                {
                    System.out.println("Reply received from node " + tokens[2].trim() + " with sequence number- " + tokens[1]);
                    ricartAgarwala.receiveReply(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[1]));
//
//                    RA.incrementCount();
//
//                    // Roucairol-Carvalho optimization
//                    RA.participants.remove(tokens[1]);
//
//                    System.out.println("REPLYCOUNT:"+RA.replyCount+":REPLYFROM"+tokens[1]);
//                    RA.checkCS();
                }

                if(messageType.equals("REQUEST"))
                {
                    System.out.println("Request received from node " + tokens[2] + " with sequence number- " + tokens[1]);
                    ricartAgarwala.receiveRequest(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[1]));


//                    System.out.println("SERVER-TS REQUEST:"+RA.requestTS);
//                    messageTS = Long.parseLong(tokens[1]);
//
//                    System.out.println("MESSAGE-TS REQUEST:"+tokens[2]+":"+messageTS);
//                    System.out.println("----------------------------------");
//
//                    // Tricky comparisons. Read paper to understand
//                    if(RA.criticalSection == false &&
//                            ((RA.requestCS == false)
//                                    || (RA.requestCS == true && RA.requestTS > messageTS)
//                                    || (RA.requestCS == true && RA.requestTS == messageTS
//                                    && Node.nodeID > Integer.parseInt(tokens[2]))))
//                    {
//                        if (RA.requestCS == true && RA.criticalSection == false
//                                && RA.criticalSectionCount != 0 && !(RA.copyOfParticipants.contains(tokens[2])))
//                        {
//                            ++RA.participantsCount;
//                            PrintWriter writer2 = Node.writers.get(socket);
//                            long requestTS = TimeStamp.getTimestamp();
//                            writer2.println("REQUEST,"+requestTS+","+Node.nodeID);
//                            writer2.flush();
//                            System.out.println("Sending delayed request to"+tokens[2]+":"+requestTS);
//                        }
//
//                        System.out.println("REPLY SENT TO:"+tokens[2]);
//                        // Reply
//                        PrintWriter writer = Node.writers.get(socket);
//                        writer.println("REPLY"+","+Node.nodeID);
//                        writer.flush();
//
//                        // Roucairol-Carvalho optimization
//                        RA.participants.add(tokens[2]);
//                    }
//                    else
//                    {
//                        // defer REPLY
//                        System.out.println("Deferred reply to "+tokens[2]);
//                        RA.deferred.add(tokens[2]);
//                    }
                }
                System.out.println("in while");
            }
            System.out.println("done");

        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    }
