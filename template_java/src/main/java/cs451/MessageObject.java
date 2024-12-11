package cs451;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MessageObject {
    private final int shot;
    private final Set<Integer> decision;

    // Parameterized constructor
    public MessageObject(int shot, Set<Integer> decision) {
        this.shot = shot;
        this.decision = decision;
    }

    // Getter for 'shot'
    public int getShot() {
        return shot;
    }

    // Getter for 'decision'
    public Set<Integer> getDecision() {
        return decision;
    }

    // Equal method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageObject that = (MessageObject) o;
        return shot == that.shot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shot);
    }

    @Override
    public String toString() {
        return "MessageObject{" +
                "shot=" + shot +
                ", decision=" + decision +
                '}';
    }
}
