package cs451;

import java.io.IOException;
import java.net.*;

/*
I use this udp packet exemple

https://www.baeldung.com/udp-in-java
 */

public class Udp_sender {
    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    public Udp_sender() {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public String sendEcho(String msg) {
        buf = msg.getBytes();
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, 5555);
        try {
            socket.send(packet);
            System.out.println("udp_sender: message sended " + msg);
            //socket.receive(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*packet = new DatagramPacket(buf, buf.length);
        String received = new String(
                packet.getData(), 0, packet.getLength());
        System.out.println("udp_sender: received " + received);
        return received;
         */
        //Temporary return nothing
        return "";
    }

    public void close() {
        socket.close();
    }
}