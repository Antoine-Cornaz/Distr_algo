package cs451;

import java.io.IOException;
import java.net.*;

/*
I use this udp packet example

https://www.baeldung.com/udp-in-java
 */

public class Udp_sender {
    private final DatagramSocket socket;

    public Udp_sender() {
        try {
            socket = new DatagramSocket();
        } catch (SocketException  e) {
            throw new RuntimeException(e);
        }
    }

    public void send(String msg, String ip, int port) {
        //System.out.println("send echo msg " + msg + " ip " + ip + " port " + port);
        DatagramPacket packet;
        try {
            int length_msg = msg.length();
            byte[] buf = msg.getBytes();
            InetAddress address = InetAddress.getByName(ip);

               packet = new DatagramPacket(buf, length_msg, address, port);

            socket.send(packet);
            //System.out.println("udp_sender: message sent " + msg);

        } catch (IOException e) {
            System.err.println("Exception in sender, send: " + e.getMessage());
        }
    }

    public void close() {
        socket.close();
    }
}