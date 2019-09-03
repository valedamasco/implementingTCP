package exampleS;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// Java program to illustrate Server side 
// Implementation using DatagramSocket 
import java.io.IOException; 
import java.net.DatagramPacket; 
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.Timer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class EchoServer {

    private static byte[] seqIn = new byte[2];

    public static void main(String[] args) throws IOException, InterruptedException {


        DatagramSocket ds = new DatagramSocket(1234);
        //init the var         
        InetAddress ip = InetAddress.getLocalHost();
        Queue<int[]> seqQueue = new LinkedList<>();
        DatagramPacket DsSend = null;


        
        while (true) {
            byte[] receive = new byte[65535];
            String message = "";
            message = waitMsg(ip, ds ,  seqQueue, receive  );
            System.out.println(message);
            sendAck(ds , DsSend,  ip, seqIn );

            if (message=="bye") {
                System.out.println("Client sent bye.....EXITING");
                ds.close();
                System.exit(0);
            }

        }
    }

    public byte[] getSeqIn(){
        return seqIn;
    }

    public static byte[] sendAck(DatagramSocket ds , DatagramPacket DsSend, InetAddress ip, byte[] seqIn ) throws IOException {
        //Create pck to send the ACK withe the seq recived
        DsSend = new DatagramPacket(seqIn, seqIn.length, ip , 2345);
        //if (seqQueue.size()>2) {
        //Send the pck ACK
        ds.send(DsSend);
        //}
        return seqIn;
    }


    public static String waitMsg( InetAddress ip, DatagramSocket ds, Queue<int[]> seqQueue, byte[] receive) throws IOException {

        // Create a DatgramPacket to receive the data. rdt1.0
        DatagramPacket DsRecive = new DatagramPacket(receive, receive.length);
        //Revieve the data in byte buffer.
        ds.receive(DsRecive);

        //Create var to se seq that we will have
        //byte[] seqIn = new byte[2];
        //Create var to save in queue to know if i already recive the msg
        int[] newData = new int[2];


        // rdt2.0 Save the seq from the msg recive
        seqIn[0] = receive[0];
        seqIn[1] = receive[1];
        newData[0] = (int)receive[0];
        newData[1] = (int)receive[1];

        //Thread.sleep(4000);


        String msgRecive = "";

        //Check if i already recive the seq, so the msg will be ignor
        if (seqQueue.isEmpty() || !isThere(newData[0],newData[1], seqQueue)) {
            //System.out.println("Send it de seq" + newData[0]+ newData[1]);
            //Display the msg without the seq
            msgRecive ="Client:-" + data(receive) ;
            //System.out.println("Client:-" + data(receive));
            //Save the seq in queue
            seqQueue.add(newData);
        } else {
            //Ignoring the msg cause is repeat
            msgRecive = "Element is repeated." ;
            //System.out.println("Element is repeated.");
        }

        //When msg comming is "bye" will close the connection
        if (data(receive).toString().equals("bye")) {
            return "bye";
        }

        //receive = new byte[65535];
        return msgRecive;
    }

    // A utility method to convert the byte array
    // data into a string representation.
    public static StringBuilder data(byte[] a) {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        //Will ignor the 2 first bytes cause they are the seq
        int i = 2;
        while (i < a.length && a[i] != 0) {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }

    //Checking in my queue that the seq was not safe before
    public static boolean isThere(int first, int second, Queue<int[]> seqQueue){
        boolean isThere = false ;
        Iterator<int[]> iter = seqQueue.iterator();
        while (iter.hasNext() && !isThere) {
            int[] aux = iter.next();
            if (aux[0]==first && aux[1]==second) {
                isThere = true;
            }             
        }
        return isThere;
    }

}