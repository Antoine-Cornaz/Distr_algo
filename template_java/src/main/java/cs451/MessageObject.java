package cs451;

import java.util.Objects;

public class MessageObject {
    private int message_number;
    private int id_sender;

    // Constructor
    // If id is -1, it's a broadcast message
    public MessageObject(int id_sender, int message_number) {
        this.message_number = message_number;
        this.id_sender = id_sender;
    }

    // Getters
    public int getMessage_number() {
        return message_number;
    }

    public int getId_sender() {
        return id_sender;
    }

    // Setters
    public void setMessage_number(int message_number) {
        this.message_number = message_number;
    }

    public void setId_sender(int id_sender) {
        this.id_sender = id_sender;
    }

    // Overriding equals()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Same object reference
        if (o == null || getClass() != o.getClass()) return false; // Null or different class

        MessageObject that = (MessageObject) o;
        return message_number == that.message_number && id_sender == that.id_sender; // Compare fields
    }

    // Overriding hashCode()
    @Override
    public int hashCode() {
        return Objects.hash(message_number, id_sender); // Generate hash code using fields
    }

    // Optional: Override toString() for better readability
    @Override
    public String toString() {
        return "Message_Object{" +
                "message=" + message_number +
                ", id=" + id_sender +
                '}';
    }
}
