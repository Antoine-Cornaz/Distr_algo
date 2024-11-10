package cs451;

import java.util.ArrayList;
import java.util.List;

import static cs451.Constants.MAX_MESSAGE_PER_PACKET;

public class Roulette {
    private int min_value;
    private final int max_value;
    private final int process_id;
    private final int batch_size;
    
    private final byte[] states;

    public final byte SEND = 0;
    public final byte SENT = 1;
    public final byte TO_CONFIRM = 2;
    public final byte CONFIRMED = 3;
    
    public Roulette(int min_value, int max_value, int process_id, int batch_size){
        this.min_value = min_value; // Included
        this.max_value = max_value; // Not included
        this.process_id = process_id;
        this.batch_size = batch_size;

        this.states = new byte[batch_size];
    }

    public void increase_value(int msg_number, byte state){

        assert SEND <= state;
        assert state <= CONFIRMED;

        assert 0 <= msg_number;
        assert msg_number < max_value;
        assert msg_number < min_value + batch_size;

        if (msg_number < min_value) return;

        if (this.states[msg_number%batch_size] < state){
            this.states[msg_number%batch_size] = state;

            while (this.states[min_value%batch_size] >= CONFIRMED){

                this.states[min_value%batch_size] = SEND;

                min_value++;
            }


        }
    }

    public void add_messages(List<Message> messageList){
        add_messages_type(messageList, 'a', (byte) 0);
        add_messages_type(messageList, 'b', (byte) 2);
    }



    private void add_messages_type(List<Message> messageList, char type_message, byte state){
        int i = 0;

        int max = Math.min(batch_size, max_value - min_value);

        while(i < max) {

            StringBuilder sb = new StringBuilder();
            sb.append(type_message);
            sb.append(Constants.SEPARATOR_C);
            sb.append(process_id);
            sb.append(Constants.SEPARATOR_C);
            sb.append(process_id);

            int j = 0;
            while (j < MAX_MESSAGE_PER_PACKET && i < max) {
                int index = (min_value + i) % batch_size;
                while (states[index] != state && i < max){
                    i++;
                    index = (min_value + i) % batch_size;
                }
                if (i >= max) break;
                sb.append(Constants.SEPARATOR_C);
                sb.append(min_value + i);

                i++;
                j++;
            }

            if (j == 0) return;

            String message_content = sb.toString();

            Message message = new Message(process_id, message_content);
            messageList.add(message);
        }
    }

    /*
    public static void main (String[] args) {
        //int min_value, int max_value, int process_id, int batch_size
        Roulette roulette = new Roulette(0, 12, 3, 50);

        ArrayList<Message> list = new ArrayList<>();
        roulette.add_messages(list);

        for (Message m : list) {
            System.out.println("id " + m.getId() + " content " + m.getContent());
        }

        System.out.println("\n\n\n --------------------------- \n\n\n");

        roulette = new Roulette(10, 47, 3, 20);
        roulette.increase_value(10, (byte) 1);
        roulette.increase_value(10, (byte) 0);// Should do nothing
        roulette.increase_value(11, (byte) 2);
        roulette.increase_value(12, (byte) 3);
        roulette.increase_value(13, (byte) 3);
        roulette.increase_value(14, (byte) 3);
        roulette.increase_value(15, (byte) 3);
        roulette.increase_value(16, (byte) 3);


        roulette.add_messages(list);

        for (Message m : list) {
            System.out.println("id " + m.getId() + " content " + m.getContent());
        }


        System.out.println("\n\n\n --------------------------- \n\n\n");

        roulette = new Roulette(40, 80, 3, 35);
        roulette.increase_value(40, (byte) 0);
        roulette.increase_value(41, (byte) 2);
        roulette.increase_value(43, (byte) 2);
        roulette.increase_value(41, (byte) 2);
        roulette.increase_value(45, (byte) 2);
        roulette.increase_value(41, (byte) 2);
        roulette.increase_value(47, (byte) 2);
        roulette.increase_value(49, (byte) 2);

        roulette.add_messages(list);
        for (Message m : list) {
            System.out.println("id " + m.getId() + " content " + m.getContent());
        }
    }
     */
}