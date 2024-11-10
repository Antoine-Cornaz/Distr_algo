package cs451;

import java.util.Objects;

public class Message {

    private final int id;
    private final String content;

    // Constructor
    public Message(int id_destination, String content) {
        this.id = id_destination;
        this.content = content;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    // Override equals to compare based on id and messageNumber
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id == message.id && content.equals(message.content);
    }

    // Override hashCode to ensure uniqueness in sets or hash-based collections
    @Override
    public int hashCode() {
        return Objects.hash(id, content);
    }

    // toString method for easy debugging
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content=" + content +
                '}';
    }
}
