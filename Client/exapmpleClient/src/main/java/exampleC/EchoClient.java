package exampleC;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// Java program to illustrate Client side 
// Implementation using DatagramSocket 
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import javax.swing.Timer;

public class EchoClient {

	
	private static boolean ackOk = false;
	private static InetAddress ip = null;
	private static int count = 0;

	//Socket to be use, pck to send and other to recive 
	private static DatagramSocket ds = null;
	private static DatagramSocket dr = null;
	private static DatagramPacket DpSend = null;
	private static DatagramPacket DpRecive = null;

	//Var to have the info to send and recive 
	private static byte buf[] = null;
	private static byte mes[] = null;
	private static byte[] receive = new byte[65535];
	//Var to use as de seq number before de message 
	private static byte seq[] = null;
	
	public static void main(String args[]) throws IOException {
		Scanner sc = new Scanner(System.in);

		// Step 1:Create the socket object to carry the info 
		ds = new DatagramSocket();
		dr = new DatagramSocket(2345);
		ip = InetAddress.getLocalHost();
  
				
		while (true) {
			//ds.connect(ip, 1234);
			//count how many message i sended 
			count = 0;

			//To read line 
			String inp = sc.nextLine();
			// convert the String input into the byte array.
			buf = inp.getBytes();

			// rdt2.0 generate the seq number
			seq = findSeq(seq);

			int seqLen = seq.length;
			int bufLen = buf.length;
			//Create de message with the seq number in the first two bytes
			mes = new byte[seqLen + bufLen];
			System.arraycopy(seq, 0, mes, 0, seqLen);
			System.arraycopy(buf, 0, mes, seqLen, bufLen);

			// Step 2 : Create the datagramPacket for sending the data. rdt1.0
			DpSend = new DatagramPacket(mes, mes.length, ip, 1234);
			ds.send(DpSend);


			//Timer do actions when is certain time 	
			Timer timer = new Timer(1000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//If there's timeout and I didn's send the message 3 times, resend it 
					if ((!ackOk) && (count < 3)){
						try {
							System.out.println("Did't recive the ACK, sending again");
							ds.send(DpSend);
							count++;
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}

				}
			});
			timer.start();


			//If input is "bye", close de connection
			if (inp.equals("bye"))
				break;
			
			//Create a thread to wait the ACK
			Thread t = new Thread(new reciveClass());
			t.start();

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

	private static class reciveClass implements Runnable {

		public void run() {	
			try {			
				// rdt2.0
				ackOk = false;

				//Create de pck to send the ACK
				DpRecive = new DatagramPacket(receive, receive.length);
				dr.receive(DpRecive);

				//Read the seq number in the ACK to compare with the one i sended 
				int first = receive[0];
				int second = receive[1];
				ackOk = (first==seq[0]) && (second==seq[1]);
				
				if (ackOk) {
					//They recive the message right 
					System.out.println("Message was recive.");
				}			
			
			} catch (IOException e) {
				e.printStackTrace();
			}


		}
	}

	//Function to generate seq number 
	public static byte[] findSeq(byte[] seq) {
		byte[] ret = seq;
		if (ret == null) {
			ret = new byte[2];
			ret[0] = (byte) 0;
			ret[1] = (byte) 0;
		} else {
			if (ret[1] == Byte.MAX_VALUE) {
				ret[0]++;
				ret[1] = (byte) 0;
			} else if (ret[0] == Byte.MAX_VALUE){
				ret[0] = (byte) 0 ;
				ret[1] = (byte) 0 ;
			} else {	
				ret[1]++;
			}
		}
		return ret;
	}

}
