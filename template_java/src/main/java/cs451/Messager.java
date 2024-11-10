package cs451;

import java.util.*;

import static cs451.Constants.BATCH_SIZE;

public class Messager {

    private final int number_processes;
    private final int self_process;
    private final Set<Message> setA;
    private final Set<Message> setB;
    private final Roulette[] roulettes;
    public Messager(int number_processes, int self_process, int number_messages){

        this.number_processes = number_processes;
        this.self_process = self_process;

        setA = new HashSet<>();
        setB = new HashSet<>();

        roulettes = new Roulette[number_processes];
        for (int i = 0; i < number_processes; i++) {
            roulettes[i] = new Roulette(0, number_messages, i, BATCH_SIZE, self_process);
        }
    }

    public void receive(Message message){
        switch (message.getType()){
            case 'A':
                setA.add(message);
                for (int message_number : message.getMessage_numbers()){
                    roulettes[message.getId()].increase_value(message_number, Roulette.SENT);
                    System.out.println("message recu A");
                }
                break;
            case 'B':
                setB.add(message);
                for (int message_number : message.getMessage_numbers()){
                    roulettes[message.getId()].increase_value(message_number, Roulette.CONFIRMED);
                }
                break;
            default:
                System.err.println("type " + message.getType());
        }

    }

    public ArrayList<Message> getMessages(List<Integer> manage_id){
        ArrayList<Message> messageList = new ArrayList<>();

        for (int i = 0; i < number_processes; i++) {
            roulettes[i].add_messages(messageList);
        }

        if (messageList.isEmpty()){
            String message = "c" +
                    Constants.SEPARATOR_C +
                    self_process +
                    Constants.SEPARATOR_C +
                    self_process +
                    Constants.SEPARATOR_C +
                    "PING";
            for (int i = 0; i < number_processes; i++) {
                messageList.add(new Message(i, message));
            }
        }

        return messageList;
    }

    public static void main (String[] args) {

        //TODO debug Messager

        Messager messager = new Messager(10, 3, 20);

        ArrayList<Message> messages = messager.getMessages(new ArrayList<>());
        for(Message m: messages){
            //System.out.println(m);
        }

        String content = "A,5,5,1,2,3,4,7,8,9,10";
        for (int i = 0; i < 20; i++) {
            Message m = new Message(3, content);
            messager.receive(m);
        }

        content = "B,7,7,1,2,3,4,7,8,9,10";
        for (int i = 0; i < 20; i++) {
            Message m = new Message(3, content);
            messager.receive(m);
        }

        for (int i = 0; i < 10; i++) {
            System.out.println("i " + i);
            messager.roulettes[i].print_state();
        }

        messages = messager.getMessages(new ArrayList<>());

        for(Message m: messages){
            System.out.println(m);
        }

    }
}
