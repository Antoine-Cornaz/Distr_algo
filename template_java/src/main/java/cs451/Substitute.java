package cs451;

import java.util.List;
import java.util.Set;

import static cs451.Constants.BATCH_SIZE;

public class Substitute {

    private final int self_id;
    private final int number_processes;
    private final int substitute_id;
    private final Set<IdMessage> messages;
    private final int max_value;

    private final Roulette[] roulettes;
    private final int[] min_value_other;
    private int max_value_min_everyone;


    public Substitute(int self_id, int number_processes, int substitute_id, Set<IdMessage> messages, int max_value){
        this.self_id = self_id;
        this.number_processes = number_processes;
        this.substitute_id = substitute_id;
        this.messages = messages;
        this.max_value = max_value;
        this.roulettes = new Roulette[number_processes];
        this.min_value_other = new int[number_processes]; // not include
        this.max_value_min_everyone = max_value;
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
                //System.out.println(i + " info roulettes " + roulettes[i].getMin() + " " + roulettes[i].getMax_value_sent());
                int length_after = messages.size();
                for (int j = length_before; j < length_after; j++) {
                    Message message = messages.get(j);
                    if (message.getType() != 'c' && message.getType() != 'C') {
                        System.out.println("substitute " + message);
                    }
                }
            }
            int number_message_after = messages.size();
            int number_message_added = number_message_after - number_message_before;

            if (number_message_added == 0){
                Message message = new Message(i, 'd', self_id, substitute_id, new int[]{max_value});
                messages.add(message);
            }
        }
    }

    public void get_message(Message message){
        //assert message.getType() == 'D' || message.getType() == 'E';
        assert message.getOriginal_id() == substitute_id;
        // TODO

        switch (message.getType()){
            case 'A':
                for (int message_number : message.getMessage_numbers()) {
                    roulettes[message.getId_sender()].increase_value(message_number, Roulette.SENT);
                    update_to_confirm_if_majority(message_number);
                }
                break;

            case 'B':
                for (int message_number : message.getMessage_numbers()) {
                    roulettes[message.getId_sender()].increase_value(message_number, Roulette.CONFIRMED);
                }
                break;


            case 'D':
                System.out.println("D got " + message);
                int min_value = message.getMessage_numbers()[0];
                int id = message.getId_sender();
                this.min_value_other[id] = min_value;
                System.out.println("Substitute get message D from sender " + id + " min value " + min_value);
                roulettes[id] = new Roulette(min_value, max_value_min_everyone, id, BATCH_SIZE, self_id);
                new_member_confirm_majority(min_value);
                break;
            case 'E':
                for (int number_message : message.getMessage_numbers()){
                    messages.add(new IdMessage(message.getId_sender(), number_message));
                }

                System.out.println("Substitute get message E");
                for (Integer integer: message.getMessage_numbers()){
                    if(integer == max_value_min_everyone + 1){
                        max_value_min_everyone++;
                    }
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

            System.out.println(i + " Substitute min this  " + roulettes[i].getMin());
            if(roulettes[i].getMin() < minEveryone){
                minEveryone = roulettes[i].getMin();
            }
        }

        System.out.println("Substitute min new process " + min + " min_everyone " + minEveryone);
        for (int i = minEveryone; i < min+1; i++) {
            update_to_confirm_if_majority(i);
        }
    }

    private void update_to_confirm_if_majority (int msg_number){
        System.out.println("update_to_confirm_if_majority");
        int counter_receive = 0;

        for (int i = 0; i < number_processes; i++) {
            if (roulettes[i] == null) continue;
            byte state = roulettes[i].getState(msg_number);
            if (state == Roulette.SENT) counter_receive++;

            // If already confirmation stage doesn't need to update
            if (state > Roulette.SENT) {
                setMajority(msg_number);// TODO change only check if new member
                //return;
            }
        }

        System.out.println("number got " + counter_receive + " / " + number_processes);
        //System.out.println("counter_receive " + counter_receive);
        if (counter_receive > number_processes/2){
            // 50% < received
            // if majority received
            setMajority(msg_number);
        }
    }

    private void setMajority(int msg_number) {
        System.out.println("majority in substitute " + self_id + " msg_n " + msg_number);
        for (int i = 0; i < number_processes; i++) {
            if(roulettes[i]!= null) {
                roulettes[i].increase_value(msg_number, Roulette.TO_CONFIRM);
            }
        }
    }


}
