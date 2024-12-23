package cs451;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lattice class handles the distributed agreement logic by managing proposals and responses.
 */
public class Lattice {

    private boolean end = false; // Indicates the end of the agreement process.
    private final int numberProcesses; // Total number of processes involved.
    private int receivedYes = 0; // Counter for answers received in the current round.
    private int currentRun = 0; // Current round or attempt number.

    private final Set<Integer> proposals; // Set of proposed values.
    private final Set<Message> receivedMessages; // Tracks received messages to avoid duplicates.

    private final Lock lock; // Lock for ensuring thread safety.
    private final MyWriter myWriter; // Writer to log results.
    private final int roundId; // Identifier for the current round of agreement.
    private final MessageReceiver messageReceiver;
    private final boolean[] peerAccepted;


    /**
     * Constructor to initialize the Lattice instance.
     *
     * @param proposals      Initial set of proposals.
     * @param numberProcesses Total number of processes.
     * @param myWriter       Writer instance for logging results.
     * @param agreementId        Identifier for the agreement process round.
     */
    public Lattice(Set<Integer> proposals, int numberProcesses, MyWriter myWriter, int agreementId) {
        this.proposals = proposals;
        this.numberProcesses = numberProcesses;
        this.receivedMessages = new HashSet<>();
        this.myWriter = myWriter;
        this.roundId = agreementId;
        this.lock = new ReentrantLock();
        this.messageReceiver = new MessageReceiver();
        peerAccepted = new boolean[numberProcesses];
    }

    /**
     * Returns the current proposition.
     *
     * @return The current proposition encapsulated in a Proposition object.
     */
    public Proposition getLatestProposition() {

        if(end){
            return null;
        }
        lock.lock();
        Proposition proposition = new Proposition(new HashSet<>(proposals), currentRun);
        lock.unlock();
        return proposition;

    }

    public boolean[] getPeerAccepted() {
        return peerAccepted;
    }

    /**
     * Processes a received message.
     *
     * @param message The message to process.
     * @return A list of messages as responses or null.
     */
    public List<Message> receive(Message message) {

        if (message.getShotId() != roundId){
            System.err.println("Error received Message wrong lattice");
        }

        // Avoid processing duplicate messages.
        if (!receivedMessages.add(message)){
            return new ArrayList<>();
        }

        // Join messages
        message = messageReceiver.receive(message);
        if (message == null) {
            return new ArrayList<>();
        }

        switch (message.getType()) {
            case 'A':
                return handleQuestionMessage(message);
            case 'B':
                handleNoMessage(message);
                return new ArrayList<>();
            case 'C':
                handleYesMessage(message);
                return new ArrayList<>();
            default:
                System.err.println("Unknown message type: " + message.getType());
                return new ArrayList<>();
        }
    }

    /**
     * Handles a question ('A') message.
     *
     * @param question The received question message.
     * @return A list of response messages.
     */
    private List<Message> handleQuestionMessage(Message question) {
        List<Message> messages = new ArrayList<>(1);
        lock.lock();
        if (question.getProposalValues().containsAll(proposals)) {
            messages.add(question.answerMessageYes());
        } else {
            List<Integer> missingProposals = new ArrayList<>(proposals);
            missingProposals.removeAll(question.getProposalValues());
            messages.addAll(question.answerMessageNo(missingProposals));
        }
        startNewRun(question.getProposalValues());
        lock.unlock();
        return messages;
    }

    /**
     * Handles a NO ('B') message.
     *
     * @param message The received NO message.
     */
    private void handleNoMessage(Message message) {

        if (end) return;

        lock.lock();
        System.out.println("No");

        Set<Integer> newValues = message.getProposalValues();
        newValues.removeAll(proposals);

        if (!newValues.isEmpty()) {
            startNewRun(message.getProposalValues());
        }

        lock.unlock();
    }

    /**
     * Handles a YES ('C') message.
     *
     * @param message The received YES message.
     */
    private void handleYesMessage(Message message) {

        lock.lock();
        if (end || message.getAttemptNumber() != currentRun) {
            lock.unlock();
            return;
        }
        System.out.println("Yes");

        peerAccepted[message.getIdSender()] = true;
        receivedYes++;

        if (isMajorityReceived()) {
            finalizeAgreement();
        }
        lock.unlock();
    }

    /**
     * Checks if a majority of responses have been received.
     *
     * @return True if majority is reached, otherwise false.
     */
    private boolean isMajorityReceived() {
        return receivedYes > numberProcesses / 2;
    }

    /**
     * Starts a new agreement run by updating the proposal set.
     */
    private void startNewRun(Set<Integer> newValues) {
        // Already in lock

        if (proposals.containsAll(newValues)) return;

        currentRun++;
        receivedYes = 0;
        proposals.addAll(newValues);

        System.out.println(proposals.size() + " new run" + proposals);

        Arrays.fill(peerAccepted, false);
    }

    /**
     * Finalizes the agreement process by delivering the result.
     */
    private void finalizeAgreement() {
        // In a lock
        //System.out.println("agreement : " + roundId + " " + Arrays.toString(peerAccepted));
        end = true;
        myWriter.newDeliverMessage(roundId, new HashSet<>(proposals));
    }
}
