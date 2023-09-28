package com.lamp.light.common.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.sql.Date;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class TimeTrunk {
    public enum TrunkMethod {
        LOWER, UPPER, NEAREST
    }


    public static LocalDateTime trunkTime(LocalDateTime time, TrunkMethod method, ChronoUnit unit) {
        if (method == TrunkMethod.LOWER)
            return trunkTimeLower(time, method, unit);
        else if (method == TrunkMethod.UPPER)
            return trunkTimeUpper(time, method, unit);
        else
            return trunkTimeNearest(time, method, unit);
    }

    /**
     * 为了简洁，复用了trunkTime方法
     * @param time
     * @param method
     * @param unit
     * @return 返回截取后的LocalDate对象
     */
    public static LocalDate trunkDate(LocalDateTime time, TrunkMethod method, ChronoUnit unit) {
        if (unit.compareTo(ChronoUnit.DAYS) <0 )
            throw new IllegalArgumentException("unit must be greater than or equal to days");
        else
        return trunkTime(time, method, unit).toLocalDate();
    }

    public static LocalDateTime trunkTime(java.sql.Date date, TrunkMethod method, ChronoUnit unit) {
        return trunkTime(toLocal(date), method, unit);
    }
    public static LocalDateTime trunkTime(long timestamp, TrunkMethod method, ChronoUnit unit) {
        return trunkTime(LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.systemDefault().getRules().getOffset(LocalDateTime.now())), method, unit);
    }
    public static LocalDate trunkDate(java.sql.Date date, TrunkMethod method, ChronoUnit unit) {
        return trunkDate(toLocal(date), method, unit);
    }
    public static LocalDate trunkDate(long timestamp, TrunkMethod method, ChronoUnit unit) {
        return trunkDate(LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.systemDefault().getRules().getOffset(LocalDateTime.now())), method, unit);
    }


    /**
     * @param time
     * @param unit
     * @param unit
     * @return
     */
    private static LocalDateTime trunkTimeLower(LocalDateTime time, TrunkMethod method, ChronoUnit unit) {
        if (unit.compareTo(ChronoUnit.YEARS) > 0)
            throw new IllegalArgumentException("unit must be less than or equal to years");
        else {
            // 根据unit的精度，截取time的时间。对于某一个时间精度如果unit单位大，保留当前的时间数据，如果unit单位小，将当前的时间数据置为最小值
            return LocalDateTime.of(
                    time.getYear(),
                    (unit.compareTo(ChronoUnit.MONTHS) <= 0 ? time.getMonthValue() : 1),
                    (unit.compareTo(ChronoUnit.DAYS) <= 0 ? time.getDayOfMonth() : 1),
                    (unit.compareTo(ChronoUnit.HOURS) <= 0 ? time.getHour() : 0),
                    (unit.compareTo(ChronoUnit.MINUTES) <= 0 ? time.getMinute() : 0),
                    (unit.compareTo(ChronoUnit.SECONDS) <= 0 ? time.getSecond() : 0),
                    (unit == ChronoUnit.MILLIS ? time.getNano()/1000_000 * 1000_000 : 0)
            );
        }
    }

    private static LocalDateTime trunkTimeUpper(LocalDateTime time, TrunkMethod method, ChronoUnit unit) {
        return trunkTimeLower(time, method, unit).plus(1, unit).minusNanos(1);
    }
    private static LocalDateTime trunkTimeNearest(LocalDateTime time, TrunkMethod method, ChronoUnit unit) {
        LocalDateTime lower = trunkTimeLower(time, method, unit);
        LocalDateTime upper = trunkTimeUpper(time, method, unit);
        if (unit.between(time, lower) < unit.between(time, upper))
            return upper;
        else
            return lower;
    }

    private static LocalDateTime toLocal(java.sql.Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
