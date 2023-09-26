package com.lamp.light.common.time;

public class StopWatchsMillis extends StopWatchs{
    @Override
    public void start() {
        timepoints.add(System.currentTimeMillis());
    }

    @Override
    public void checkpoint() {
        timepoints.add(System.currentTimeMillis());
    }

    @Override
    public void end() {
        timepoints.add(System.currentTimeMillis());
    }

    public static StopWatchMillis creatStart (){
        StopWatchMillis c = new StopWatchMillis();
        c.start();
        return c;
    }

}
