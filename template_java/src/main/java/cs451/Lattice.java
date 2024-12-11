package cs451;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lattice class handles the distributed agreement logic by managing proposals and responses.
 */
public class Lattice {

    private boolean end = false; // Flag to indicate the end of the agreement process.
    private final int numberProcesses; // Total number of processes involved.
    private boolean isOk = true; // Tracks if the current state of agreement is valid.
    private int numberAnswer = 0; // Counter for the number of answers received in the current round.
    private int runNumber = 0; // The current run or attempt number for agreement.
    private final Set<Integer> proposal; // The set of proposed values.
    private final Set<Integer> newProposition; // New values proposed by other processes.
    private final Lock lock; // Lock for ensuring thread safety.
    private final MyWriter myWriter; // Writer to log or output results.
    private final int shot; // Identifier for the current round of agreement.

    /**
     * Constructor to initialize the Lattice instance.
     * @param proposal Initial set of proposals.
     * @param numberProcesses Total number of processes.
     * @param myWriter Writer instance for logging.
     * @param shot Identifier for the current agreement process.
     */
    public Lattice(Set<Integer> proposal, int numberProcesses, MyWriter myWriter, int shot) {
        this.proposal = proposal;
        this.numberProcesses = numberProcesses;
        this.newProposition = new HashSet<>();
        this.myWriter = myWriter;
        this.shot = shot;
        lock = new ReentrantLock();
    }

    /**
     * Gets the latest proposition.
     * @return The current proposition encapsulated in a Proposition object.
     */
    public Proposition getLatestProposition() {
        lock.lock();
        try {
            return new Proposition(proposal, runNumber);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Handles the receipt of an answer message.
     * @param message The received message containing proposals.
     * @return True if the agreement process is successfully completed, otherwise false.
     */
    public boolean receive(Message message) {

        switch (message.getType()) {
            case 'A':
                receiveA(message);
                break;

            case 'B':
                receiveB(message);
                break;

            case 'C':
                receiveC(message);
                break;
            case default:
                System.err.println("message type weird " + message.getType());
        }


    }

    private boolean receiveA(Message answer) {
        // TODO
        return false;
    }

    private boolean receiveB(Message answer) {
        // TODO
        lock.lock();
        isOk = false;
        try {
            for (Integer i : answer.getProposalValues()) {
                newProposition.add(i);

            }

            if (answer.getAttemptNumber() == runNumber) {
                numberAnswer++;
            }

            boolean majority = numberAnswer > numberProcesses / 2;
            if (majority && isOk && !end) {
                end = true;
                myWriter.newDeliverMessage(shot, proposal);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Handles the receipt of a question message and formulates an appropriate response.
     * @param question The received question message.
     * @return A list of response messages.
     */
    private List<Message> receiveC(Message question) {
        lock.lock();
        try {
            List<Message> answer = new ArrayList<>();
            if (proposal.containsAll(question.getProposalValues())) {
                // If the proposal is a subset of the question's proposal values.
                answer.add(question.answerMessageYes());
            } else {
                // If the proposal is not a subset of the question's proposal values.
                Set<Integer> difference = new HashSet<>(proposal);
                difference.removeAll(question.getProposalValues());
                List<Integer> differenceList = new ArrayList<>(difference);
                answer = question.answerMessageNo(differenceList);
            }
            return answer;
        } finally {
            lock.unlock();
        }
    }
}
