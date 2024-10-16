package cs451;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

import static cs451.Constants.NO_CHAR;

/*
I use this udp packet example

https://www.baeldung.com/udp-in-java
 */

public class Udp_receiver extends Thread {

    private final DatagramSocket socket;
    private final byte[] buf = new byte[256];
    private DatagramPacket packet;

    public Udp_receiver(int port)  {
        System.out.println("Udp_receiver port " + port);
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public String listen_message() {

        // Clear the buf.
        Arrays.fill(buf, (byte) NO_CHAR);

        packet = new DatagramPacket(buf, buf.length);
        System.out.println("not sender_ack, not received " + socket.getPort());
        try {
            socket.receive(packet);
            System.out.println("not sender_ack, received " + socket.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        packet = new DatagramPacket(buf, buf.length, address, port);
        String received
                = new String(packet.getData(), 0, packet.getLength());
        //System.out.println("udp_receiver: received " + received);

        return received;
    }

    public void sendBack(int port){
        try {
            InetAddress address = packet.getAddress(); // Use the same address as received
            packet = new DatagramPacket(packet.getData(), packet.getLength(), address, port);
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close(){
        socket.close();
    }
}
