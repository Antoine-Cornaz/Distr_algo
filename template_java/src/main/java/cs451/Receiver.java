package cs451;

import java.lang.String;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import java.util.HashSet;
import java.util.Set;

import static cs451.Constants.SEPARATOR;
import static cs451.Constants.SEPARATOR_C;


public class Receiver {

    private final Udp_receiver udpReceiver;
    private FileWriter fileWriter;
    private final Set<IdMessage> messageSeenSet;
    private boolean running = true;
    private final int[] ports;
    public Receiver(int port, String outputFileName, int[] ports){
        // Create the file to write.
        try {
            fileWriter = new FileWriter(outputFileName);
        }catch (IOException e){
            System.err.println("Exception in receiver, init filewriter: " + e.getMessage());
        }


        messageSeenSet = new HashSet<>();
        Udp_receiver tmp_receiver = null;
        while (tmp_receiver == null) {
            // We really need the receiver to be successfully
            try {
                tmp_receiver = new Udp_receiver(port);
            } catch (Exception e) {
                System.err.println("Exception in receiver, init: " + e.getMessage());
                tmp_receiver = null;
            }
        }

        udpReceiver = tmp_receiver;


        this.ports = ports;
    }

    public void start(){
        try {
            while (running) {

                String received = udpReceiver.listen_message();


                //same number of message as number of ','
                int amount_message = (int) received.chars().filter(c -> c == SEPARATOR_C).count();
                // split to have 0: id, 1: message1, 2: message2, ..., 8: message8
                String[] split_received = received.split(SEPARATOR);

                int port_send_back = ports[Integer.parseInt(split_received[0]) - 1];
                if (!running) {
                    System.out.println("Break receiver");
                    return;
                }
                udpReceiver.sendBack(port_send_back);

                for (int i = 0; i < amount_message; i++) {
                    int message_number = Integer.parseInt(split_received[i + 1].trim());

                    int id = Integer.parseInt(split_received[0]);
                    IdMessage idMessage = new IdMessage(id, message_number);
                    if (!running) {
                        System.out.println("Break receiver 2");
                        return;
                    }
                    messageSeenSet.add(idMessage);
                }
            }
        } catch (Exception e){
            System.err.println("Exception in receiver, start: " + e.getMessage());
            start();
        }
    }

    public void write() {
        System.out.println("Writing\n");

        try {
            for (IdMessage idMessage : messageSeenSet) {
                String message = "d " + idMessage.getId() + " " + idMessage.getMessageNumber() + "\n";

                fileWriter.write(message);
            }

            fileWriter.flush();
        } catch (IOException e) {
            System.err.println("Exception in receiver, write: " + e.getMessage());
        }
    }

    public void stop(){
        running = false;
        //System.out.println("Stop size message seen " + messageSeenSet.size() + "\n");
    }

    public void close(){
        try {
            fileWriter.close();
        }catch (IOException e){
            System.err.println("Exception in receiver, close: " + e.getMessage());
        }
        udpReceiver.close();
    }
}