package algorithm;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by priyadarshini on 2/16/15.
 */
public class Node {
    public static final int TOTAL_NODES = 3;
    public static int nodeId;
    public static int portNumber;
    public static int seqNumber;

    public static HashMap<Integer,Socket> socketMap = new HashMap<Integer,Socket>();

    public static HashMap<Socket,BufferedReader> readers = new HashMap<Socket,BufferedReader>();
    public static HashMap<Socket,PrintWriter> writers = new HashMap<Socket,PrintWriter>();
}
