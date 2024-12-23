package cs451;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static cs451.Message.messageProposition;

public class Manager {

    private Scanner scanner = null;
    private MyWriter myWriter = null;

    private final int numberLattices;
    private final int numberProcesses;
    private final int maxProposition;
    private final int distincElement;
    private final Lattice[] lattices;
    private final int idSelf;
    public Manager(String config_file_path, String outputfileName, int idSelf, int numberProcesses){
        File myObj = new File(config_file_path);
        this.numberProcesses = numberProcesses;
        try {
             scanner = new Scanner(myObj);
             myWriter = new MyWriter(outputfileName);
        } catch (FileNotFoundException e) {
            System.err.println("file not found " + config_file_path + " " + e);
        }

        String[] firstLine = scanner.nextLine().split(" ");
        numberLattices = Integer.parseInt(firstLine[0]);
        maxProposition = Integer.parseInt(firstLine[1]);
        //System.out.println("number process " + numberProcesses);
        distincElement = Integer.parseInt(firstLine[2]);
        lattices = new Lattice[numberLattices];
        for (int i = 0; i < numberLattices; i++) {
            String proposal = scanner.nextLine();
            lattices[i] = new Lattice(stringToSetInteger(proposal), numberProcesses, myWriter, i);
        }

        this.idSelf = idSelf;
    }

    public List<Message> getMessages(){

        List<Message> messages = new ArrayList<>();

        for (int i = 0; i < numberLattices; i++) {
            Proposition proposition = lattices[i].getLatestProposition();
            if (proposition == null) continue;
            boolean[] listAccepted = lattices[i].getPeerAccepted();

            for (int j = 0; j < numberProcesses; j++) {
                if(!listAccepted[j]){
                    messages.addAll(messageProposition(i, j, idSelf, proposition.getAttemptNumber(), proposition.getProposal(), 'A'));
                }
            }
            if (messages.size() > 100) break;
        }


        return messages;
    }

    public List<Message> receive(Message message){
        //System.out.println("Manager " + message);
        if (! (0 <=message.getShotId() && message.getShotId() < numberLattices)){
            System.err.println("Receive message with shot id outside possible ids");
        }
        return lattices[message.getShotId()].receive(message);
    }


    private Set<Integer> stringToSetInteger(String line){
        String[] values = line.split(" ");
        Set<Integer> set = new HashSet<>();

        for (String s: values){
            set.add(Integer.parseInt(s));
        }

        return set;
    }

    public void close(){
        myWriter.close();
    }
}
