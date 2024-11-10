package cs451;

import java.util.Arrays;
import java.util.Objects;

public class Message {

    private final int id_destination;
    private final String content;
    private final char type;
    private final int id_sender;
    private int original_id;
    private int[] message_numbers;
    private String extra;

    // Constructor
    public Message(int id_destination, String content) {
        this.id_destination = id_destination;
        this.content = content;

        String[] split = content.split(Constants.SEPARATOR);
        int number_message = split.length - 3;
        assert 0 < number_message;
        assert number_message <= Constants.MAX_MESSAGE_PER_PACKET;
        type = split[0].charAt(0);

        id_sender = Integer.parseInt(split[1]);
        try {
            original_id = Integer.parseInt(split[2]);

            message_numbers = Arrays.stream(split, 3, split.length)
                    .map(String::trim) // Trim whitespace
                    .mapToInt(Integer::parseInt) // Convert to int
                    .toArray(); // Collect as int[]
        } catch (Exception ignored){
            extra = split[3];
        }
    }

    public Message(int id_destination, char type, int id_sender, int original_id, int[] message_numbers){
        this.id_destination = id_destination;
        this.type = type;
        this.id_sender = id_sender;
        this.original_id = original_id;
        this.message_numbers = message_numbers;

        StringBuilder sb = new StringBuilder();
        sb.append(type);
        sb.append(Constants.SEPARATOR_C);
        sb.append(id_sender);
        sb.append(Constants.SEPARATOR_C);
        sb.append(original_id);
        for (int messageNumber : message_numbers) {
            sb.append(Constants.SEPARATOR_C);
            sb.append(messageNumber);
        }
        this.content = sb.toString();
    }

    // Getters
    public int getId() {
        return id_destination;
    }

    public String getContent() {
        return content;
    }

    public char getType() {
        return type;
    }

    public int getId_destination() {
        return id_destination;
    }

    public int getId_sender() {
        return id_sender;
    }

    public int getOriginal_id() {
        return original_id;
    }

    public int[] getMessage_numbers() {
        return message_numbers;
    }

    // Override equals to compare based on id and messageNumber
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id_destination == message.id_destination && content.equals(message.content);
    }

    // Override hashCode to ensure uniqueness in sets or hash-based collections
    @Override
    public int hashCode() {
        return Objects.hash(id_destination, content);
    }

    // toString method for easy debugging
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id_destination +
                ", content=" + content +
                '}';
    }
}
