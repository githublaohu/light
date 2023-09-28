package com.lamp.light.common.time;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class TimeTrunkTest {

    private LocalDateTime time1;

    @Before
    public void before() {
        time1 = LocalDateTime.of(2023, 5, 6, 12, 36, 34, 123456789);
    }

    // correctness test

    @Test
    public void milliLower() {
        time1 = TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.LOWER, ChronoUnit.MILLIS);
        Assert.assertEquals(LocalDateTime.of(2023, 5, 6, 12, 36, 34, 123000000), time1);
    }
    @Test
    public void secondLower() {
        time1 = TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.LOWER, ChronoUnit.SECONDS);
        Assert.assertEquals(LocalDateTime.of(2023, 5, 6, 12, 36, 34, 0), time1);
    }
    @Test
    public void minutelower() {
        time1 = TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.LOWER, ChronoUnit.MINUTES);
        Assert.assertEquals(LocalDateTime.of(2023, 5, 6, 12, 36, 0, 0), time1);
    }
    @Test
    public void hourLower() {
        time1 = TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.LOWER, ChronoUnit.HOURS);
        Assert.assertEquals(LocalDateTime.of(2023, 5, 6, 12, 0, 0, 0), time1);
    }
    @Test
    public void dayLower() {
        time1 = TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.LOWER, ChronoUnit.DAYS);
        Assert.assertEquals(LocalDateTime.of(2023, 5, 6, 0, 0, 0, 0), time1);
    }
    @Test
    public void monthLower() {
        time1 = TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.LOWER, ChronoUnit.MONTHS);
        Assert.assertEquals(LocalDateTime.of(2023, 5, 1, 0, 0, 0, 0), time1);
    }
    @Test
    public void yearLower() {
        time1 = TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.LOWER, ChronoUnit.YEARS);
        Assert.assertEquals(LocalDateTime.of(2023, 1, 1, 0, 0, 0, 0), time1);
    }


    @Test
    public void secondUpper() {
        time1 = TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.UPPER, ChronoUnit.SECONDS);
        Assert.assertEquals(LocalDateTime.of(2023, 5, 6, 12, 36, 34, 999999999), time1);
    }
    @Test
    public void minuteUpper() {
        time1 = TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.UPPER, ChronoUnit.MINUTES);
        Assert.assertEquals(LocalDateTime.of(2023, 5, 6, 12, 36, 59, 999999999), time1);
    }
    @Test
    public void hourUpper() {
        time1 = TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.UPPER, ChronoUnit.HOURS);
        Assert.assertEquals(LocalDateTime.of(2023, 5, 6, 12, 59, 59, 999999999), time1);
    }
    @Test
    public void dayUpper() {
        time1 = TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.UPPER, ChronoUnit.DAYS);
        Assert.assertEquals(LocalDateTime.of(2023, 5, 6, 23, 59, 59, 999999999), time1);
    }
    @Test
    public void monthUpper() {
        time1 = TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.UPPER, ChronoUnit.MONTHS);
        Assert.assertEquals(LocalDateTime.of(2023, 5, 31, 23, 59, 59, 999999999), time1);
    }
    @Test
    public void yearUpper() {
        time1 = TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.UPPER, ChronoUnit.YEARS);
        Assert.assertEquals(LocalDateTime.of(2023, 12, 31, 23, 59, 59, 999999999), time1);
    }

    //time1 = LocalDateTime.of(2023, 5, 6, 12, 36, 34, 123456789);
    @Test
    public void secondNear() {
        time1 = TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.NEAREST, ChronoUnit.SECONDS);
        Assert.assertEquals(TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.LOWER, ChronoUnit.SECONDS),
                TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.NEAREST, ChronoUnit.SECONDS));
    }
    @Test
    public void minuteNear() {
        time1 = TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.NEAREST, ChronoUnit.MINUTES);
        Assert.assertEquals(TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.NEAREST, ChronoUnit.MINUTES),
                TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.UPPER, ChronoUnit.MINUTES));
    }
    @Test
    public void hourNear() {
        time1 = TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.NEAREST, ChronoUnit.HOURS);
        Assert.assertEquals(TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.NEAREST, ChronoUnit.HOURS),
                TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.UPPER, ChronoUnit.HOURS));
    }
    @Test
    public void dayNear() {
        time1 = TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.NEAREST, ChronoUnit.DAYS);
        Assert.assertEquals(TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.NEAREST, ChronoUnit.DAYS),
                TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.UPPER, ChronoUnit.DAYS));
    }
    @Test
    public void monthNear() {
        time1 = TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.NEAREST, ChronoUnit.MONTHS);
        Assert.assertEquals(TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.NEAREST, ChronoUnit.MONTHS),
                TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.UPPER, ChronoUnit.MONTHS));
    }
    @Test
    public void yearNear() {
        time1 = TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.NEAREST, ChronoUnit.YEARS);
        Assert.assertEquals(TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.NEAREST, ChronoUnit.YEARS),
                TimeTrunk.trunkTime(time1, TimeTrunk.TrunkMethod.UPPER, ChronoUnit.YEARS));
    }


    // Date api test
    @Test
    public void dateTest(){
        java.sql.Date date = new java.sql.Date(1683302400000L);
        System.out.println(date);
        Assert.assertEquals(TimeTrunk.trunkTime(date, TimeTrunk.TrunkMethod.LOWER, ChronoUnit.MINUTES), LocalDateTime.of(2023, 5, 6, 12, 36, 0, 0));
    }
    @Test
    public void stampTest(){
        long stamp = time1.toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(LocalDateTime.now()));
        Assert.assertEquals(TimeTrunk.trunkTime(stamp, TimeTrunk.TrunkMethod.LOWER, ChronoUnit.MINUTES), LocalDateTime.of(2023, 5, 6, 12, 36, 0, 0));
    }
    @Test
    public void toLocalDateTest(){
        LocalDate date = TimeTrunk.trunkDate(time1, TimeTrunk.TrunkMethod.LOWER, ChronoUnit.DAYS);
        Assert.assertEquals(date, LocalDate.of(2023, 5, 6));
    }

}
