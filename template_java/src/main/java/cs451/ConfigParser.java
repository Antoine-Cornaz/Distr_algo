package cs451;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class ConfigParser {

    private String path;
    private int number_message;
    private int index_receive;

    public boolean populate(String value) {
        File file = new File(value);
        path = file.getPath();
        readFile();
        return true;
    }

    public String getPath() {
        return path;
    }

    public int getNumberMessage(){
        return number_message;
    }

    public int getIndexReceive(){
        return index_receive;
    }

    private void readFile() {
        //System.out.println("ConfigParser: read file");
        try {
            File myObj = new File(getPath());
            Scanner myReader = new Scanner(myObj);
            String data = myReader.nextLine();
            //System.out.println("data " + data);
            String[] data_split = data.split(" ");
            number_message = Integer.parseInt(data_split[0]);
            //index_receive = Integer.parseInt(data_split[1]);
            //System.out.println("number message " + number_message);
            //System.out.println("index receive " + index_receive);

            myReader.close();
        } catch (FileNotFoundException e) {
            System.err.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
