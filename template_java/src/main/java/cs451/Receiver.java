package cs451;

import java.lang.String;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
/*
public class Receiver extends Thread{

    private final Udp_receiver udpReceiver;
    private FileWriter fileWriter;
    private boolean running = false;
    private final int[] list_ports;
    private final int self_id;
    private final Messager messager;
    private final Detector detector;

    public Receiver(String outputFileName, int[] list_ports, int self_id,
                    Messager messager, Detector detector){
        // Create the file to write.
        try {
            fileWriter = new FileWriter(outputFileName);
        }catch (IOException e){
            System.err.println("Exception in receiver, init filewriter: " + e.getMessage());
        }

        int self_port = list_ports[self_id];

        this.udpReceiver = new Udp_receiver(self_port);
        this.list_ports = list_ports;
        this.self_id = self_id;
        this.messager = messager;
        this.detector = detector;
    }

    @Override
    public void run(){
        System.out.println("Start receiver");
        running = true;

        while (running) {

            String received = udpReceiver.listen_message();
            Message message = new Message(self_id, received);

            detector.update(message.getId_sender());
            Message answer = messager.receive(message);

            if (message.getType() != 'c' && message.getType() != 'C'){
                System.out.println("Recu " + message);
                System.out.println("Send " + answer);
                System.out.println();
            }


            if(answer != null){
                // If it is a request send ack
                int port_sender = list_ports[message.getId_sender()];
                udpReceiver.sendBack(port_sender, answer);
            }
        }
    }

    public void close(){
        running = false;
        try {
            fileWriter.flush();
            fileWriter.close();
        }catch (IOException e){
            System.err.println("Exception in receiver, close: " + e.getMessage());
        }
        udpReceiver.close();
        messager.close();
    }
}

 */