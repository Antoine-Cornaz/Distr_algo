package cs451;

import java.util.*;

public class MessageReceiver {
    private int numberMessageReceived = 0;
    private static class MessagePart {
        int totalParts;
        Map<Integer, Message> receivedParts;


        public MessagePart(int totalParts) {
            this.totalParts = totalParts;
            this.receivedParts = new HashMap<>();
        }

        public void addPart(Message message) {
            receivedParts.put(message.getPartNumber(), message);
        }

        public boolean isComplete() {
            return receivedParts.size() == totalParts;
        }

        public Message assembleCompleteMessage() {
            if (!isComplete()) {
                throw new IllegalStateException("Message parts are not complete yet.");
            }
            return Message.joinMessages(new ArrayList<>(receivedParts.values()));
        }
    }

    private final Map<String, MessagePart> messagesInProgress;

    public MessageReceiver() {
        this.messagesInProgress = new HashMap<>();
    }

    /**
     * Receives a message part. If the message is complete after adding this part,
     * it returns the complete message. Otherwise, it returns null.
     *
     * @param message The incoming message part.
     * @return The complete message if all parts are received; otherwise null.
     */
    public Message receive(Message message) {
        numberMessageReceived++;
        if (message.getTotalParts() == 1) return message;
        String messageKey = generateMessageKey(message);

        // Retrieve or initialize the MessagePart entry
        MessagePart messagePart = messagesInProgress.computeIfAbsent(messageKey,
                key -> new MessagePart(message.getTotalParts()));

        // Add the part to the message
        messagePart.addPart(message);

        Message completeMessage = null;

        // Check if the message is complete
        if (messagePart.isComplete()) {
            // Assemble the complete message
            completeMessage = messagePart.assembleCompleteMessage();

            // Remove from progress map as it's completed
            messagesInProgress.remove(messageKey);
            //System.out.println(message.getTotalParts() + " parts, Complete message " + completeMessage);
        }

        if (messagesInProgress.size() > 10_000){
            messagesInProgress.clear();
            System.out.println("Clear cache MessageReceiver");
        }

        return completeMessage;
    }

    /**
     * Generates a unique key for a message based on its shotId and sender.
     *
     * @param message The message to generate a key for.
     * @return A string key representing the message.
     */
    private String generateMessageKey(Message message) {
        return message.getShotId() + "-" + message.getIdSender() + "-" + message.getAttemptNumber() + "-" + message.getType();
        //int shotId, int idDestination, int idSender, int attemptNumber, Set<Integer> proposalValues, int partNumber, int totalParts, char type
    }

    /*
    public static void main(String[] args) {
        MessageReceiver receiver = new MessageReceiver();

        // Simulating message parts from the same sender with the same shotId
        Set<Integer> valuesPart1 = new HashSet<>(Arrays.asList(1, 2));
        Set<Integer> valuesPart2 = new HashSet<>(Arrays.asList(3, 4));
        Set<Integer> valuesPart3 = new HashSet<>(Arrays.asList(5, 6));

        Message part1 = new Message(123, 1001, 2002, 1, valuesPart1, 1, 3, 'A');
        Message part2 = new Message(123, 1001, 2002, 1, valuesPart2, 2, 3, 'A');
        Message part3 = new Message(123, 1001, 2002, 1, valuesPart3, 3, 3, 'A');

        System.out.println(receiver.receive(part1)); // Output: null
        System.out.println(receiver.receive(part2)); // Output: null
        System.out.println(receiver.receive(part3)); // Output: Complete message


        System.out.println(receiver.receive(part3)); // Output: null
        System.out.println(receiver.receive(part2)); // Output: null
        System.out.println(receiver.receive(part1)); // Output: Complete message
    }
     */
}
