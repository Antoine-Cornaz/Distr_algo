package cs451;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

public class MyWriter {

    private final Writer writer;
    private final Set<MessageObject> messageGot;
    private final Set<MessageObject> messageDelivered;

    public MyWriter(String fileName){
        try {
            writer = new FileWriter(fileName);
        } catch (IOException e) {
            System.err.println("Messager, filename incorrect " + e);
            throw new RuntimeException(e);
        }

        messageGot = new HashSet<>();
        messageDelivered = new HashSet<>();
    }

    // Got a new message, mo.id == -1 if broadcast
    public void newDeliverMessage(MessageObject mo){
        boolean isNew = messageGot.add(mo);

        if (isNew){
            if (mo.getMessage_number() == 0){
                deliver(mo);
            }else {
                MessageObject previous_message = new MessageObject(mo.getId_sender(), mo.getMessage_number() - 1);
                if (messageDelivered.contains(previous_message)){
                    deliver(mo);
                }
            }
        }
    }



    private void deliver(MessageObject mo){
        if(mo.getId_sender() == -1){
            // If id is -1, it's a broadcast message
            writeBroadCast(mo.getMessage_number());
        }else{
            writeDeliver(mo);
        }

        messageDelivered.add(mo);


        MessageObject next_message = new MessageObject(mo.getId_sender(), mo.getMessage_number() + 1);
        if (messageGot.contains(next_message)){
            deliver(next_message);
        }
    }

    public void writeBroadCast(int msg_number){
        //System.out.println("Write broadcast");

        // shift bc msg start at 1.
        String message = "b " + (msg_number+1) + "\n";
        try {
            writer.write(message);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private void writeDeliver(MessageObject mo){
        //System.out.println("write deliver sender: " + sender + " msg: " + msg_number);

        // shift bc msg and sender start at 1.
        String message = "d " + (mo.getId_sender()+1) + " " + (mo.getMessage_number()+1) + "\n";
        try {
            writer.write(message);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void close(){
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
