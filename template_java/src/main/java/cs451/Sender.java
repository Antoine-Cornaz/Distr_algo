package cs451;

import java.lang.String;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
Class Sender


Will use Udp_sender to send message 1 by one.
 */

//for thread
// https://www.geeksforgeeks.org/multithreading-in-java/

public class Sender extends Thread {
    private final int[] list_port;
    private final String[] list_ip;
    private final Manager manager;

    private final Udp_sender udpSender;
    private boolean isRunning = false;

    public Sender(int[] list_port,
                  String[] list_ip,
                  Manager manager){


        this.list_port = list_port;
        this.list_ip = list_ip;
        this.manager = manager;

        udpSender = new Udp_sender();
    }


    @Override
    public void run(){

        isRunning = true;

        while (isRunning) {
            List<Message> messages = manager.getMessages();
            for (Message m : messages) {
                String msg = m.toContent();
                if(m.getIdDestination() >= list_ip.length){
                    System.err.println("Problem sender " + m.getIdDestination() + " " + list_ip.length);
                    System.err.println(m);
                }
                System.out.println("set " + m.getProposalValues() + " type " + m.getType());
                String ip = list_ip[m.getIdDestination()];
                int port = list_port[m.getIdDestination()];
                System.out.println("Send " + messages);
                udpSender.send(msg, ip, port);
            }

            //Thread.yield();
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException ignore) {}

        }
    }

    public void stop_message(){
        isRunning = false;
    }

    public void close(){
        udpSender.close();
        manager.close();
    }
}

