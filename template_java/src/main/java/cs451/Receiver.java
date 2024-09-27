package cs451;

import java.lang.String;

public class Receiver {

    Udp_receiver udpReceiver;
    public Receiver(){
        udpReceiver = new Udp_receiver();
        udpReceiver.run();
    }
}