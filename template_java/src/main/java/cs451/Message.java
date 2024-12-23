package cs451;

import java.util.*;
import java.util.stream.Collectors;



public class Message {
    private final int shotId; // Indicates which agreement number it is
    private final int idDestination; // Max 7 bytes
    private final int idSender; // Max 7 bytes
    private final int attemptNumber; // Unique for a split message
    private final Set<Integer> proposalValues; // Proposal values
    private final int partNumber; // Part number of the split message
    private final int totalParts; // Total parts for the split message
    private final char type;

    // Constructor
    private Message(int shotId, int idDestination, int idSender, int attemptNumber, Set<Integer> proposalValues, int partNumber, int totalParts, char type) {
        this.shotId = shotId;
        this.idDestination = idDestination;
        this.idSender = idSender;
        this.attemptNumber = attemptNumber;
        this.proposalValues = proposalValues;
        this.partNumber = partNumber;
        this.totalParts = totalParts;
        this.type = type;
    }

    // Updated messageProposition to include shotId
    public static List<Message> messageProposition(int shotId, int idDestination, int idSender, int attemptNumber, Set<Integer> proposalValues, char type) {
        int maxPayloadSize = 500 - 32 - 14 - 50; // 500 bytes minus header size
        List<Message> messages = new ArrayList<>();
        List<Integer> proposalList = new ArrayList<>(proposalValues);



        // Assuming each integer takes 16 bytes
        int totalParts = (int) Math.ceil((double) proposalList.size() * 16 / maxPayloadSize);
        totalParts = totalParts <= 0 ? 1 : totalParts; // Ensure at least one part

        for (int i = 0; i < totalParts; i++) {
            Set<Integer> partProposal = new HashSet<>();
            int start = i * maxPayloadSize / 16;
            int end = Math.min(proposalList.size(), (i + 1) * maxPayloadSize / 16);
            for (int j = start; j < end; j++) {
                partProposal.add(proposalList.get(j));
            }
            Message newMessage = new Message(shotId, idDestination, idSender, attemptNumber, partProposal, i + 1, totalParts, type);
            if (newMessage.toContent().getBytes().length > 500){
                System.err.println("ERROR Message too big");
                throw new RuntimeException();
            }else{
                messages.add(newMessage);
            }
        }
        return messages;
    }

    // Answer message "NO" with missing values
    public List<Message> answerMessageNo(List<Integer> missingValues) {
        Set<Integer> missingSet = new HashSet<>(missingValues);
        List<Message> messages = messageProposition(this.shotId, idSender, idDestination, attemptNumber, missingSet, 'B');
        for (Message m: messages){
            if (!Message.fromString(m.toContent()).equals(m)){
                System.err.println("Answer no wrong");
                throw new RuntimeException();
            }
        }
        return messages;
    }

    // Answer message "YES"
    public Message answerMessageYes() {
        Message answer = new Message(shotId, idSender, idDestination, attemptNumber, proposalValues, partNumber, totalParts, 'C');
        if (!Message.fromString(answer.toContent()).equals(answer)){
            System.err.println("Answer yes wrong");
            throw new RuntimeException();
        }
        return answer;
    }

    // Serialize Message to content string
    public String toContent() {
        return shotId + "|" + idDestination + "|" + idSender + "|" + attemptNumber + "|" +
                partNumber + "|" + totalParts + "|" + type + "|" +
                proposalValues.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    // Convert Message to String (for display purposes)
    @Override
    public String toString() {
        return idSender + " -> " + idDestination +
                ", Shot ID: " + shotId +
                ", Attempt: " + attemptNumber +
                ", Part: " + partNumber + "/" + totalParts +
                ", Type: " + type +
                ", Values: " + proposalValues.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    // Convert String to Message
    public static Message fromString(String str) {
        String[] parts = str.split("\\|");
        if (parts.length < 7) {
            System.err.println("message " + str);
            throw new IllegalArgumentException("Invalid message format");
        }
        int shotId = Integer.parseInt(parts[0]);
        int idDestination = Integer.parseInt(parts[1]);
        int idSender = Integer.parseInt(parts[2]);
        int attemptNumber = Integer.parseInt(parts[3]);
        int partNumber = Integer.parseInt(parts[4]);
        int totalParts = Integer.parseInt(parts[5]);
        char type = parts[6].charAt(0);
        Set<Integer> proposalValues = new HashSet<>();
        if (parts.length > 7 && !parts[7].isEmpty()) {
            for (String val : parts[7].split(",")) {
                if(val.isBlank()) break;
                proposalValues.add(Integer.parseInt(val.trim()));
            }
        }
        return new Message(shotId, idDestination, idSender, attemptNumber, proposalValues, partNumber, totalParts, type);
    }

    // Getters
    public int getShotId() {
        return shotId;
    }

    public int getIdDestination() {
        return idDestination;
    }

    public int getIdSender() {
        return idSender;
    }

    public int getAttemptNumber() {
        return attemptNumber;
    }

    public Set<Integer> getProposalValues() {
        return proposalValues;
    }

    public int getPartNumber() {
        return partNumber;
    }

    public int getTotalParts() {
        return totalParts;
    }

    public char getType() {
        return type;
    }

    // Updated equals to exclude proposalValues
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;
        return shotId == message.shotId &&
                idDestination == message.idDestination &&
                idSender == message.idSender &&
                attemptNumber == message.attemptNumber &&
                partNumber == message.partNumber &&
                totalParts == message.totalParts &&
                type == message.type;
    }

    // Updated hashCode to exclude proposalValues
    @Override
    public int hashCode() {
        return Objects.hash(
                shotId,
                idDestination,
                idSender,
                attemptNumber,
                partNumber,
                totalParts,
                type
        );
    }

    /**
     * Joins multiple messages into one. Assumes all messages have the same shotId, idDestination,
     * idSender, attemptNumber, and type. It combines their proposalValues and recalculates
     * partNumber and totalParts accordingly.
     *
     * @param messages List of messages to join.
     * @return A single joined message.
     * @throws IllegalArgumentException if messages have differing fields.
     */
    public static Message joinMessages(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("Message list is null or empty");
        }

        // Validate that all messages have the same shotId, idDestination, idSender, attemptNumber, and type
        Message first = messages.get(0);
        for (Message msg : messages) {
            if (msg.shotId != first.shotId ||
                    msg.idDestination != first.idDestination ||
                    msg.idSender != first.idSender ||
                    msg.attemptNumber != first.attemptNumber ||
                    msg.type != first.type) {
                throw new IllegalArgumentException("Messages have differing fields and cannot be joined");
            }
        }

        // Combine all proposalValues
        Set<Integer> combinedProposalValues = new HashSet<>();
        for (Message msg : messages) {
            combinedProposalValues.addAll(msg.proposalValues);
        }

        // Recalculate partNumber and totalParts
        int maxPayloadSize = 500 - 32 - 14; // 500 bytes minus header size
        int totalParts = (int) Math.ceil((double) combinedProposalValues.size() * 4 / maxPayloadSize);
        totalParts = totalParts == 0 ? 1 : totalParts; // Ensure at least one part

        // For simplicity, we'll create a single part message. Adjust if multiple parts are needed.
        // Here, we'll assume that after joining, the combined message fits into one part.

        Message message = new Message(
                first.shotId,
                first.idDestination,
                first.idSender,
                first.attemptNumber,
                combinedProposalValues,
                1,
                totalParts,
                first.type);

        if (!Message.fromString(message.toContent()).equals(message)){
            System.err.println("Answer yes wrong");
            throw new RuntimeException();
        }

        return message;
    }

    public static void main(String[] args) throws Exception{
        test1();
        test2();
        test3();
    }

    private static void test1(){
        // Example usage and tests

        // Create a proposal with 1000 integers
        Set<Integer> proposal = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            proposal.add(i);
        }

        // Create messages with shotId = 123
        List<Message> messages = Message.messageProposition(123, 1001, 2002, 1, proposal, 'A');

        // Display all messages
        for (Message msg : messages) {
            System.out.println(msg);
        }

        // Serialize the first message to content string
        String serialized = messages.get(0).toContent();
        System.out.println("Serialized: " + serialized);

        // Deserialize the string back to a Message object
        Message deserialized = Message.fromString(serialized);
        System.out.println("Deserialized: " + deserialized);

        // Create a "YES" response
        Message yesResponse = deserialized.answerMessageYes();
        System.out.println("Yes Response: " + yesResponse);

        // Validate idDestination and idSender swap
        if (messages.get(0).idDestination != yesResponse.idSender) {
            System.out.println("ERROR 1: idDestination does not match yesResponse.idSender");
        }

        if (messages.get(0).idSender != yesResponse.idDestination) {
            System.out.println("ERROR 2: idSender does not match yesResponse.idDestination");
        }

        // Validate equals (should not include proposalValues)
        Message anotherDeserialized = Message.fromString(serialized);
        if (!messages.get(0).equals(anotherDeserialized)) {
            System.out.println("ERROR 3: Messages should be equal excluding proposalValues");
        } else {
            System.out.println("Equality check passed.");
        }

        // Test joining messages
        if (messages.size() > 1) {
            List<Message> subset = messages.subList(0, 2); // Take first two messages
            Message joined = Message.joinMessages(subset);
            System.out.println("Joined Message: " + joined);

            // Verify that the joined message contains all proposal values from the subset
            Set<Integer> expectedValues = new HashSet<>();
            for (Message msg : subset) {
                expectedValues.addAll(msg.getProposalValues());
            }
            if (joined.getProposalValues().equals(expectedValues)) {
                System.out.println("Join Messages: Success");
            } else {
                System.out.println("Join Messages: Failed");
            }
        }
    }

    private static void test2() throws Exception{
        Set<Integer> set = new HashSet<>(Arrays.asList(1, 2, 3, 4, 14));
        Message message = new Message(0, 1, 2, 3, set, 4, 5, 'A');

        String content = message.toContent();
        Message reconstitution = Message.fromString(content);

        a(message.equals(reconstitution));
    }

    private static void test3() throws Exception{
        Set<Integer> set = new HashSet<>(Arrays.asList(1, 2, 3, 4, 14));
        Message message = Message.messageProposition(0, 1, 2, 3, set, 'A').get(0);

        String content = message.toContent();
        Message reconstitution = Message.fromString(content);

        a(message.equals(reconstitution));

        Message answerYes = message.answerMessageYes();
        Message reconstitutionYes = Message.fromString(answerYes.toContent());

        a(answerYes.equals(reconstitutionYes));

    }
    public static void a(boolean shouldBeTrue) throws Exception {
        if (!shouldBeTrue) throw new Exception("Assert");
    }
}
