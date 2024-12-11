package cs451;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class MyWriter {

    private final Writer writer;
    private final Set<MessageObject> messageGot;

    public MyWriter(String fileName){
        try {
            writer = new FileWriter(fileName);
        } catch (IOException e) {
            System.err.println("Messager, filename incorrect " + e);
            throw new RuntimeException(e);
        }

        messageGot = new HashSet<>();
    }

    // Got a new message, mo.id == -1 if broadcast
    public void newDeliverMessage(int shot, Set<Integer> decision){
        MessageObject mo = new MessageObject(shot, decision);
        boolean isNew = messageGot.add(mo);

        if (isNew){
            if (mo.getShot() == 0){
                writeDeliver(mo);
            }else {
                MessageObject previous_message = new MessageObject(shot-1, Set.of());
                if (messageGot.contains(previous_message)){
                    writeDeliver(mo);
                }
            }
        }
    }

    private void writeDeliver(MessageObject mo){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mo.getDecision().size(); i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(mo.getDecision().iterator().next());
        }


        String message = sb.toString();
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
