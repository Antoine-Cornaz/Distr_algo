package cs451;

import java.util.*;
import java.util.stream.Collectors;

public class Message {
    private final int idDestination; // Max 7 bytes
    private final int idSender; // Max 7 bytes
    private final int attemptNumber; // Unique for a split message
    private final Set<Integer> proposalValues; // Proposal values
    private final int partNumber; // Part number of the split message
    private final int totalParts; // Total parts for the split message
    private final char type;

    // Constructor
    private Message(int idDestination, int idSender, int attemptNumber, Set<Integer> proposalValues, int partNumber, int totalParts, char type) {
        this.idDestination = idDestination;
        this.idSender = idSender;
        this.attemptNumber = attemptNumber;
        this.proposalValues = proposalValues;
        this.partNumber = partNumber;
        this.totalParts = totalParts;
        this.type = type;
    }

    // To split the message into multiple parts if the proposalValues are too large
    public static List<Message> messageProposition(int idDestination, int idSender, int attemptNumber, Set<Integer> proposalValues, char type) {
        int maxPayloadSize = 500 - 32 - 14; // 500 bytes minus header size
        List<Message> messages = new ArrayList<>();
        List<Integer> proposalList = new ArrayList<>(proposalValues);

        int totalParts = (int) Math.ceil((double) proposalList.size() * 4 / maxPayloadSize);
        for (int i = 0; i < totalParts; i++) {
            Set<Integer> partProposal = new HashSet<>();
            for (int j = i * maxPayloadSize / 4; j < Math.min(proposalList.size(), (i + 1) * maxPayloadSize / 4); j++) {
                partProposal.add(proposalList.get(j));
            }
            messages.add(new Message(idDestination, idSender, attemptNumber, partProposal, i + 1, totalParts, type));
        }
        return messages;
    }

    // Answer message "YES"
    public Message answerMessageYes() {
        return new Message(idSender, idDestination, attemptNumber, proposalValues, partNumber, totalParts, 'B');
    }

    // Answer message "NO" with missing values
    public List<Message> answerMessageNo(List<Integer> missingValues) {
        Set<Integer> missingSet = new HashSet<>(missingValues);
        return messageProposition(idSender, idDestination, attemptNumber, missingSet, 'c');
    }

    // Convert Message to String
    @Override
    public String toString() {
        return idDestination + "|" + idSender + "|" + attemptNumber + "|" + partNumber + "|" + totalParts + "|" +
                proposalValues.stream().map(String::valueOf).collect(Collectors.joining(","));

    }

    // Convert String to Message
    public static Message fromString(String str) {
        String[] parts = str.split("\\|");
        int idDestination = Integer.parseInt(parts[0]);
        int idSender = Integer.parseInt(parts[1]);
        int attemptNumber = Integer.parseInt(parts[2]);
        int partNumber = Integer.parseInt(parts[3]);
        int totalParts = Integer.parseInt(parts[4]);
        Set<Integer> proposalValues = new HashSet<>();
        if (parts.length > 5) {
            for (String val : parts[5].split(",")) {
                proposalValues.add(Integer.parseInt(val));
            }
        }
        return new Message(idDestination, idSender, attemptNumber, proposalValues, partNumber, totalParts, 'C');
    }

    // Getters
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;


        Message message1 = (Message) o;
        return idDestination == message1.idDestination &&
                idSender == message1.idSender &&
                proposalValues.equals(message1.proposalValues) &&
                attemptNumber == message1.attemptNumber &&
                partNumber == message1.partNumber &&
                totalParts == message1.totalParts &&
                type == message1.type;

    }

    /*

    private final int idDestination; // Max 7 bytes
    private final int idSender; // Max 7 bytes
    private final int attemptNumber; // Unique for a split message
    private final Set<Integer> proposalValues; // Proposal values
    private final int partNumber; // Part number of the split message
    private final int totalParts; // Total parts for the split message
     */

    public static void main(String[] args) {
        Set<Integer> proposal = new HashSet<>(Set.of(1));
        for (int i = 0; i < 1000; i++) {
            proposal.add(i);
        }
        List<Message> messages = Message.messageProposition(1001, 2002, 1, proposal, 'A');

        for (Message msg : messages) {
            System.out.println(msg);
        }

        String serialized = messages.get(0).toString();
        System.out.println("Serialized:   " + serialized);

        Message deserialized = Message.fromString(serialized);
        System.out.println("Deserialized: " + deserialized);

        Message yesResponse = deserialized.answerMessageYes();
        System.out.println("Yes Response: " + yesResponse);


        if(messages.get(0).idDestination != yesResponse.idSender){
            System.out.println("ERROR 1");
        }

        if(messages.get(0).idSender != yesResponse.idDestination){
            System.out.println("ERROR 2");
        }

        if(messages.get(0).idSender != yesResponse.idDestination){
            System.out.println("ERROR 3");
        }


        if(!messages.get(0).equals(deserialized)){
            System.out.println("ERROR 4");
        }
    }

}
