package cs451;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class MyWriter {

    private final Writer writer;
    private final Map<Integer, Set<Integer>> messageGot;
    private int messageToWrite = 0;

    public MyWriter(String fileName){
        try {
            writer = new FileWriter(fileName);
        } catch (IOException e) {
            System.err.println("MyWriter, filename incorrect: " + e);
            throw new RuntimeException(e);
        }

        messageGot = new HashMap<>();
    }

    public synchronized void newDeliverMessage(int shot, Set<Integer> decision){
        if (shot < messageToWrite){
            System.err.println("ERROR writing value 2 times");
        }
        messageGot.put(shot, decision);
        while (messageGot.containsKey(messageToWrite)){
            writeDeliver();
        }
    }

    private void writeDeliver(){
        Set<Integer> decision = messageGot.get(messageToWrite);
        if (decision == null) {
            System.err.println("No decision found for shot: " + messageToWrite);
            return;
        }

        StringBuilder sb = new StringBuilder();
        Iterator<Integer> it = decision.iterator();
        for (int i = 0; i < decision.size(); i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(it.next());
        }
        sb.append("\n");

        String message = sb.toString();
        try {
            //System.out.println("Writing " + messageToWrite);
            writer.write(message);
            writer.flush(); // Ensure data is written to disk
            //System.out.println("Written shot " + messageToWrite + ": " + message.trim());
        } catch (IOException e) {
            System.err.println("Failed to write message for shot " + messageToWrite + ": " + e.getMessage());
        }

        messageGot.remove(messageToWrite);
        messageToWrite++;
    }

    public synchronized void close(){
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.err.println("Failed to close MyWriter: " + e.getMessage());
        }
    }
}
