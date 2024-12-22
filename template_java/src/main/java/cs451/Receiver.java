package cs451;

import java.lang.String;
import java.util.List;

public class Receiver extends Thread{

    private final Udp_receiver udpReceiver;
    private boolean running = false;
    private final int[] list_ports;
    private final Manager manager;


    public Receiver(int[] list_ports, int self_id, Manager manager){
        // Create the file to write.

        int self_port = list_ports[self_id];

        this.udpReceiver = new Udp_receiver(self_port);
        this.list_ports = list_ports;
        this.manager = manager;
    }

    @Override
    public void run(){
        System.out.println("Start receiver");
        running = true;

        while (running) {

            String received = udpReceiver.listen_message();
            Message message = Message.fromString(received);
            //System.out.println("Recu " + message);
            List<Message> answers = manager.receive(message);



            //System.out.println("Send " + (answers.isEmpty() ? "null" : answers.get(0)));
            //System.out.println();

            if(!answers.isEmpty()){
                // If it is a request send ack
                int port_sender = list_ports[answers.get(0).getIdDestination()];
                for (Message answer: answers){
                    //System.out.println("I answer this " + answer);
                    udpReceiver.sendBack(port_sender, answer);
                }
            }
        }
    }

    public void close(){
        udpReceiver.close();
    }
}

