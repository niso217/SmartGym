package com.a.n.smartgym.Quary;

import java.util.List;

/**
 * Created by nirb on 20/04/2017.
 */

public class MachineUsage {

    private String machine_name;
    private int counter;
    private long present;

    public String getMachine_name() {
        return machine_name;
    }

    public long getPresent() {
        return present;
    }

    public void setPresent(long present) {
        this.present = present;
    }

    public void setMachine_name(String machine_name) {
        this.machine_name = machine_name;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    private int CalcSum(List<MachineUsage> list) {
        int sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i).counter;
        }
        return sum;
    }

    public List<MachineUsage> CalcPercentage(List<MachineUsage> list) {

        int sum = CalcSum(list);

        for (int i = 0; i < list.size(); i++) {
            list.get(i).setPresent(100L * list.get(i).getCounter() / sum);

        }

        return list;

    }
}
