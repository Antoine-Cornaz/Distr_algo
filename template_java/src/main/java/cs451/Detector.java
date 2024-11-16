package cs451;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Detector {

    private final int number_process;
    private final int self_process;
    private long ping_time_ms;

    private final Lock lock;
    private final long[] last_message_time;
    private final boolean[] was_dead;

    private List<Integer> new_dead;
    private List<Integer> new_resurect;

    public Detector(int number_process, int self_process, long ping_time_ms){

        assert 0 < number_process;
        assert 0 <= self_process;
        assert self_process < number_process;

        assert 0 < ping_time_ms;

        this.number_process = number_process;
        this.self_process = self_process;
        this.ping_time_ms = ping_time_ms;

        lock = new ReentrantLock();

        last_message_time = new long[number_process];

        // Initialize the delay 2 seconds after start so we don't have everything down after 1 sec.
        long START_SHIFT_TIME_MILLIS = 0L;
        Arrays.fill(last_message_time, System.currentTimeMillis() + START_SHIFT_TIME_MILLIS);

        was_dead = new boolean[number_process];
    }

    public void update(int process_number){
        lock.lock();
        last_message_time[process_number] = System.currentTimeMillis();
        //System.out.print("u" +process_number+",");
        lock.unlock();
    }


    public void update_time(){

        new_dead = new LinkedList<>();
        new_resurect = new LinkedList<>();
        long timeNow = System.currentTimeMillis();

        long next_ping_time = ping_time_ms;

        lock.lock();
        for (int i = 0; i < number_process; i++) {
            // Self process never die
            if (i == self_process) continue;
            boolean is_dead = timeNow - last_message_time[i] > ping_time_ms;

            if (is_dead && !was_dead[i]) {
                new_dead.add(i);
                was_dead[i] = true;
            } else if (!is_dead && was_dead[i]) {
                new_resurect.add(i);
                was_dead[i] = false;

                // Double ping time delay, when some dead come back to life.
                next_ping_time = 2 * ping_time_ms;
            }
        }

        ping_time_ms = next_ping_time;

        lock.unlock();
    }

    public List<Integer> get_new_dead(){
        return new_dead;
    }

    public List<Integer> get_new_resurect(){
        return new_resurect;
    }


    public static void main (String[] args) throws InterruptedException {
        //int min_value, int max_value, int process_id, int batch_size

        Detector detector = new Detector(10, 3, 100);

        detector.printNews();// Nothing

        System.out.println("Sleep 1 second");
        TimeUnit.SECONDS.sleep(1);

        System.out.println("update 7");
        detector.update(7);

        // all dead except 7
        detector.printNews();

        TimeUnit.SECONDS.sleep(1);

        System.out.println("update 5");
        detector.update(5);


        detector.printNews();

        TimeUnit.SECONDS.sleep(1);

        for (int i = 0; i < 10; i++) {
            System.out.println("update 2");
            detector.update(2);
            System.out.println("wait 1 sec");
            TimeUnit.SECONDS.sleep(1);
            detector.printNews();
        }
    }

    private void printNews(){
        System.out.println("printNews");
        update_time();


        for (Integer d : get_new_dead()) {
            System.out.println("dead :" + d);
        }


        for (Integer d : get_new_resurect()) {
            System.out.println("resurect :" + d);
        }
        System.out.println();
    }

}
