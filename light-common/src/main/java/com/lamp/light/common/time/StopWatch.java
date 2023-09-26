package com.lamp.light.common.time;

/**
 * @author lambert
 */
public abstract class StopWatch {

    public abstract void start();

    public abstract long end();
    public long duration() {
        if (duration == 0) {
            throw new RuntimeException("StopWatch has not been ended");
        }
        return duration;
    }
    public void endOutToConsole() {
        System.out.println(end());
    }

    protected long duration = 0;
    protected long start =0;
}