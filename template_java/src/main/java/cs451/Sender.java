package cs451;

import java.lang.String;

public class Sender {
    private int number_message;
    private int[] list_message_num;
    private int number_destination;
    private int[] list_message_ack;
    private String[] hosts;
    public Sender(int number_message, int[] messages, int number_destination, int destination[], String hosts[]){
        this.number_message = number_message;
        this.list_message_num = messages;
        this.number_destination = number_destination;
        this.list_message_ack = destination;
        this.hosts = hosts;
    }

    public void send(int[] message, int destination_number, String host){

    }
}