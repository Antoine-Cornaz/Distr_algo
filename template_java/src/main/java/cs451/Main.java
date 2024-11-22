package cs451;

import java.util.List;
import java.lang.System;

import static cs451.Constants.INITIAL_PING_TIME_MS;


public class Main {

    static Receiver receiver;
    static Sender sender;

    private static void handleSignal() {
        //immediately stop network packet processing
        System.out.println("Immediately stopping network packet processing.");

        if (receiver != null) receiver.stop();
        if(sender != null) sender.stop_message();

        //write/flush output file if necessary
        System.out.println("Writing output.");

        //finish the program when asked to finish
        if(receiver != null) {
            receiver.close();
        }


        if(sender != null){
            sender.close();
        }

    }

    private static void initSignalHandlers() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                handleSignal();
            }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        Parser parser = new Parser(args);
        parser.parse();

        initSignalHandlers();

        // example
        long pid = ProcessHandle.current().pid();
        System.out.println("My PID: " + pid + "\n");
        System.out.println("From a new terminal type `kill -SIGINT " + pid + "` or `kill -SIGTERM " + pid + "` to stop processing packets\n");

        System.out.println("My ID: " + parser.myId() + "\n");
        System.out.println("List of resolved hosts is:");
        System.out.println("==========================");
        for (Host host: parser.hosts()) {
            System.out.println(host.getId());
            System.out.println("Human-readable IP: " + host.getIp());
            System.out.println("Human-readable Port: " + host.getPort());
            System.out.println();
        }
        System.out.println();

        System.out.println("Path to output:");
        System.out.println("===============");
        System.out.println(parser.output() + "\n");

        System.out.println("Path to config:");
        System.out.println("===============");
        System.out.println(parser.config() + "\n");

        System.out.println("Doing some initialization\n");
        initialize(parser);

        System.out.println("Broadcasting and delivering messages...\n");

        System.out.println("output parser:" + parser.output() + "\n");

        // After a process finishes broadcasting,
        // it waits forever for the delivery of messages.

        sender.start();
        receiver.start();


        while (true) {
            // Sleep for 1 hour
            Thread.sleep(60 * 60 * 1000);
        }
    }


    private static void initialize(Parser parser){


        List<Host> hosts = parser.hosts();
        int numberProcess = hosts.size();
        int[] list_ports = new int[numberProcess];
        String[] list_ip = new String[numberProcess];

        for (int i = 0; i < numberProcess; i++) {
            list_ports[i] = hosts.get(i).getPort();
            list_ip[i] = hosts.get(i).getIp();
        }
        MyWriter myWriter = new MyWriter(parser.output());

        int self_id = parser.myId()-1;
        int number_message = parser.getNumberMessage();
        Messager messager = new Messager(numberProcess, self_id, number_message, myWriter);
        Detector detector = new Detector(numberProcess, self_id, INITIAL_PING_TIME_MS);
        String fileName = parser.output();

        sender = new Sender(
                list_ports,
                list_ip,
                self_id,
                messager,
                detector
        );

        receiver = new Receiver(fileName, list_ports, self_id, messager, detector);
    }
}