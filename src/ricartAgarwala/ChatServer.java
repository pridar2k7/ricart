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
    Node node;

    ChatServer(Node node) throws Exception
    {
        ServerSocket soc=new ServerSocket(4040);
        ClientSockets=new Vector();

        this.node = node;

        while(true)
        {
            Socket CSoc=soc.accept();
            AcceptClient obClient=new AcceptClient(CSoc);
        }
    }
    public static void main(String args[]) throws Exception
    {

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
//                dout = new DataOutputStream(ClientSocket.getOutputStream());

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
            while(true)
            {
                try
                {
                    byte buf[] = new byte[50];
                    din.read(buf);
                    String msgFromClient=new String(buf);
                    System.out.println("msgfrmclient"+msgFromClient);
//                    byte writeMsg[] = new byte[200];
//                    writeMsg= "Message received".getBytes();
//                    dout.write(writeMsg);
//                    Thread.sleep(1000);
//                    dout.write("Second msg received".getBytes());
//                    System.out.println("msg written");
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
