package cs451;

import java.util.Objects;

public class IdMessage {

    private final int id;
    private final int messageNumber;

    // Constructor
    public IdMessage(int id, int messageNumber) {
        this.id = id;
        this.messageNumber = messageNumber;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    // Override equals to compare based on id and messageNumber
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdMessage message = (IdMessage) o;
        return id == message.id && messageNumber == message.messageNumber;
    }

    // Override hashCode to ensure uniqueness in sets or hash-based collections
    @Override
    public int hashCode() {
        return Objects.hash(id, messageNumber);
    }

    // toString method for easy debugging
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", messageNumber=" + messageNumber +
                '}';
    }
}
