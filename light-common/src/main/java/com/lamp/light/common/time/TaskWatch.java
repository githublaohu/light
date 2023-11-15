package com.lamp.light.common.time;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 一个用来计算任务耗时的工具类
 */
public class TaskWatch {
    private final Method method;
    private final LinkedHashMap<String, StopWatch> watchMap;
    /**
     * @param method Millis / Nano 表示使用毫秒 / 纳秒; Access / Insert 表示使用访问顺序 / 插入顺序
     */
    public TaskWatch(Method method) {
        this.method = method;
        if (method == Method.MillisAccess || method == Method.NanoAccess) {
            watchMap = new LinkedHashMap<>(16, 0.75f, true);
        } else {
            watchMap = new LinkedHashMap<>();
        }

    }

    /**
     * If the taskName already exists, the previous taskName will be overwritten
     *
     * @param taskName 任务名
     * @return 开始时间
     */
    public long startTask(String taskName) {
        StopWatch newWatch;
        if (method == Method.NanoAccess || method == Method.NanoInsert) {
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

    /**
     * @return 所有任务的开始时间,结束时间,耗时 的字符串
     */
    public String allTask() {
        StringBuilder sb = new StringBuilder();
        sb.append(method == Method.NanoAccess || method == Method.NanoInsert ? "nano time\t---\t" : "millis time\t---\t");
        sb.append(method == Method.NanoAccess || method == Method.MillisAccess ? "access order\n" : "insert order\n");
        for (Map.Entry<String, StopWatch> entry : watchMap.entrySet()) {
            sb.append(entry.getKey());
            sb.append(":\t");
            sb.append(entry.getValue().Info());
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * 打印所有任务的开始时间,结束时间,耗时
     */
    public void printAllTask() {
        System.out.println(allTask());
    }

    /**
     * @return 忽略所有未结束的任务,返回所有任务的总耗时
     */
    public long allTaskDuration() {
        long duration = 0;
        for (StopWatch watch : watchMap.values()) {
            duration += watch.getDuration();
        }
        return duration;
    }

    /**
     *
     * @return 所有任务的总耗时和完成情况
     */
    public String overview() {
        StringBuilder sb = new StringBuilder();

        final int[] finished = {0};
        final long[] duration = {0};
        watchMap.forEach((k, v) -> {
            if (v.getEnd() != 0) {
                finished[0]++;
                duration[0] += v.getDuration();
            }
        });
        sb.append("finished task: ").append(finished[0]).append('/').append(watchMap.size()).append('\n');
        sb.append("total duration: ").append(duration[0]).append(method == Method.NanoAccess || method == Method.NanoInsert ? " ns\n" : " ms\n");
        return sb.toString();
    }

    /**
     * 打印所有任务的总耗时和完成情况
     */
    public void printOverview() {
        System.out.println(overview());
    }

    /**
     * 计时时间单位和获取时使用的顺序,Insert使用插入计时器的顺序,Access使用最后操作计时器的顺序
     */
    public enum Method {
        MillisAccess, MillisInsert, NanoAccess, NanoInsert
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
