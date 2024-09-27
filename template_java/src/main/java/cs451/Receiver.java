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
            String message = "d ? " + received + "\n";
            try {
                fileWriter.write(message);
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