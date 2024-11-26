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
    private final Messager messager;
    private final Detector detector;


    private final Manager manager;
    private final Udp_sender udpSender;
    private boolean isRunning = false;

    public Sender(int[] list_port,
                  String[] list_ip,
                  int self_id,
                  Messager messager,
                  Detector detector){


        this.list_port = list_port;
        this.list_ip = list_ip;
        this.messager = messager;
        this.detector = detector;

        assert list_port.length == list_ip.length;

        int number_processes = list_port.length;
        this.manager = new Manager(number_processes, self_id);

        udpSender = new Udp_sender();
    }


    @Override
    public void run(){

        isRunning = true;

        while (isRunning) {
            List<Integer> manage_id = manager.get_manage();
            List<Message> messages = messager.getMessages(manage_id);

            for (Message m : messages) {
                String ip = list_ip[m.getId()];
                int port = list_port[m.getId()];
                if (!isRunning) break;
                udpSender.send(m.getContent(), ip, port);
                //System.out.println("message sent " + m);
            }

            // Say to the OS if you want to pause it's the right time
            Thread.yield();

            try {
                TimeUnit.MILLISECONDS.sleep(5);
            } catch (InterruptedException ignored) {}


            detector.update_time();

            List<Integer> list_new_alive = detector.get_new_resurect();
            if(!list_new_alive.isEmpty()){
                for (int alive: list_new_alive){
                    System.out.println("new alive " + alive);
                }
            }
            manager.setAlive(list_new_alive);
            messager.killSubstitute(list_new_alive);


            List<Integer> list_new_dead = detector.get_new_dead();
            if(!list_new_dead.isEmpty()){
                for (int dead: list_new_dead){
                    System.out.println("new dead " + dead);
                }
            }
            manager.setDead(list_new_dead);



        }
    }

    public void stop_message(){
        isRunning = false;
    }

    public void close(){
        udpSender.close();
    }
}