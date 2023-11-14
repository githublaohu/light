package com.lamp.light.common.time;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Set;

public class TimeSplitterTest {
    private TimeSplitter timeSplitter;
    @Before
    public void setUp()  {
        timeSplitter = TimeSplitter.create(
                LocalDateTime.of(2023, 1, 1, 0, 0, 0),
                LocalDateTime.of(2023, 1, 1, 6, 0, 0),
                ChronoUnit.HOURS,
                LocalDateTime::getHour
        );
    }

    @Test
    public void testNum() {
        timeSplitter.calculate(6);
        Assert.assertEquals(7,timeSplitter.getAllTime().size());
        System.out.println(timeSplitter.getAllTime());

    }
    @Test
    public void testDuration() {
        timeSplitter.calculate(Duration.ofMinutes(30));
        Assert.assertEquals(13,timeSplitter.getAllTime().size());
        System.out.println(timeSplitter.getAllValue());
    }
    @Test
    public void testDefault() {
        timeSplitter.calculate();
        Assert.assertEquals(7,timeSplitter.getAllTime().size());
        System.out.println(timeSplitter.getAll());
    }
    @Test
    public void testNear() {
        timeSplitter.calculate();
        Object result = timeSplitter.getNear(LocalDateTime.of(2023, 1, 1, 3, 29, 0));
        System.out.println(result);
        Assert.assertEquals(3,result);
    }
    @Test
    public void testLower(){
        timeSplitter.calculate();
        Object result = timeSplitter.get(LocalDateTime.of(2023, 1, 1, 3, 29, 0));
        Assert.assertEquals(3,result);
    }
    @Test
    public void testPeriod(){
        timeSplitter.calculate(Duration.ofMinutes(30));
        System.out.println(timeSplitter.getFromPeriod(LocalDateTime.of(2023, 1, 1, 3, 25,0),Duration.ofMinutes(75)));
        System.out.println(timeSplitter.getTimeFromPeriod(LocalDateTime.of(2023, 1, 1, 3, 25,0),Duration.ofMinutes(75)));
        Assert.assertEquals(5,timeSplitter.getFromPeriod(LocalDateTime.of(2023, 1, 1, 3, 25,0),Duration.ofMinutes(75)).size());
    }
}
