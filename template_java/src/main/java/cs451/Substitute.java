package cs451;

import java.util.List;
import java.util.Set;

public class Substitute {

    private final int self_id;
    private final int number_processes;
    private final int substitute_id;
    private final Set<Message> messages;
    private final int max_value;

    private final Roulette[] roulettes;


    public Substitute(int self_id, int number_processes, int substitute_id, Set<Message> messages, int max_value){
        this.self_id = self_id;
        this.number_processes = number_processes;
        this.substitute_id = substitute_id;
        this.messages = messages;
        this.max_value = max_value;
        this.roulettes = new Roulette[number_processes];
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
}
