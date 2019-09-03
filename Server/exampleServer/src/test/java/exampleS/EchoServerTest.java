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
    byte[] recive = new byte[6890];

    @BeforeClass
    public static void onceExecutedBeforeAll() throws IOException {
        myServer = new EchoServer();
        ds = new DatagramSocket(1234);
        ip = InetAddress.getLocalHost();
        seqQueue = new LinkedList<>();

    }

    @Test
    public void getMsg() throws IOException {
        String msgExpected  = "Client:-My new msg";
        Thread myMsgThread = new Thread(new CreateMsg());
        myMsgThread.start();
        String actualMsg = myServer.waitMsg( ip,  ds,  seqQueue, recive);
        assertEquals(msgExpected,actualMsg);
    }

    @Test
    public void sendAck() throws IOException {
        byte[] seqExpected = {0,1};
        int seqE = seqExpected[0] + seqExpected[1];
        Thread myMsgThread = new Thread(new CreateMsg());
        myMsgThread.start();
        String actualMsg = myServer.waitMsg( ip,  ds,  seqQueue, recive);
        byte[] seqReal = myServer.getSeqIn();
        int seqR = seqReal[0] + seqReal[1];

        assertEquals(seqE,seqR);
    }

    @Test
    public void elemRepeated() throws IOException {
        String expected = "Element is repeated.";
        Thread myMsgThread1 = new Thread(new CreateMsg());
        myMsgThread1.start();
        String actual = myServer.waitMsg( ip,  ds,  seqQueue, recive);
        assertEquals(expected,actual);
    }



    public static class CreateMsg implements Runnable {
        public void run() {
            while (true) {
                String msgToRecive = "My new msg";
                byte[] buf = new byte[6660];
                byte[] seq = new byte[2];
                seq[0] = 0;
                seq[1] = 1;
                int seqLen = 2;
                int bufLen = msgToRecive.getBytes().length;
                byte[] mes = new byte[seqLen + bufLen];
                System.arraycopy(seq, 0, mes, 0, seqLen);
                System.arraycopy(msgToRecive.getBytes(), 0, mes, seqLen, bufLen);
                try {
                    DatagramPacket myMsg = new DatagramPacket(mes, mes.length, ip, 1234);
                    ds.send(myMsg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
