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

public class EchoServer {
    
    //Socket with two pck, one to recive and other so send 
    private static DatagramSocket ds = null;
    private static DatagramPacket DsRecive = null;
    private static DatagramPacket DsSend = null;
    //private static byte[] seqIn = new byte[2];

    private static InetAddress ip=null;
    private static byte[] receive = new byte[65535];
    private static Queue<int[]> seqQueue = null;
    public static void main(String[] args) throws IOException, InterruptedException {
        
        // Step 1 : Create a socket to listen at port 1234
        ds = new DatagramSocket(1234);
        //init the var         
        ip = InetAddress.getLocalHost();
        seqQueue = new LinkedList<>(); 
        
        while (true) {
            

            //Create var to se seq that we will have
            byte[] seqIn = new byte[2];
            //Create var to save in queue to know if i already recive the msg
            int[] newData = new int[2];

            // Create a DatgramPacket to receive the data. rdt1.0
            DsRecive = new DatagramPacket(receive, receive.length);
            //Revieve the data in byte buffer.
            ds.receive(DsRecive);

            // rdt2.0 Save the seq from the msg recive
            seqIn[0] = receive[0];
            seqIn[1] = receive[1];
            newData[0] = (int)receive[0];
            newData[1] = (int)receive[1];
                        
            //TimeUnit.SECONDS.sleep(4);

            //Create pck to send the ACK withe the seq recived 
            DsSend = new DatagramPacket(seqIn, seqIn.length, ip , 2345);
             //if (seqQueue.size()>2) {
                 //Send the pck ACK
            ds.send(DsSend);
             //}
                                    
            
            //Check if i already recive the seq, so the msg will be ignor
            if (seqQueue.isEmpty() || !isThere(newData[0],newData[1])) {
                //System.out.println("Send it de seq" + newData[0]+ newData[1]);
                //Display the msg without the seq
                System.out.println("Client:-" + data(receive));
                //Save the seq in queue
                seqQueue.add(newData);
            } else {
                //Ignoring the msg cause is repeat
                //System.out.println("Element is repeated.");
            }

            //When msg comming is "bye" will close the connection
            if (data(receive).toString().equals("bye")) {
                System.out.println("Client sent bye.....EXITING");
                ds.close();                
                break;
            }

            // Clear the buffer after every message.
            receive = new byte[65535];



        	// // isBound() method 
            // System.out.println("IsBound : " + ds.isBound()); 
    
            // // isConnected() method 
            // System.out.println("isConnected : " + ds.isConnected()); 
    
            // // getInetAddress() method 
            // System.out.println("InetAddress : " + ds.getInetAddress()); 
    
            // // getPort() method 
            // System.out.println("Port : " + ds.getPort()); 
    
            // // getRemoteSocketAddress() method 
            // System.out.println("Remote socket address : " +  
            // ds.getRemoteSocketAddress()); 
    
            // // getLocalSocketAddress() method 
            // System.out.println("Local socket address : " +  
            // ds.getLocalSocketAddress()); 
        }
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
    public static boolean isThere(int first, int second){
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