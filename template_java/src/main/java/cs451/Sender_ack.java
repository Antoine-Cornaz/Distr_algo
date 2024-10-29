package cs451;

import static cs451.Constants.SEPARATOR;
import static cs451.Constants.SEPARATOR_C;

public class Sender_ack extends Thread{

    private final Udp_receiver udpReceiver;
    private final boolean[] list_received;
    private final int number_message;

    public Sender_ack(int port_sender,
                      boolean[] list_received,
                      int number_message){

        this.udpReceiver = new Udp_receiver(port_sender);
        this.list_received = list_received;

        this.number_message = number_message;
    }

    public void run(){
        boolean finished = false;

        while (!finished){
            String received = udpReceiver.listen_message();
            //same number of message as number of ','
            int amount_message = (int) received.chars().filter(c -> c == SEPARATOR_C).count();
            //System.out.println("amount message " + amount_message);
            // split to have 0: id, 1: message1, 2: message2, ..., 8: message8
            String[] split_received = received.split(SEPARATOR);


            if(!received.isBlank() && amount_message != 0) {
                //System.out.print("received $" + received + "$  ");
                for (int j = 0; j < amount_message; j++) {
                    int message_number = 0;
                    try {
                        message_number = Integer.parseInt(split_received[j+1].trim());
                    }catch (Exception e){
                        System.err.println("sender_ack message break down. Some part are missing :( " + e.getMessage());
                        break;
                    }

                    list_received[message_number-1] = true;
                }
            }

            finished = allMessageReceived();
        }

    }

    private boolean allMessageReceived(){
        for (int i = 0; i < number_message; i++) {
            if (!list_received[i]) return false;
        }
        return true;
    }

}
