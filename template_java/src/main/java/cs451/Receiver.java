package cs451;

import java.lang.String;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors


public class Receiver {

    Udp_receiver udpReceiver;
    FileWriter fileWriter;
    public Receiver(int port, String ouputFileName){
        try {
            fileWriter = new FileWriter(ouputFileName);
        }catch (IOException e){
            System.out.println(e);
        }

        udpReceiver = new Udp_receiver(port);
        String received;
        boolean running = true;
        while (running){
            received = udpReceiver.listen_message();
            if (received.equals("end")) {
                running = false;
                continue;
            }

            //same number of message as number of space
            int amount_message = (int) received.chars().filter(c -> c == (int) ' ').count();
            // split to have 1 id, 2 message1, 3 message2, ..., 9 message8
            String[] split_received = received.split(" ");

            //TODO check if message already got.

            try {
                for (int i = 0; i < amount_message; i++) {
                    String message = "d " + split_received[0] + " " + split_received[i + 1] + "\n";
                    fileWriter.write(message);
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