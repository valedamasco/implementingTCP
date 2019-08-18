package exampleS;

// Java program to illustrate Server side 
// Implementation using DatagramSocket 
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class EchoServer {

    public static void main(String[] args) throws IOException {
        // Step 1 : Create a socket to listen at port 1234
        DatagramSocket ds = new DatagramSocket(1234);
        byte[] receive = new byte[65535];
        DatagramPacket DpReceive = null;

        // rdt2.0 create the ACK package
        byte[] seqIn = null;
        DatagramSocket ackMessage = new DatagramSocket();
        InetAddress ip = InetAddress.getLocalHost();

        while (true) {

            // Step 2 : create a DatgramPacket to receive the data.
            // rdt1.0
            DpReceive = new DatagramPacket(receive, receive.length);
            // rdt2.0
            seqIn = new byte[2];
            seqIn[0] = receive[0];
            seqIn[1] = receive[1];

            // Step 3 : revieve the data in byte buffer.
            ds.receive(DpReceive);

            DatagramPacket ackSend = new DatagramPacket(seqIn, seqIn.length, ip, 2345);
            // ackMessage.send(ackSend);
            System.out.println("ACK send will be: " + seqIn);
            // ackMessage.close();

            System.out.println("Client:-" + data(receive));

            if (data(receive).toString().equals("bye")) {
                System.out.println("Client sent bye.....EXITING");
                ds.close();
                break;
            }

            // Clear the buffer after every message.
            receive = new byte[65535];
        }
    }

    // A utility method to convert the byte array
    // data into a string representation.
    public static StringBuilder data(byte[] a) {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 2;
        while (i < a.length && a[i] != 0) {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }

}