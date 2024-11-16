package cs451;

import java.util.ArrayList;
import java.util.List;

public class Manager {

    private final int number_processes;
    private final int self_process;
    private final boolean[] processes_dead;

    public Manager(int number_processes, int self_process){
        this.number_processes = number_processes;
        this.self_process = self_process;

        processes_dead = new boolean[number_processes];
    }

    public void setAlive(List<Integer> list_processes_number){
        for (int process_number: list_processes_number){
            setAlive(process_number);
        }
    }

    public void setAlive(int process_number){
        assert process_number != self_process;
        assert process_number < number_processes;
        processes_dead[process_number] = false;
    }

    public void setDead(List<Integer> list_processes_number){
        for (Integer process_number: list_processes_number){
            setDead(process_number);
        }
    }

    public void setDead(int process_number){
        assert process_number != self_process;
        assert process_number < number_processes;
        processes_dead[process_number] = true;
    }

    public List<Integer> get_manage(){
        ArrayList<Integer> list = new ArrayList<>();

        int i = (self_process+1) % number_processes;
        while (processes_dead[i]){
            list.add(i);
            i = (i+1) % number_processes;
        }

        return list;
    }


    /*
    public static void main (String[] args) throws InterruptedException {
        Manager manager = new Manager(60, 28);

        for (Integer a : manager.get_manage()){
            System.out.println("manage " + a);
        }

        System.out.println("kill 29 to 52");
        for (int i = 28; i < 53; i++) {
            manager.setDead(i);
        }

        for (Integer a : manager.get_manage()){
            System.out.println("manage " + a);
        }

        System.out.println("resurect process 42");
        manager.setAlive(42);

        for (Integer a : manager.get_manage()){
            System.out.println("manage " + a);
        }

        System.out.println("kill all excpet self(28) and 20");


        for (int i = 0; i < 60; i++) {
            if(i == 28 || i == 20) continue;
            manager.setDead(i);
        }


        for (Integer a : manager.get_manage()){
            System.out.println("manage " + a);
        }

    }
     */

}
