package cs451;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cs451.Constants.MAX_MESSAGE_PER_PACKET;

public class Roulette {
    private int min_value;
    private final int max_value;
    private final int peer_id;
    private final int batch_size;
    private final int self_id;
    
    private final byte[] states;

    public static final byte SEND = 0;
    public static final byte SENT = 1;
    public static final byte TO_CONFIRM = 2;
    public static final byte CONFIRMED = 3;
    
    public Roulette(int min_value, int max_value, int peer_id, int batch_size, int self_id){
        this.min_value = min_value; // Included
        this.max_value = max_value; // Not included
        this.peer_id = peer_id;
        this.batch_size = batch_size;
        this.self_id = self_id;

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
                //System.out.println("roulette min value " + min_value);
            }


        }
    }

    public void add_messages(List<Message> messageList, int original_sender){
        int size_before = messageList.size();
        add_messages_type(messageList, 'a', (byte) 0, original_sender);
        add_messages_type(messageList, 'b', (byte) 2, original_sender);
        int size_after = messageList.size();
        //TODO add ping when no more message
    }



    private void add_messages_type(List<Message> messageList, char type_message, byte state, int original_sender){
        int i = 0;

        int max = Math.min(batch_size, max_value - min_value);

        while(i < max) {
            int[] message_number = new int[8];

            int j = 0;
            while (j < MAX_MESSAGE_PER_PACKET && i < max) {
                int index = (min_value + i) % batch_size;
                while (states[index] != state && i < max){
                    i++;
                    index = (min_value + i) % batch_size;
                }
                if (i >= max) break;

                message_number[j] = min_value + i;

                i++;
                j++;
            }

            if (j == 0) return;
            int[] copyArray = Arrays.copyOf(message_number, j);

            Message message = new Message(peer_id, type_message, self_id, original_sender, copyArray);
            messageList.add(message);
        }
    }

    public byte getState(int msg_number){
        assert 0 <= msg_number;
        assert msg_number < max_value;
        assert msg_number < min_value + batch_size;

        if (msg_number < min_value) return CONFIRMED; // 3
        if (min_value + batch_size <= msg_number) return SEND;// 0

        return states[msg_number % batch_size];
    }

    public void print_state(){
        System.out.print(min_value + " ");
        for (int i = 0; i < batch_size && i < max_value; i++) {
            int state = states[(min_value + i) %batch_size];
            System.out.print(state + ",");
        }
        System.out.println();
    }

    public int getMin(){
        return min_value;
    }

    /*
    public static void main (String[] args) {
        //int min_value, int max_value, int process_id, int batch_size
        Roulette roulette = new Roulette(0, 12, 3, 50, 7);

        ArrayList<Message> list = new ArrayList<>();
        roulette.add_messages(list, 7);

        for (Message m : list) {
            System.out.println("id " + m.getId() + " content " + m.getContent());
        }

        System.out.println("\n\n\n --------------------------- \n\n\n");

        roulette = new Roulette(10, 47, 3, 20, 7);
        roulette.increase_value(10, (byte) 1);
        roulette.increase_value(10, (byte) 0);// Should do nothing
        roulette.increase_value(11, (byte) 2);
        roulette.increase_value(12, (byte) 3);
        roulette.increase_value(13, (byte) 3);
        roulette.increase_value(14, (byte) 3);
        roulette.increase_value(15, (byte) 3);
        roulette.increase_value(16, (byte) 3);


        roulette.add_messages(list, 7);

        for (Message m : list) {
            System.out.println("id " + m.getId() + " content " + m.getContent());
        }


        System.out.println("\n\n\n --------------------------- \n\n\n");

        roulette = new Roulette(40, 80, 3, 35, 7);
        roulette.increase_value(40, (byte) 0);
        roulette.increase_value(41, (byte) 2);
        roulette.increase_value(43, (byte) 2);
        roulette.increase_value(41, (byte) 2);
        roulette.increase_value(45, (byte) 2);
        roulette.increase_value(41, (byte) 2);
        roulette.increase_value(47, (byte) 2);
        roulette.increase_value(49, (byte) 2);

        roulette.add_messages(list, 7);
        for (Message m : list) {
            System.out.println("id " + m.getId() + " content " + m.getContent());
        }
    }
    */
}