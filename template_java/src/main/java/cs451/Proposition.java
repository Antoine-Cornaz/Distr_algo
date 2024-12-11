package cs451;
import java.util.Set;
import java.util.Objects;

public class Proposition {
    private final Set<Integer> proposal;
    private final int attemptNumber;

    // Constructor
    public Proposition(Set<Integer> proposal, int attemptNumber) {
        this.proposal = proposal;
        this.attemptNumber = attemptNumber;
    }

    // Getter for proposal
    public Set<Integer> getProposal() {
        return proposal;
    }

    // Getter for attemptNumber
    public int getAttemptNumber() {
        return attemptNumber;
    }

    // toString method for readability
    @Override
    public String toString() {
        return "Proposal{" +
                "proposal=" + proposal +
                ", attemptNumber=" + attemptNumber +
                '}';
    }

    // Equals method to compare Proposal objects
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proposition proposition1 = (Proposition) o;
        return attemptNumber == proposition1.attemptNumber &&
                Objects.equals(proposal, proposition1.proposal);
    }

    // HashCode method for use in hash-based collections
    @Override
    public int hashCode() {
        return Objects.hash(proposal, attemptNumber);
    }
}

