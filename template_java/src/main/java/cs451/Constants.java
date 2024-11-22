package cs451;

public class Constants {
    public static final int ARG_LIMIT_CONFIG = 7;

    // indexes for id
    public static final int ID_KEY = 0;
    public static final int ID_VALUE = 1;

    // indexes for hosts
    public static final int HOSTS_KEY = 2;
    public static final int HOSTS_VALUE = 3;

    // indexes for output
    public static final int OUTPUT_KEY = 4;
    public static final int OUTPUT_VALUE = 5;

    // indexes for config
    public static final int CONFIG_VALUE = 6;

    //
    public static final int MAX_MESSAGE_PER_PACKET = 8;

    // Time out in ms
    public static final int MAX_TIME_OUT_MS = 20;

    public static final String SEPARATOR = ",";
    public static final char SEPARATOR_C = ',';

    public static final char NO_CHAR = ' ';

    public static final int MAX_SIZE_MESSAGE = 128;

    public static final int BATCH_SIZE = 8;

    public static final int INITIAL_PING_TIME_MS = 5_000;

    public static final long START_SHIFT_TIME_MILLIS = 3_000L;
}
