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
    private final int[] list_port;
    private final String[] list_ip;
    private final int self_id;
    private final int number_message;
    private final Messager messager;
    private final Detector detector;


    private final Udp_sender udpSender;
    private final FileWriter fileWriter;
    private boolean isRunning = true;
    private int last_check_message = 0;

    public Sender(int[] list_port,
                  String[] list_ip,
                  int self_id,
                  int number_message,
                  Messager messager,
                  Detector detector,
                  String fileName){

       this.list_port = list_port;
       this.list_ip = list_ip;
       this.self_id = self_id;
       this.number_message = number_message;
       this.messager = messager;
       this.detector = detector;


        Udp_sender tmp_sender = null;
        while (tmp_sender == null){
            try {
                tmp_sender = new Udp_sender();
            }catch (Exception e) {
                System.err.println("Exception in sender, init: " + e.getMessage());
            }
        }
        udpSender = tmp_sender;

        try {
            this.fileWriter = new FileWriter(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public void run(){
        // TODO
    }


    public void write() {
        // TODO
    }

    public void stop_message(){
        isRunning = false;
    }

    public void close(){
        udpSender.close();
    }
}