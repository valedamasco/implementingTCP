package exampleC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class EchoClientTest {

    private static EchoClient myClient = null;
    private static DatagramSocket ds = null;

    private static InetAddress ip = null;

    @BeforeClass
    public static void onceExecutedBeforeAll() throws IOException {
        myClient = new EchoClient();
        ds = new DatagramSocket();
		ip = InetAddress.getLocalHost();
        myClient.setDr(2345);

    }

    
    @Test
    public void dataSending() throws IOException {
        
        String pckSending = null;
        String message = "Sending datas";

        pckSending = myClient.sendingMsgToServer(message.getBytes(), ds , ip);
        assertEquals(message, pckSending);
    
    }

    @Test
    public void dataSendingNull() throws IOException {
        byte[] emptyMsg = null;
        String expectedOutPut = "Message was null, cant send it.";

        String actualMsg = myClient.sendingMsgToServer(emptyMsg, ds, ip);

        assertEquals(expectedOutPut,actualMsg) ;
    }

    @Test
    public void noMsgRecive() throws IOException {
        String message = "Try to send msg";
        String theMsg = myClient.sendingMsgToServer(message.getBytes(), ds, ip);

        boolean actuallOutput = myClient.getAck();


        assertFalse(actuallOutput); ;
    }

    @Test
    public void sendMsgAgain() throws IOException, InterruptedException {

        int expected = 3;
        String message = "Sending datas";
        myClient.sendingMsgToServer(message.getBytes(), ds , ip);
        TimeUnit.SECONDS.sleep(5);
        int actuall = myClient.getCount();

        assertEquals(expected,actuall);
    }

    @Test
    public void getResponse() throws IOException {
        String send = "Some msg";
        String theM = myClient.sendingMsgToServer(send.getBytes(),ds,ip);
        byte[] mySeq = myClient.getSeq();
        DatagramPacket sendAck = new DatagramPacket(mySeq, mySeq.length, ip, 2345);
        byte[] msgRecive = myClient.getReceive();
        String expect = String.valueOf(mySeq[0] + mySeq[0]);
        String real = String.valueOf(msgRecive[0]+msgRecive[1]);
        assertEquals(expect,real);
    }

 


}