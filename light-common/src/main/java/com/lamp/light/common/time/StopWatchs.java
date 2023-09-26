package com.lamp.light.common.time;

import java.util.ArrayList;

/**
 * @author lambert
 */
public abstract class StopWatchs {

    public abstract void start();

    public abstract void checkpoint();
    public abstract void end();
    public ArrayList<Long> getTimepoints() {
        return timepoints;
    }
    public ArrayList<Long> getTimepointsDiff() {
        ArrayList<Long> diff = new ArrayList<Long>();
        for(int i = 0 ; i < timepoints.size() - 1 ; i++){
            diff.add(timepoints.get(i+1) - timepoints.get(i));
        }
        return diff;
    }
    public void printTimepoints(){
        timepoints.forEach(System.out::println);
    }
    public void printTimepointsDiff(){
        for(int i = 0 ; i < timepoints.size() - 1 ; i++){
            System.out.print(i+1+":\t");
            System.out.print(timepoints.get(i+1) - timepoints.get(i));
            System.out.print('\n');
        }
        System.out.flush();
    }

    public void endOutToConsole() {
        this.end();
        this.printTimepointsDiff();
    }

    protected ArrayList<Long> timepoints = new ArrayList<Long>();
}
