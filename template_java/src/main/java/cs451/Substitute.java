package cs451;

import java.util.List;
import java.util.Set;

import static cs451.Constants.BATCH_SIZE;

public class Substitute {

    private final int self_id;
    private final int number_processes;
    private final int substitute_id;
    private final Set<IdMessage> messages;

    private final Roulette[] roulettes;
    private final int[] min_value_other;
    private int max_value_principal_sent;


    public Substitute(int self_id, int number_processes, int substitute_id, Set<IdMessage> messages){
        this.self_id = self_id;
        this.number_processes = number_processes;
        this.substitute_id = substitute_id;
        this.messages = messages;
        this.roulettes = new Roulette[number_processes];
        this.min_value_other = new int[number_processes]; // not include
        this.max_value_principal_sent = 0;
        for (int i = 0; i < number_processes; i++) {
            this.min_value_other[i]=0;
        }
    }


    public void addMessages(List<Message> messages){
        for (int i = 0; i < number_processes; i++) {

            int number_message_before = messages.size();
            if (roulettes[i] != null){
                //System.out.println("not null roulettes " + i);
                // Send basic message from other source
                int length_before = messages.size();
                roulettes[i].add_messages(messages, substitute_id);
                int length_after = messages.size();
                for (int j = length_before; j < length_after; j++) {
                    Message message = messages.get(j);
                    /*if (message.getType() != 'c' && message.getType() != 'C') {
                        System.out.println("substitute " + message);
                    }*/
                }
            }
            int number_message_after = messages.size();
            int number_message_added = number_message_after - number_message_before;

            if (number_message_added == 0){
                Message message = new Message(i, 'd', self_id, substitute_id, new int[]{max_value_principal_sent});
                messages.add(message);
            }
        }
    }

    public void receive_message(Message message){
        //assert message.getType() == 'D' || message.getType() == 'E';
        assert message.getOriginal_id() == substitute_id;

        switch (message.getType()){
            case 'A':
                for (int message_number : message.getMessage_numbers()) {
                    roulettes[message.getId_sender()].increase_value(message_number, Roulette.RECEIVED);
                    //System.out.println("Substitute increase " + message_number + " to  " + Roulette.RECEIVED);
                    update_to_confirm_if_majority(message_number);
                }
                break;

            case 'B':
                for (int message_number : message.getMessage_numbers()) {
                    roulettes[message.getId_sender()].increase_value(message_number, Roulette.MAJORITY_CONFIRMED);
                    //System.out.println("Substitute increase " + message_number + " to  " + Roulette.MAJORITY_CONFIRMED);
                }
                break;

            case 'C':
                break;


            case 'D':
                //System.out.println("D got " + message);
                int min_value = message.getMessage_numbers()[0];
                int id = message.getId_sender();
                this.min_value_other[id] = min_value;
                //System.out.println("Substitute get message D from sender " + id + " min value " + min_value);
                roulettes[id] = new Roulette(min_value, max_value_principal_sent, id, BATCH_SIZE, self_id);
                new_member_confirm_majority(min_value);
                break;
            case 'E':
                for (int number_message : message.getMessage_numbers()){
                    messages.add(new IdMessage(message.getId_sender(), number_message));
                }

                //System.out.println("Substitute get message E");
                boolean increased = false;
                for (Integer integer: message.getMessage_numbers()){
                    if(integer == max_value_principal_sent){
                        max_value_principal_sent++;
                        increased = true;
                    }
                }

                if (increased){
                    // max_value increase
                    for (int i = 0; i < number_processes; i++) {
                        if (roulettes[i] == null) continue;

                        // Increase all roulette to send new messages.
                        roulettes[i].setMax_value(max_value_principal_sent+1);
                    }
                    //System.out.println("max value increased to " + (max_value_principal_sent+1));
                }

                break;


            default:
                System.err.println("substitute get error type");
        }

    }

    private void new_member_confirm_majority(int min){
        int minEveryone = min;
        for (int i = 0; i < number_processes; i++) {
            if (roulettes[i] == null) continue;

            int min_current = roulettes[i].getMin();
            //System.out.println(i + " Substitute min this  " + min_current);
            if(min_current < minEveryone){
                minEveryone = min_current;
            }
        }

        //System.out.println("Substitute min new process " + min + " min_everyone " + minEveryone);
        for (int i = minEveryone; i < max_value_principal_sent; i++) {
            update_to_confirm_if_majority(i);
        }
    }

    private void update_to_confirm_if_majority (int msg_number){
        //System.out.println("update_to_confirm_if_majority");
        int counter_receive = 0;

        for (int i = 0; i < number_processes; i++) {
            if (roulettes[i] == null) continue;
            byte state = roulettes[i].getState(msg_number);
            if (state == Roulette.RECEIVED) counter_receive++;

            // If already confirmation stage doesn't need to update
            if (Roulette.MAJORITY <= state) {
                //System.out.println("Someone already in confirmation stage");
                setMajority(msg_number);// TODO change only check if new member
                break;
            }
        }

        //System.out.println("number got " + counter_receive + " / " + number_processes);
        //System.out.println("counter_receive " + counter_receive);
        if (counter_receive > number_processes/2){
            // 50% < received
            // if majority received
            //System.out.println("new majority");
            setMajority(msg_number);
        }
    }

    private void setMajority(int msg_number) {
        //System.out.println("majority in substitute " + self_id + " msg_n " + msg_number);
        for (int i = 0; i < number_processes; i++) {
            if(roulettes[i]!= null) {
                roulettes[i].increase_value(msg_number, Roulette.MAJORITY);
            }
        }
    }
}
