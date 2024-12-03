package cs451;

import java.util.*;

import static cs451.Constants.BATCH_SIZE;

public class Messager {

    private final int number_processes;
    private final int self_process;
    private final Set<IdMessage> setA;
    private final Set<IdMessage> setB;
    private final Roulette[] roulettes;
    private final Substitute[] substitutes;
    private final MyWriter myWriter;
    private int lastMessage = -1;
    public Messager(int number_processes, int self_process, int number_messages, MyWriter myWriter){

        this.number_processes = number_processes;
        this.self_process = self_process;

        setA = new HashSet<>();
        setB = new HashSet<>();

        roulettes = new Roulette[number_processes];
        substitutes = new Substitute[number_processes];
        for (int i = 0; i < number_processes; i++) {
            roulettes[i] = new Roulette(0, number_messages, i, BATCH_SIZE, self_process);
        }

        this.myWriter = myWriter;

    }

    public Message receive(Message message){
        //System.out.println("message recu " + message.getType());

        if (message.isAnswer() &&
                message.getOriginal_id() != self_process &&
                substitutes[message.getOriginal_id()] != null){

            substitutes[message.getOriginal_id()].receive_message(message);
        }


        boolean isNew=false;
        switch (message.getType()) {
            case 'A':
                for (int message_number : message.getMessage_numbers()) {
                    isNew = setA.add(new IdMessage(message.getId_sender(), message_number));
                    if (!isNew) continue;;

                    if (message.getOriginal_id() == self_process) {
                        roulettes[message.getId_sender()].increase_value(message_number, Roulette.RECEIVED);
                        //System.out.println("update roulette sender: " + message.getId_sender());
                        update_to_confirm_if_majority(message_number);
                    }
                }
                break;
            case 'B':
                //System.out.println("confirmed " + message.getMessage_numbers()[0]);

                for (int message_number : message.getMessage_numbers()) {
                    isNew = setB.add(new IdMessage(message.getId_sender(), message_number));
                    if (!isNew) continue;
                    roulettes[message.getId_sender()].increase_value(message_number, Roulette.MAJORITY_CONFIRMED);
                }

                break;

            case 'C':
                //TODO
                break;

            case 'D':
                //TODO
                //substitutes[message.getOriginal_id()].receive_message(message);
                break;

            case 'E':
                // TODO
                //substitutes[message.getOriginal_id()].receive_message(message);
                break;

            case 'a':
                // TODO
                return message.getAnswer();

            case 'b':
                // TODO
                for (int msg_number : message.getMessage_numbers()){
                    //System.out.println("write new deliver original id " + message.getOriginal_id()  + " msg number " + msg_number);
                    myWriter.newDeliverMessage(new MessageObject(message.getOriginal_id(), msg_number));
                }
                return message.getAnswer();

            case 'c':
                // TODO
                return message.getAnswer();

            case 'd':
                // TODO
                //System.out.println("got max_value " + message.getMessage_numbers()[0]);
                int max_value_sender = message.getMessage_numbers()[0];
                int original_sender = message.getOriginal_id();


                int max_value_self = maxSet(original_sender, setA);
                //System.out.println("Messager max_value_self " + max_value_self + " from sender " + original_sender);

                if (max_value_self <= max_value_sender+1){
                    //System.out.println(message.getAnswerD(max_value_self));
                    return message.getAnswerD(max_value_self);
                }else {
                    int[] list_message = roulettes[original_sender].getFrom(max_value_sender);
                    //System.out.println(message.getAnswerE(list_message));
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
                substitutes[id] = new Substitute(self_process, number_processes, id, setA, roulettes[id].getMin());
            }

            substitutes[id].addMessages(messageList);
        }

        int max_send = -1;
        for (int i = 0; i < number_processes; i++) {
            roulettes[i].add_messages(messageList, self_process);
            int current_max_send = roulettes[i].getMax_value_sent();
            if (max_send < current_max_send){
                max_send = current_max_send;
            }
        }

        if(lastMessage < max_send){
            for (int i = lastMessage; i < max_send; i++) {
                myWriter.newDeliverMessage(new MessageObject(-1, i+1));
            }
            lastMessage = max_send;
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
            if (state == Roulette.RECEIVED) counter_receive++;

            // If already confirmation stage doesn't need to update
            if (state > Roulette.RECEIVED) return;
        }

        //System.out.println("counter_receive " + counter_receive);
        if (counter_receive > number_processes/2){
            // 50% < received
            // if majority received
            for (int i = 0; i < number_processes; i++) {
                roulettes[i].increase_value(msg_number, Roulette.MAJORITY);
            }
        }
    }

    public void close(){
        myWriter.close();
        //print_state();
        printMessages();
    }


    public void print_state(){
        System.out.println("print state");
        for (int i = 0; i < number_processes; i++) {
            roulettes[i].print_state();
            //System.out.println(roulettes[i].getMin());
        }

    }

    private int maxSet(int id, Set<IdMessage> set){
        int i = 0;
        while (set.contains(new IdMessage(id, i))){
            i++;
        }

        return i;
    }

    private void printMessages(){
        for (IdMessage message: setA){
            System.out.println(message);
        }

        for (IdMessage message: setB){
            System.out.println(message);
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
