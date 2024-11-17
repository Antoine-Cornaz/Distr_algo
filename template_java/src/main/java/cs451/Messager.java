package cs451;

import java.util.*;

import static cs451.Constants.BATCH_SIZE;

public class Messager {

    private final int number_processes;
    private final int self_process;
    private final Set<Message> setA;
    private final Set<Message> setB;
    private final Roulette[] roulettes;
    private final Substitute[] substitutes;
    private final MyWriter myWriter;
    public Messager(int number_processes, int self_process, int number_messages, String fileName){

        this.number_processes = number_processes;
        this.self_process = self_process;

        setA = new HashSet<>();
        setB = new HashSet<>();

        roulettes = new Roulette[number_processes];
        substitutes = new Substitute[number_processes];
        for (int i = 0; i < number_processes; i++) {
            roulettes[i] = new Roulette(0, number_messages, i, BATCH_SIZE, self_process);
        }

        myWriter = new MyWriter(fileName);

    }

    public Message receive(Message message){
        //System.out.println("message recu " + message.getType());
        boolean isNew;
        switch (message.getType()) {
            case 'A':
                isNew = setA.add(message);
                if (isNew) {
                    for (int message_number : message.getMessage_numbers()) {
                        roulettes[message.getId_sender()].increase_value(message_number, Roulette.SENT);
                        //System.out.println("update roulette sender: " + message.getId_sender());
                        update_to_confirm_if_majority(message_number);
                    }
                }
                break;
            case 'B':
                isNew = setB.add(message);
                //System.out.println("confirmed " + message.getMessage_numbers()[0]);
                if (isNew) {
                    for (int message_number : message.getMessage_numbers()) {
                        roulettes[message.getId_sender()].increase_value(message_number, Roulette.CONFIRMED);
                        myWriter.newDeliverMessage(new MessageObject(message.getId_sender(), message_number));
                    }
                }
                break;

            case 'C':
                //TODO
                break;

            case 'D':
                //TODO
                substitutes[message.getOriginal_id()].answer(message);
                break;

            case 'E':
                // TODO
                break;

            case 'a':
                // TODO
                return message.getAnswer();

            case 'b':
                // TODO
                return message.getAnswer();

            case 'c':
                // TODO
                return message.getAnswer();

            case 'd':
                // TODO
                int max_value_sender = message.getMessage_numbers()[0];
                int original_sender = message.getOriginal_id();
                int max_value_self = roulettes[original_sender].getMinConfirmed();

                if (max_value_self <= max_value_sender){
                    System.out.println(message.getAnswerD(max_value_self));
                    return message.getAnswerD(max_value_self);
                }else {
                    int[] list_message = roulettes[original_sender].getFrom(max_value_sender);
                    System.out.println(message.getAnswerE(list_message));
                    return message.getAnswerE(list_message);
                }

            default:
                System.err.println("type " + message.getType());
        }

        return null;

    }

    public List<Message> getMessages(List<Integer> manage_id){

        ArrayList<Message> messageList = new ArrayList<>();
        for (Integer id: manage_id){
            if (substitutes[id] == null){
                substitutes[id] = new Substitute(self_process, number_processes, id, setA, roulettes[id].getMinExist());
            }

            substitutes[id].addMessages(messageList);
        }

        for (int i = 0; i < number_processes; i++) {
            roulettes[i].add_messages(messageList, self_process);
        }

        return messageList;
    }

    public void killSubstitute(List<Integer> list_id){
        for (Integer id: list_id){
            substitutes[id] = null;
        }
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
            myWriter.newDeliverMessage(new MessageObject(-1, msg_number));
            for (int i = 0; i < number_processes; i++) {
                roulettes[i].increase_value(msg_number, Roulette.TO_CONFIRM);
            }
        }
    }

    public void close(){
        myWriter.close();
        //print_state();
    }


    public void print_state(){
        System.out.println("print state");
        for (int i = 0; i < number_processes; i++) {
            roulettes[i].print_state();
            //System.out.println(roulettes[i].getMin());
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
