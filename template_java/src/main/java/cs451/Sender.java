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
public class Sender {
    private final int number_message;
    private final int[] list_message_num;
    private final int id_sender;

    // 1 address is ip and port
    private final String[] list_ip;
    private final int[] list_port;

    private final boolean[] list_received;

    private final Udp_sender udpSender;
    private final FileWriter fileWriter;

    public Sender(int number_message, int[] messages, int id_sender, String[] ips, int[] ports, String fileName){
        this.number_message = number_message;
        this.list_message_num = messages;
        this.list_ip = ips;
        this.list_port = ports;
        this.id_sender = id_sender;

        // Initialize to false
        list_received = new boolean[number_message];

        udpSender = new Udp_sender();
        try {
            this.fileWriter = new FileWriter(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("number message " + number_message);
        for (int i = 0; i < number_message; i++) {
            System.out.println(
                    "message " + messages[i] +
                    " id sender " + id_sender +
                    " ip " + ips[i] +
                    " ports " + ports[i] +
                    " received " + list_received[i]);
        }
    }

    public void start(){
        int[] messages = new int[number_message];
        for (int i = 0; i < number_message; i++) {
            messages[i] = i;
        }
        send(messages, number_message);

        // Horrible way to pass an int by reference. to have 2 outputs.
        // amount
        int[] amountNotReceived = new int[1];
        int[] notReceived = notReceivedMsg(amountNotReceived);
        while (amountNotReceived[0] != 0){
            send(notReceived, amountNotReceived[0]);
            notReceived = notReceivedMsg(amountNotReceived);
        }
        System.out.println("all messages successfully sent");
    }

    /*
    amountNotReceived: is an output. The number of message not received.
    Return 1: first_message_not_send, 2: second_message_not_send, ...
     */
    private int[] notReceivedMsg(int[] amountNotReceived){
        int[] notSend = new int[number_message];
        int j = 0;
        for (int i = 0; i < number_message; i++) {
            if(! list_received[i]){
                notSend[j] = i;
                j++;
            }
        }
        amountNotReceived[0] = j;
        return notSend;
    }

    private void send(int[] messages, int number_message){

        int i = 0;
        while (i < number_message){
            int[] message_send = new int[MAX_MESSAGE_PER_PACKET];
            Arrays.fill(message_send, -1);

            String ip = list_ip[i];
            int port = list_port[i];

            // Create message :
            // "id m1"
            StringBuilder sb = new StringBuilder();
            sb.append(id_sender);
            sb.append(SEPARATOR);
            sb.append(list_message_num[messages[i]]);
            message_send[0] = i;

            // Create message " m2 m3 m4 m5"
            for (int j = 0; j < MAX_MESSAGE_PER_PACKET - 1; j++) {
                int index = i + 1;
                System.out.println("index " + index);

                if(index >= number_message) break;
                System.out.println("index2 " + index);

                if(! ip.equals(list_ip[index])) break;
                System.out.println("index3 " + index);

                if(port != list_port[index]) break;
                System.out.println("index4 " + index);

                // We can stack multiple message next to each other.
                sb.append(SEPARATOR_C);
                sb.append(list_message_num[messages[index]]);


                message_send[j+1] = index;

                i++;
            }

            // Add end of message
            String composed_message = sb.toString();

            boolean ack = udpSender.send(composed_message, ip, port);

            if(ack) {
                try {
                    for (int j = 0; j < MAX_MESSAGE_PER_PACKET && message_send[j] != -1; j++) {
                        if( !list_received[message_send[j]]){
                            list_received[message_send[j]] = true;
                            fileWriter.write("b " + list_message_num[message_send[j]] + "\n");
                        }
                    }
                    fileWriter.flush();
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
            i++;
        }
    }

    public void close(){
        udpSender.close();
    }
}