package cs451;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.String;
import java.util.Arrays;

import static cs451.Constants.*;

/*
Class Sender


Will use Udp_sender to send message 1 by one.
 */

//for thread
// https://www.geeksforgeeks.org/multithreading-in-java/
public class Sender extends Thread {
    private final int number_message;
    private final int id_sender;

    // 1 address is ip and port
    private final String list_ip;
    private final int list_port;

    private final boolean[] list_received;
    private final boolean[] list_send;

    private final Udp_sender udpSender;
    private final FileWriter fileWriter;
    private boolean isRunning = true;
    private int last_check_message = 0;

    public Sender(int number_message,
                  int id_sender,
                  String ips,
                  int ports,
                  String fileName,
                  boolean[] list_received){
        this.number_message = number_message;
        this.list_ip = ips;
        this.list_port = ports;
        this.id_sender = id_sender;

        // Initialize to false
        this.list_received = list_received;
        list_send = new boolean[number_message];

        Udp_sender tmp_sender = null;
        while (tmp_sender == null){
            try {
                tmp_sender = new Udp_sender();
            }catch (Exception e) {
                System.err.println("Exception in sender, init: " + e.getMessage());
            }
        }
        udpSender = tmp_sender;
        //udpReceiver = new Udp_receiver(port_sender);

        try {
            this.fileWriter = new FileWriter(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public void run(){
        System.out.println("Start run");
        int[] messages = new int[number_message];
        for (int i = 0; i < number_message; i++) {
            messages[i] = i;
        }
        send(messages, number_message);

        // Horrible way to pass an int by reference. to have 2 outputs.
        // amount
        int[] amountNotReceived = {number_message};
        int[] notReceived = init_list_notReceived();
        while (amountNotReceived[0] != 0 && isRunning){
            send(notReceived, amountNotReceived[0]);
            notReceived = notReceivedMsg(amountNotReceived);
        }
    }

    /*
    amountNotReceived: is an output. The number of message not received.
    Return 1: first_message_not_send, 2: second_message_not_send, ...
     */
    private int[] notReceivedMsg(int[] amountNotReceived){

        int[] notSend = new int[number_message];
        int j = 0;
        int index=0;
        for (int i = 0; i < number_message; i++) {
            index = (last_check_message + i) % number_message;
            if(! list_received[index]){
                notSend[j] = index;
                j++;

                /*if(j >= 10000_000*MAX_MESSAGE_PER_PACKET){
                    // We don't need more than 10'000 messages in the list.
                    break;
                }*/
            }
        }
        last_check_message = index;
        amountNotReceived[0] = j;
        return notSend;
    }

    private int[] init_list_notReceived(){

        int[] notSend = new int[number_message];
        for (int i = 0; i < number_message; i++) {
            notSend[i] = i;
        }
        return notSend;
    }

    private void send(int[] messages, int number_message){

        int i = 0;
        while (i < number_message){
            int[] message_send = new int[MAX_MESSAGE_PER_PACKET];
            Arrays.fill(message_send, -1);

            // Create message :
            // "id m1"
            StringBuilder sb = new StringBuilder();
            sb.append(id_sender);
            sb.append(SEPARATOR);
            int message = messages[i] + 1;
            sb.append(message);
            message_send[0] = i;
            list_send[message_send[0]] = true;

            // Create message " m2 m3 m4 m5"
            for (int j = 0; j < MAX_MESSAGE_PER_PACKET - 1; j++) {
                // i is increased at the end of the loop so we don't need j.
                int index = i + 1;

                if(index >= number_message) break;

                /*
                When dealing with multiple destination
                if(! ip.equals(list_ip[index])) break;
                if(port != list_port[index]) break;
                 */

                // We can stack multiple message next to each other.
                sb.append(SEPARATOR_C);
                int message2 = messages[index]+1;
                sb.append(message2);

                message_send[j+1] = index;
                list_send[index] = true;

                i++;
            }

            // Add end of message
            String composed_message = sb.toString();
            //System.out.println("composed_message $" + composed_message+ "$");
            if(!isRunning) {
                System.out.println("Break Sender");
                return;
            }
            udpSender.send(composed_message, list_ip, list_port);
            //System.out.println("Received : " + received);
            i++;
        }
    }

    public void write() {
        System.out.println("Writing\n");
        for (int i = 0; i < number_message; i++) {
            if (list_send[i]){
                int i_1 = i+1;
                String message = "b " + i_1 + "\n";
                try {
                    fileWriter.write(message);
                } catch (IOException e) {
                    System.err.println("Exception in sender, write: " + e.getMessage());
                }
            }
        }
        try {
            fileWriter.flush();
        } catch (IOException e) {
            System.err.println("Exception in receiver, write2: " + e.getMessage());
        }
    }

    public void stop_message(){
        isRunning = false;
    }

    public void close(){
        udpSender.close();
    }
}