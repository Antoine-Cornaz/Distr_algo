package cs451;

import java.util.List;

public class Parser {

    private String[] args;
    private long pid;
    private IdParser idParser;
    private HostsParser hostsParser;
    private OutputParser outputParser;
    private ConfigParser configParser;

    public Parser(String[] args) {
        this.args = args;
    }

    public void parse() {
        pid = ProcessHandle.current().pid();

        idParser = new IdParser();
        hostsParser = new HostsParser();
        outputParser = new OutputParser();
        configParser = new ConfigParser();

        int argsNum = args.length;
        if (argsNum != Constants.ARG_LIMIT_CONFIG) {
            //System.out.println("B");
            help();
        }

        if (!idParser.populate(args[Constants.ID_KEY], args[Constants.ID_VALUE])) {
            //System.out.println("C");
            help();
        }

        if (!hostsParser.populate(args[Constants.HOSTS_KEY], args[Constants.HOSTS_VALUE])) {
            //System.out.println("D");
            help();
        }

        if (!hostsParser.inRange(idParser.getId())) {
            //System.out.println("E");
            help();
        }

        if (!outputParser.populate(args[Constants.OUTPUT_KEY], args[Constants.OUTPUT_VALUE])) {
            //System.out.println("F");
            help();
        }

        //System.out.println(args[Constants.CONFIG_VALUE]);
        if (!configParser.populate(args[Constants.CONFIG_VALUE])) {
            //System.out.println("G");
            help();
        }
    }

    private void help() {
        System.err.println("Usage: ./run.sh --id ID --hosts HOSTS --output OUTPUT CONFIG");
        System.exit(1);
    }

    public int myId() {
        return idParser.getId();
    }

    public List<Host> hosts() {
        return hostsParser.getHosts();
    }

    public String output() {
        return outputParser.getPath();
    }

    public String config() {
        return configParser.getPath();
    }

    public int getNumberMessage(){
        return configParser.getNumberMessage();
    }

    public int getIndexReceive(){
        return configParser.getIndexReceive();
    }

}
