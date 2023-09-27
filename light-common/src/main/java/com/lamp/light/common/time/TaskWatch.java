package com.lamp.light.common.time;

import java.util.LinkedHashMap;
import java.util.Map;

public class TaskWatch {

    private boolean useNano;
    private LinkedHashMap<String, StopWatch> watchMap;
//    private ReadWriteLock lock;


    public TaskWatch(boolean useNano) {
        this.useNano = useNano;
        watchMap = new LinkedHashMap<>();
    }

    /**
     * If the taskName already exists, the previous taskName will be overwritten
     * @param taskName
     * @return
     */
    public long startTask(String taskName) {
        StopWatch newWatch;
        if (useNano) {
            newWatch = new NanoStopWatch();
        } else {
            newWatch = new MillisStopWatch();
        }
        watchMap.put(taskName, newWatch);
        return newWatch.start();
    }

    public long endTask(String taskName) {
        StopWatch watch = watchMap.get(taskName);
        if ((watch == null) || watch.getEnd() != 0) {
            return -1;
        }
        return watch.end();
    }
    public String allTaskOrderByStart(){
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, StopWatch> entry : watchMap.entrySet()) {
            sb.append(entry.getKey());
            sb.append(":\t");
            sb.append(entry.getValue().Info());
            sb.append('\n');
        }
        return sb.toString();
    }
    public void clear(){
        watchMap.clear();
    }

    public void printAllTaskOrderByStart(){
        System.out.println(allTaskOrderByStart());
    }



    private abstract class StopWatch {
        private long start;
        private long end;

        public long start() {
            start = time();
            return start;
        }

        public long end(){
            end = time();
            return end;
        }

        public abstract long time();


        public long getDuration() {
            return getEnd() - getStart();
        }

        public long getStart(){
            return start;
        }

        public long getEnd(){
            return end;
        }

        public String Info() {
            StringBuilder sb = new StringBuilder();
            if (getStart() == 0) {
                sb.append("0");
                return sb.toString();
            }
            sb.append(this.getStart());
            sb.append(" --");
            if (getEnd() == 0) {
                sb.append("--> ");
                return sb.toString();
            }
            sb.append(getEnd() - getStart());
            sb.append("--> ");
            sb.append(getEnd());
            return sb.toString();
        }


    }

    private class MillisStopWatch extends StopWatch {
        @Override
        public long time() {
            return System.currentTimeMillis();
        }

    }
    private class NanoStopWatch extends StopWatch {
        @Override
        public long time() {
            return System.nanoTime();
        }
    }
}
