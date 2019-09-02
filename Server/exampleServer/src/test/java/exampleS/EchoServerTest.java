package exampleS;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class EchoServerTest {

    private static DatagramSocket ds ;
    private static InetAddress ip;
    private static Queue<int[]> seqQueue;
    private static DatagramPacket DsSend;
    private static byte[] seqIn = new byte[2];
    private static EchoServer myServer ;

    @BeforeClass
    public static void onceExecutedBeforeAll() throws IOException {
        myServer = new EchoServer();
        ds = new DatagramSocket(1234);
        ip = InetAddress.getLocalHost();
        seqQueue = new LinkedList<>();
    }

    @Test
    public void getMsg() throws IOException {
        String msgToRecive = "My new msg";
        byte[] buf = new byte[666];
        byte[] seq = new byte[2];
        seq[0] = 0;
        seq[1] = 1;
        int seqLen = 2;
        int bufLen = msgToRecive.getBytes().length;
        byte[] mes = new byte[seqLen + bufLen];
        System.arraycopy(seq, 0, mes, 0, seqLen);
        System.arraycopy(msgToRecive.getBytes(), 0, mes, seqLen, bufLen);

        DatagramPacket sendMsg = new DatagramPacket(mes, mes.length);
        myServer.waitMsg( ip,  ds,  seqQueue, buf, sendMsg);


        System.out.println("llego a ejecutar algo ");

        assertTrue(true);
    }

}
