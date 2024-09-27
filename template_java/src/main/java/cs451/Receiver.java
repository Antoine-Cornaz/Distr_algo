package cs451;

import java.lang.String;

public class Receiver {

    Udp_receiver udpReceiver;
    public Receiver(int port){
        udpReceiver = new Udp_receiver(port);
        udpReceiver.run();
    }
}