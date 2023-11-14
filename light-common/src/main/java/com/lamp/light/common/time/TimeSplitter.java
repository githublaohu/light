package com.lamp.light.common.time;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

public class TimeSplitter<T> {

    private final ChronoUnit unit;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Function<LocalDateTime, T> function;
    private final TreeMap<LocalDateTime, T> timeMap = new TreeMap<>();

    public TimeSplitter(LocalDateTime start, LocalDateTime end, ChronoUnit unit, Function<LocalDateTime, T> function) {
        this.start = start;
        this.end = end;
        this.unit = unit;
        this.function = function;
    }

    public TimeSplitter(Date start, Date end, ChronoUnit unit, Function<LocalDateTime, T> function) {
        this(start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                end.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                unit,
                function);
    }

    /**
     * 创建一个时间分割器，默认不会计算时间间隔和时间段点，需要手动调用calculate方法
     * 允许传入util.Date，但是输出一定是LocalDateTime
     *
     * @param start    开始时间
     * @param end      结束时间(闭区间)
     * @param unit     时间间隔单位
     * @param function 从时间点调用的函数
     * @param <T>      函数返回值类型
     * @return 时间分割器
     */
    public static <T> TimeSplitter<T> create(LocalDateTime start, LocalDateTime end, ChronoUnit unit, Function<LocalDateTime, T> function) {
        return new TimeSplitter<>(start, end, unit, function);
    }

    /**
     * 创建一个时间分割器，默认不会计算时间间隔和时间段点，需要手动调用calculate方法
     * 允许传入util.Date，但是输出一定是LocalDateTime
     *
     * @param start    开始时间
     * @param end      结束时间(闭区间)
     * @param unit     时间间隔单位
     * @param function 从时间点调用的函数
     * @param <T>      函数返回值类型
     * @return 时间分割器
     */
    public static <T> TimeSplitter<T> create(Date start, Date end, ChronoUnit unit, Function<LocalDateTime, T> function) {
        return new TimeSplitter<>(start, end, unit, function);
    }

    /**
     * @param count 分割后的时间段的数量
     */
    public void calculate(int count) {
        Duration splitTime = Duration.between(start, end).dividedBy(count);
        for (int i = 0; i <= count; i++) {
            LocalDateTime time = start.plus(splitTime.multipliedBy(i));
            timeMap.put(time, function.apply(time));
        }
    }

    /**
     * 计算分割后的时间段，最后一个时间段可能会小于duration
     *
     * @param duration 每个分割后的时间段
     */
    public void calculate(Duration duration) {
        for (LocalDateTime time = start; !time.isAfter(end); time = time.plus(duration)) {
            timeMap.put(time, function.apply(time));
        }
    }

    public void calculate() {
        calculate(Duration.from(unit.getDuration()));
    }


    public T getNear(LocalDateTime time) {
        if (timeMap.isEmpty()) {
            calculate();
        }
        if (inRange(time)) {
            Map.Entry<LocalDateTime, T> floorEntry = timeMap.floorEntry(time);
            Map.Entry<LocalDateTime, T> ceilingEntry = timeMap.ceilingEntry(time);
            if (unit.between(floorEntry.getKey(), time) < unit.between(time, ceilingEntry.getKey())) {
                return ceilingEntry.getValue();
            } else {
                return floorEntry.getValue();
            }
        }
        return null;
    }
    public T get (LocalDateTime time ){
        if (timeMap.isEmpty()) {
            calculate();
        }
        if (inRange(time)) {
            return timeMap.ceilingEntry(time).getValue();
        }
        return null;
    }

    public ArrayList<T> getFromPeriod(LocalDateTime start, LocalDateTime end) {
        if (timeMap.isEmpty()) {
            calculate();
        }
        ArrayList<T> arrayList = new ArrayList<>();
        if (inRange(start) && inRange(end) && !start.isAfter(end)) {
            Map.Entry<LocalDateTime, T> floorEntry = timeMap.floorEntry(start);
            Map.Entry<LocalDateTime, T> ceilingEntry = timeMap.ceilingEntry(end);
            for (Map.Entry<LocalDateTime, T> entry : timeMap.subMap(floorEntry.getKey(), true, ceilingEntry.getKey(), true).entrySet()) {
                arrayList.add(entry.getValue());
            }
        }
        return arrayList;
    }


    public ArrayList<T> getFromPeriod(LocalDateTime start, Duration duration) {
        LocalDateTime end = start.plus(duration);
        return getFromPeriod(start, end);
    }

    public ArrayList<LocalDateTime> getTimeFromPeriod(LocalDateTime start,LocalDateTime end){
        if (timeMap.isEmpty()) {
            calculate();
        }
        ArrayList<LocalDateTime> arrayList = new ArrayList<>();
        if (inRange(start) && inRange(end) && !start.isAfter(end)) {
            Map.Entry<LocalDateTime, T> floorEntry = timeMap.floorEntry(start);
            Map.Entry<LocalDateTime, T> ceilingEntry = timeMap.ceilingEntry(end);
            for (Map.Entry<LocalDateTime, T> entry : timeMap.subMap(floorEntry.getKey(), true, ceilingEntry.getKey(), true).entrySet()) {
                arrayList.add(entry.getKey());
            }
        }
        return arrayList;
    }

    public ArrayList<LocalDateTime> getTimeFromPeriod(LocalDateTime start, Duration duration) {
        LocalDateTime end = start.plus(duration);
        return getTimeFromPeriod(start, end);
    }

    public Set<Map.Entry<LocalDateTime, T>> getAll() {
        if (timeMap.isEmpty()) {
            calculate();
        }
        return timeMap.entrySet();
    }

    public Set<LocalDateTime> getAllTime() {
        if (timeMap.isEmpty()) {
            calculate();
        }
        return timeMap.keySet();
    }

    /**
     * @return 返回所有value的集合，重复的value只会出现一次
     */
    public Set<T> getAllValue() {
        if (timeMap.isEmpty()) {
            calculate();
        }
        return new HashSet<>(timeMap.values());
    }

    private boolean inRange(LocalDateTime time) {
        return time.isAfter(start) && !time.isAfter(end);
    }
}
