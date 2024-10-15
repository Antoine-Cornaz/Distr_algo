package cs451;

import java.lang.String;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import java.util.HashSet;
import java.util.Set;

import static cs451.Constants.SEPARATOR;
import static cs451.Constants.SEPARATOR_C;


public class Receiver {

    Udp_receiver udpReceiver;
    FileWriter fileWriter;
    Set<IdMessage> messageSeenSet;
    public Receiver(int port, String outputFileName){
        // Create the file to write.
        try {
            fileWriter = new FileWriter(outputFileName);
        }catch (IOException e){
            System.out.println(e);
        }


        messageSeenSet = new HashSet<>();
        udpReceiver = new Udp_receiver(port);


        boolean running = true;
        while (running){
            String received = udpReceiver.listen_message();
            if (received.equals("end")) {
                running = false;
                continue;
            }

            //same number of message as number of ','
            int amount_message = (int) received.chars().filter(c -> c == SEPARATOR_C).count();
            // split to have 0: id, 1: message1, 2: message2, ..., 8: message8
            String[] split_received = received.split(SEPARATOR);

            try {
                for (int i = 0; i < amount_message; i++) {
                    int message_number = Integer.parseInt(split_received[i + 1].trim());

                    int id = Integer.parseInt(split_received[0]);
                    IdMessage idMessage = new IdMessage(id, message_number);

                    if (!messageSeenSet.contains(idMessage)){
                        messageSeenSet.add(idMessage);

                        String message = "d " + split_received[0] + " " + message_number + "\n";
                        fileWriter.write(message);
                    }
                }

                fileWriter.flush();
            }catch (IOException e){
                System.out.println(e);
            }
        }
    }

    public void close(){
        try {
            fileWriter.close();
        }catch (IOException e){
            System.out.println(e);
        }
        udpReceiver.close();
    }
}