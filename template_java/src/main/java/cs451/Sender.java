package cs451;

import java.lang.String;

public class Sender {
    private int number_message;
    private int[] list_message_num;

    // 1 addresse is id, ip and port
    private int[] list_message_id;
    private String[] list_ip;
    private int[] list_port;

    private boolean[] list_received;

    private Udp_sender udpSender;

    public Sender(int number_message, int[] messages, int[] ids, String[] ips, int[] ports){
        this.number_message = number_message;
        this.list_message_num = messages;
        this.list_message_id = ids;
        this.list_ip = ips;
        this.list_port = ports;

        // Initialize to false
        list_received = new boolean[number_message];

        udpSender = new Udp_sender();

        System.out.println("number message " + number_message);
        for (int i = 0; i < number_message; i++) {
            System.out.println(
                    "message " + messages[i] +
                    " id " + ids[i] +
                    " ip " + ips[i] +
                    " ports " + ports[i] +
                    " received " + list_received[i]);
        }
    }

    public void send(int[] message, int number_message){
        for (int i = 0; i < number_message; i++) {
            int message_number = message[i];
            String message_s = String.valueOf(list_message_num[message_number]);
            String ip = list_ip[message_number];
            int port = list_port[message_number];
            udpSender.sendEcho(message_s, ip, port);
        }
    }

    public void close(){
        udpSender.close();
    }
}