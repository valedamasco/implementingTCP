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
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.TimerTask;

import javax.swing.Timer;

public class EchoClient {

	public static void main(String args[]) throws IOException {
		Scanner sc = new Scanner(System.in);

		// Step 1:Create the socket object for
		// carrying the data.
		DatagramSocket ds = new DatagramSocket();
		InetAddress ip = InetAddress.getLocalHost();
		byte buf[] = null;

		// rdt2.0
		byte mes[] = null;
		byte seq[] = null;
		// message recive

		DatagramPacket DpSend = null;

		boolean ackOk = false;

		while (true) {

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

			Timer timer = new Timer(1000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// System.out.println("Timer!");
				}
			});
			timer.start();

			Thread t = new Thread(new reciveClass(seq,ackOk));
			t.start();

			System.out.println(ackOk);
			
			while ( ackOk ){
				System.out.println("Entra al while del t");
			}

			if (inp.equals("bye"))
				break;

		}
	}

	private static class reciveClass implements Runnable {

		byte[] seqIn;
		boolean ackOk;

		reciveClass(byte[] seqIn, boolean ack) {
			this.seqIn = seqIn;
			this.ackOk = ack;
		}

		public void run() {

			DatagramPacket DpReceive = null;
			DatagramSocket respon;
			boolean ackOk = false;

			try {
				respon = new DatagramSocket(2345);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			byte[] receive = new byte[65535];

			respon = null;

			// rdt2.0
			DpReceive = new DatagramPacket(receive, receive.length);
			try {
				respon.receive(DpReceive);
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("despues del recibe");
			this.ackOk = (receive[0] == this.seqIn[0]) && (receive[1] == this.seqIn[1]);
			// System.out.println("ACK " + ackOk);
			System.out.println("ACK recibe: " + this.seqIn);

		}
	}

	public static byte[] findSeq(byte[] seq) {
		byte[] ret = seq;
		if (ret == null) {
			ret = new byte[2];
			ret[0] = Byte.MIN_VALUE;
			ret[1] = Byte.MIN_VALUE;
		} else {
			if (ret[1] == Byte.MAX_VALUE) {
				ret[0]++;
			} else {
				ret[1]++;
			}
		}
		return ret;
	}
}
