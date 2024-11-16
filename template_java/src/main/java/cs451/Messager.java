package cs451;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static cs451.Constants.BATCH_SIZE;

public class Messager {

    private final int number_processes;
    private final int self_process;
    private final Set<Message> setA;
    private final Set<Message> setB;
    private final Roulette[] roulettes;
    private final Writer writer;
    public Messager(int number_processes, int self_process, int number_messages, String fileName){

        this.number_processes = number_processes;
        this.self_process = self_process;

        setA = new HashSet<>();
        setB = new HashSet<>();

        roulettes = new Roulette[number_processes];
        for (int i = 0; i < number_processes; i++) {
            roulettes[i] = new Roulette(0, number_messages, i, BATCH_SIZE, self_process);
        }

        try {
            writer = new FileWriter(fileName);
        } catch (IOException e) {
            System.err.println("Messager, filename incorrect " + e);
            throw new RuntimeException(e);
        }
    }

    public void receive(Message message){
        //System.out.println("message recu " + message.getType());
        switch (message.getType()){
            case 'A':
                setA.add(message);
                for (int message_number : message.getMessage_numbers()){
                    roulettes[message.getId_sender()].increase_value(message_number, Roulette.SENT);
                    //System.out.println("update roulette sender: " + message.getId_sender());
                    update_to_confirm_if_majority(message_number);
                }
                break;
            case 'B':
                setB.add(message);
                //System.out.println("confirmed " + message.getMessage_numbers()[0]);
                for (int message_number : message.getMessage_numbers()){
                    roulettes[message.getId_sender()].increase_value(message_number, Roulette.CONFIRMED);
                    writeDeliver(message.getId_sender(), message_number);
                }
                break;

            case 'C':
                //TODO
                break;

            case 'D':
                //TODO
                break;

            case 'a':
                // TODO
                break;

            case 'b':
                // TODO
                break;

            case 'c':
                // TODO
                break;

            case 'd':
                // TODO
                break;

            default:
                System.err.println("type " + message.getType());
        }

    }

    public List<Message> getMessages(List<Integer> manage_id){
        ArrayList<Message> messageList = new ArrayList<>();

        for (int i = 0; i < number_processes; i++) {
            roulettes[i].add_messages(messageList);
        }

        return messageList;
    }

    public void update_to_confirm_if_majority (int msg_number){
        int counter_receive = 0;

        for (int i = 0; i < number_processes; i++) {
            byte state = roulettes[i].getState(msg_number);
            if (state == Roulette.SENT) counter_receive++;

            // If already confirmation stage doesn't need to update
            if (state > Roulette.SENT) return;
        }

        //System.out.println("counter_receive " + counter_receive);
        if (counter_receive > number_processes/2){
            // 50% < received
            // if majority received
            writeBroadCast(msg_number);
            for (int i = 0; i < number_processes; i++) {
                roulettes[i].increase_value(msg_number, Roulette.TO_CONFIRM);
            }
        }
    }

    private void writeDeliver(int sender, int msg_number){
        //System.out.println("write deliver sender: " + sender + " msg: " + msg_number);

        // shift bc msg and sender start at 1.
        String message = "d " + (sender+1) + " " + (msg_number+1) + "\n";
        try {
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            System.err.println(e);
        }

    }

    private void writeBroadCast(int msg_number){
        //System.out.println("Write broadcast");

        // shift bc msg start at 1.
        String message = "b " + (msg_number+1) + "\n";
        try {
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void print_state(){
        System.out.println("print state");
        for (int i = 0; i < number_processes; i++) {
            //roulettes[i].print_state();
            System.out.println(roulettes[i].getMin());

        }

    }

    /*
    public static void main (String[] args) {

        int self_idea = 3;
        Messager messager = new Messager(3, self_idea, 20);

        int[] array = {1,2,3,4,7,8,9,0};
        Message m = new Message(2, 'A', self_idea, self_idea, array);
        Message m2 = new Message(1, 'A', self_idea, self_idea, array);
        messager.receive(m);
        messager.receive(m2);

        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<Message> messages = messager.getMessages(list);

        for (int i = 0; i < 3; i++) {
            messager.roulettes[i].print_state();
        }

        for (Message m3: messages){
            System.out.println(m3);
        }
    }
     */
}
