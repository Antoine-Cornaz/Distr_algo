package cs451;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;


public class Main {

    private static void handleSignal() {
        //immediately stop network packet processing
        System.out.println("Immediately stopping network packet processing.");

        //write/flush output file if necessary
        System.out.println("Writing output.");

        //finish the program when asked to finish
        System.exit(0);
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
        while (true) {
            // Sleep for 1 hour
            Thread.sleep(60 * 60 * 1000);
        }
    }


    private static void initialize(Parser parser){

        String config_path = parser.config();
        int index_receive = parser.getIndexReceive();

        if(parser.myId() == index_receive){
            // Receiver
            System.out.println("I'm the receiver\n");
            Receiver receiver = new Receiver();
        }else{
            System.out.println("I'm a sender\n");
            int number_message = parser.getNumberMessage();

            List<Host> hosts = parser.hosts();


            // Create messages and destinations
            int[] messages = new int[number_message];

            int[] destination_id = new int[number_message];
            String[] destination_ip = new String[number_message];
            int[] destination_port = new int[number_message];

            for (int i = 0; i < number_message; i++) {
                messages[i] = i;
                int destination_num = index_receive;

                Host hosts_receiver = hosts.get(destination_num-1);

                // I hop the id  is equal to the id number in the list
                assert destination_num == hosts_receiver.getId();

                destination_id[i] = hosts_receiver.getId();
                destination_ip[i] = hosts_receiver.getIp();
                destination_port[i] = hosts_receiver.getPort();
            }

            Sender sender = new Sender(
                    number_message,
                    messages,
                    destination_id,
                    destination_ip,
                    destination_port);

            int[] message2send = {0, 1, 2};
            sender.send(message2send, 3);


        }
    }
}
