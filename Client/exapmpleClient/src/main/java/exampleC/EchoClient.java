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

	private static DatagramPacket DpSend = null;
	private static DatagramSocket ds = null;
	private static byte buf[] = null;
	private static byte mes[] = null;
	private static byte seq[] = null;

		
	private static DatagramSocket respon = null;
	private static DatagramPacket DpReceive = null;
	private static byte[] receive = new byte[65535];

	private static int count = 0;

	public static void main(String args[]) throws IOException {
		Scanner sc = new Scanner(System.in);

		// Step 1:Create the socket object for
		// carrying the data.
		ds = new DatagramSocket();
		ip = InetAddress.getLocalHost();
		

		// rdt2.0

		// message recive

		respon = new DatagramSocket(2345);
		
		while (true) {
			count = 0;
			
			String inp = sc.nextLine();

			// convert the String input into the byte array.
			buf = inp.getBytes();

			// rdt2.0 generate the seq number
			seq = findSeq(seq);

			int seqLen = seq.length;
			int bufLen = buf.length;
			mes = new byte[seqLen + bufLen];
			System.arraycopy(seq, 0, mes, 0, seqLen);
			System.arraycopy(buf, 0, mes, seqLen, bufLen);

			// Step 2 : Create the datagramPacket for sending the data.
			// rdt1.0
			DpSend = new DatagramPacket(mes, mes.length, ip, 1234);
			ds.send(DpSend);

			if (inp.equals("bye")) {
				//ds.close();
				break;
			}


			Timer timer = new Timer(3000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
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

			Thread t = new Thread(new reciveClass());
			t.start();


			

		}
	}

	private static class reciveClass implements Runnable {



		public void run() {	
			try {			
				// rdt2.0
				DpReceive = new DatagramPacket(receive, receive.length);
				respon.receive(DpReceive);
				int first = receive[0];
				int second = receive[1];
				ackOk = (first==seq[0]) && (second==seq[1]);
				if (ackOk) {
					System.out.println("Message was recive.");
				}
				receive = new byte[65535];

				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

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
