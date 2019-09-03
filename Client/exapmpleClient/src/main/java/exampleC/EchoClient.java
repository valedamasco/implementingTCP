package exampleC;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// Java program to illustrate Client side 
// Implementation using DatagramSocket 
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

import javax.swing.Timer;

public class EchoClient {

	
	private static boolean ackOk = false;
	private static int count = 0;

	//Socket to be use, pck to send and other to recive 
	private static DatagramSocket ds = null;
	private static DatagramSocket dr = null;
	private static DatagramPacket DpSend = null;

	//Var to have the info to send and recive
	private static byte[] receive = new byte[65535];
	//Var to use as de seq number before de message 
	private static byte seq[] = null;
	
	public static void main(String args[]) throws IOException {

		Scanner sc = new Scanner(System.in);
		// Step 1:Create the socket object to carry the info 
		ds = new DatagramSocket();
        setDr(2345);
		InetAddress ip = InetAddress.getLocalHost();
		byte buf[] = null;
  
				
		while (true) {
			//count how many message i sended 
			count = 0;

			//To read line 
			String inp = sc.nextLine();
			// convert the String input into the byte array.
			buf = inp.getBytes();


			String messageSend = sendingMsgToServer(buf, ds, ip);
			
			if (messageSend.equals("bye")){
				System.exit(0);
				break;				
			}
			
		}
	}
	public boolean getAck(){
		return ackOk;
	}

	public byte[] getSeq(){
		return seq;
	}

	public byte[] getReceive() {
		return receive;
	}

	public  static void setDr(int num) throws SocketException {
	    dr = new DatagramSocket(num);
    }

    public int getCount(){
	    return count;
    }

	public static String sendingMsgToServer(byte[] buf, DatagramSocket ds, InetAddress ip) throws IOException {
		if (buf==null){
			return "Message was null, cant send it.";
		} else {
			// rdt2.0 generate the seq number
			seq = findSeq(seq);

			int seqLen = seq.length;
			int bufLen = buf.length;
			//Create de message with the seq number in the first two bytes
			byte[] mes = new byte[seqLen + bufLen];
			System.arraycopy(seq, 0, mes, 0, seqLen);
			System.arraycopy(buf, 0, mes, seqLen, bufLen);
			String msgSend = new String(data(mes));

			// Step 2 : Create the datagramPacket for sending the data. rdt1.0
			DatagramPacket DpSend = new DatagramPacket(mes, mes.length, ip, 1234);
			ds.send(DpSend);

			initTimer(DpSend,ds);
			//Create a thread to wait the ACK
			Thread t = new Thread(new reciveClass());
			t.start();


			//If input is "bye", close de connection
			String input = new String();
			if (input.equals("bye")) {
				return "bye";
			}

			return msgSend;
		}
	}

	public static void initTimer(DatagramPacket DpSend, DatagramSocket ds) {
		//Timer do actions when is certain time
		Timer timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//If there's timeout and I didn's send the message 3 times, resend it
				if ((!ackOk) && (count < 3)){
					try {
						System.out.println("Did't recive the ACK, sending again");
						ds.send(DpSend);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					count++;
				}

			}
		});
		timer.start();		
	}

	public static class reciveClass implements Runnable {
		private DatagramPacket DpRecive;


		public void run() {
			// rdt2.0
			ackOk = false;

			//Create de pck to send the ACK

			try {

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
}
