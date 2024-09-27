package cs451;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/*
I use this udp packet exemple

https://www.baeldung.com/udp-in-java
 */

public class Udp_receiver extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    private byte[] buf_answer = new byte[256];

    public Udp_receiver(int port)  {
        System.out.println("Udp_receiver port " + port);
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public String listen_message() {
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        packet = new DatagramPacket(buf, buf.length, address, port);
        String received
                = new String(packet.getData(), 0, packet.getLength());
        System.out.println("udp_receiver: received " + received);


        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return received;

    }

    public void close(){
        socket.close();
    }
}
