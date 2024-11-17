package cs451;

import java.util.List;
import java.util.Set;

import static cs451.Constants.BATCH_SIZE;

public class Substitute {

    private final int self_id;
    private final int number_processes;
    private final int substitute_id;
    private final Set<Message> messages;
    private final int max_value;

    private final Roulette[] roulettes;
    private final int[] max_values_other;
    private int max_max_values_other;


    public Substitute(int self_id, int number_processes, int substitute_id, Set<Message> messages, int max_value){
        this.self_id = self_id;
        this.number_processes = number_processes;
        this.substitute_id = substitute_id;
        this.messages = messages;
        this.max_value = max_value;
        this.roulettes = new Roulette[number_processes];
        this.max_values_other = new int[number_processes];
        this.max_max_values_other = -1;
        for (int i = 0; i < number_processes; i++) {
            this.max_values_other[i]=-1;
        }
    }


    public void addMessages(List<Message> messages){
        for (int i = 0; i < number_processes; i++) {
            if (roulettes[i] == null){
                // Ask for basic message

                Message message = new Message(i, 'd', self_id, substitute_id, new int[]{max_value});
                messages.add(message);
            }else {
                roulettes[i].add_messages(messages, substitute_id);
            }
        }
    }

    public void answer(Message message){
        //assert message.getType() == 'D' || message.getType() == 'E';
        assert message.getOriginal_id() == substitute_id;
        // TODO

        switch (message.getType()){
            case 'D':
                int max_value = message.getMessage_numbers()[0];
                int id = message.getId_sender();
                this.max_values_other[id] = max_value;
                if(max_max_values_other < max_value){
                    max_max_values_other = max_value;

                    //Update others
                    for (int i = 0; i < number_processes; i++) {
                        if(roulettes[i] == null) continue;
                        roulettes[i].setMax_value(max_value);
                    }
                }

                roulettes[id] = new Roulette(max_value, max_max_values_other, id, BATCH_SIZE, self_id);
                break;
            case 'E':
                break;
        }

    }
}
