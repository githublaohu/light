package com.lamp.light.common.time;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateFormat {
    public static String CN_format(LocalDateTime time) {
        return time.getYear() + "年" + time.getMonthValue() + "月" + time.getDayOfMonth() + "日" + time.getHour() + ":" + time.getMinute() + ":" + time.getSecond();
    }

    public static String CN_format(LocalDate time) {
        return time.getYear() + "年" + time.getMonthValue() + "月" + time.getDayOfMonth() + "日";
    }

    public static String CN_format(LocalTime time) {
        return time.getHour() + ":" + time.getMinute() + ":" + time.getSecond();
    }

    public static String CN_format(Date date) {
        return CN_format(TimeTrunk.toLocal(date));
    }
}
