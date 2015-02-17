package ricartAgarwala;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by priyadarshini on 2/16/15.
 */
public class ChatServer {
    static Vector ClientSockets;

    ChatServer() throws Exception
    {
        ServerSocket soc=new ServerSocket(4040);
        ClientSockets=new Vector();

        while(true)
        {
            Socket CSoc=soc.accept();
            AcceptClient obClient=new AcceptClient(CSoc);
        }
    }
    public static void main(String args[]) throws Exception
    {

        ChatServer ob=new ChatServer();
    }

    class AcceptClient extends Thread
    {
        Socket ClientSocket;
        DataInputStream din;
        DataOutputStream dout;
        AcceptClient (Socket CSoc) throws Exception
        {
            try {
                ClientSocket = CSoc;

                din = new DataInputStream(ClientSocket.getInputStream());
                dout = new DataOutputStream(ClientSocket.getOutputStream());

//                String LoginName = din.readUTF();
                System.out.println("start");
                ClientSockets.add(ClientSocket);
                start();
            }
            catch(Exception e){
                System.out.println("exception"+e.getMessage());
            }
        }

        public void run()
        {
            System.out.println("thread started");
            while(true)
            {
                System.out.println("while started");

                try
                {
                    System.out.println("msg read started");
                    byte buf[] = new byte[50];
                    din.read(buf);
                    String msgFromClient=new String(buf);
                    System.out.println("msgfrmclient"+msgFromClient);
//                    StringTokenizer st=new StringTokenizer(msgFromClient);
//                    String Sendto=st.nextToken();
//                    String MsgType=st.nextToken();
//                    int iCount=0;
//
//                    if(MsgType.equals("LOGOUT"))
//                    {
//                        for(iCount=0;iCount<LoginNames.size();iCount++)
//                        {
//                            if(LoginNames.elementAt(iCount).equals(Sendto))
//                            {
//                                LoginNames.removeElementAt(iCount);
//                                ClientSockets.removeElementAt(iCount);
//                                System.out.println("User " + Sendto +" Logged Out ...");
//                                break;
//                            }
//                        }
//
//                    }
//                    else
//                    {
//                        String msg="";
//                        while(st.hasMoreTokens())
//                        {
//                            msg=msg+" " +st.nextToken();
//                        }
//                        for(iCount=0;iCount<LoginNames.size();iCount++)
//                        {
//                            if(LoginNames.elementAt(iCount).equals(Sendto))
//                            {
//                                Socket tSoc=(Socket)ClientSockets.elementAt(iCount);
//                                DataOutputStream tdout=new DataOutputStream(tSoc.getOutputStream());
//                                tdout.writeUTF(msg);
//                                break;
//                            }
//                        }
//                    }

                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }



            }
        }
    }
}
