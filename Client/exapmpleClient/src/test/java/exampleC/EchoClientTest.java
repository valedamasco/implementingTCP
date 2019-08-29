package exampleC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;



public class EchoClientTest {

    private static EchoClient myClient = null;
    private static DatagramSocket ds = null;
    private static DatagramSocket dr = null;
    private static DatagramPacket DpSend = null;
    //private static DatagramPacket DpRecive = null;

    //Var to have the info to send and recive 
    //private static byte buf[] = null;
    //private static byte mes[] = null;
    //private static byte[] receive = new byte[65535];
    //Var to use as de seq number before de message 
    //private static byte seq[] = null;
    //private static String inp = null;
    private static InetAddress ip = null;

    @BeforeClass
    public static void onceExecutedBeforeAll() throws IOException {
        myClient = new EchoClient();
        ds = new DatagramSocket();
		dr = new DatagramSocket(2345);
		ip = InetAddress.getLocalHost();
        
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

 


}