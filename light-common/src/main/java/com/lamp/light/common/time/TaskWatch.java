package com.lamp.light.common.time;

import java.util.LinkedHashMap;
import java.util.Map;

public class TaskWatch {

    private final boolean useNano;
    private final boolean accessOrder;
    private LinkedHashMap<String, StopWatch> watchMap;

    /**
     * @param useNano     true: use nanoTime, false: use currentTimeMillis
     * @param accessOrder true: access order, false: insert order
     */
    public TaskWatch(boolean useNano, boolean accessOrder) {
        this.useNano = useNano;
        this.accessOrder = accessOrder;
        watchMap = accessOrder ? new LinkedHashMap<>(16, 0.75f, true) : new LinkedHashMap<String, StopWatch>();

    }

    /**
     * If the taskName already exists, the previous taskName will be overwritten
     *
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

    public void clear() {
        watchMap.clear();
    }

    public String allTask() {
        StringBuilder sb = new StringBuilder();
        sb.append(useNano ? "nano time\t---\t" : "millis time\t---\t");
        sb.append(accessOrder ? "access order\n" : "insert order\n");
        for (Map.Entry<String, StopWatch> entry : watchMap.entrySet()) {
            sb.append(entry.getKey());
            sb.append(":\t");
            sb.append(entry.getValue().Info());
            sb.append('\n');
        }
        return sb.toString();
    }
    public void printAllTask() {
        System.out.println(allTask());
    }

    /**
     *
     * @return neglect the task that has not ended
     */
    public long allTaskDuration() {
        long duration = 0;
        for (StopWatch watch : watchMap.values()) {
            duration += watch.getDuration();
        }
        return duration;
    }

    public String overview() {
        StringBuilder sb = new StringBuilder();

        final int[] finished = {0};
        final long[] duration = {0};
        watchMap.forEach((k, v) -> {
            if(v.getEnd()!=0) {
                finished[0]++;
                duration[0] += v.getDuration();
            }
        });
        sb.append("finished task: ").append(finished[0]).append('/').append(watchMap.size()).append('\n');
        sb.append("total duration: ").append(duration[0]).append(useNano ? " ns\n" : " ms\n");
        return sb.toString();
    }
    public void printOverview() {
        System.out.println(overview());
    }




    private abstract class StopWatch {
        private long start;
        private long end;

        public long start() {
            start = time();
            return start;
        }

        public long end() {
            end = time();
            return end;
        }

        public abstract long time();


        public long getDuration() {
            return getEnd() - getStart();
        }

        public long getStart() {
            return start;
        }

        public long getEnd() {
            return end;
        }

        public String Info() {
            StringBuilder sb = new StringBuilder();
            if (getStart() == 0) {
                return sb.append("0").toString();
            }
            sb.append(this.getStart()).append(" --");
            if (getEnd() == 0) {
                return sb.append("--> ").toString();
            }
            return sb.append(getDuration()).append("--> ").append(getEnd()).toString();
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
