package com.lamp.light.common.time;

/**
 * @author lambert
 */
public class StopWatchMillis extends StopWatch{
    public StopWatchMillis() {

    }

    public void start (){
        start = System.currentTimeMillis();
    }
    public static StopWatchMillis createStart(){
        StopWatchMillis c = new StopWatchMillis();
        c.start();
        return c;
    }
    public long end() {
        if (start == 0){
            throw new RuntimeException("StopWatch has not been started");
        }

        duration = System.currentTimeMillis() - start;
        return duration;
    }


    public void endOutToConsole () {
        System.out.println(end());
    }

}
