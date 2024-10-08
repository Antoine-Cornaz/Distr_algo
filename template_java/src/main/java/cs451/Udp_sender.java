package cs451;

import java.io.IOException;
import java.net.*;

import static cs451.Constants.MAX_TIME_OUT_MS;

/*
I use this udp packet example

https://www.baeldung.com/udp-in-java
 */

public class Udp_sender {
    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    public Udp_sender() {
        try {
            socket = new DatagramSocket();
            //address = InetAddress.getByName("localhost");
        } catch (SocketException  e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean send(String msg, String ip, int port) {
        //System.out.println("send echo msg " + msg + " ip " + ip + " port " + port);
        DatagramPacket packet;
        try {
            int length_msg = msg.length();
            buf = msg.getBytes();
            address = InetAddress.getByName(ip);

               packet = new DatagramPacket(buf, length_msg, address, port);

            socket.send(packet);
            //System.out.println("udp_sender: message sended " + msg);
            //socket.receive(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        try {
            socket.setSoTimeout(MAX_TIME_OUT_MS);
            try {
                socket.receive(packet);
            }catch (SocketTimeoutException e){
                //System.out.println("udp_sender time out reception");
                return false;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String received = new String(
                packet.getData(), 0, packet.getLength());
        //System.out.println("udp_sender: received " + received);
        return true;
    }

    public void close() {
        socket.close();
    }
}