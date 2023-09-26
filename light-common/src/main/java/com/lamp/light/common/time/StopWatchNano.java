package com.lamp.light.common.time;

public class StopWatchNano extends StopWatch{

    @Override
    public void start() {
        start = System.nanoTime();
    }
    public static StopWatchNano createStart(){
        StopWatchNano c = new StopWatchNano();
        c.start();
        return c;
    }
    @Override
    public long end() {
        if (start == 0){
            throw new RuntimeException("StopWatch has not been started");
        }
        duration = System.nanoTime() - start;
        return duration;
    }
    public void endOutToConsole () {
        System.out.println(end());
    }
}
