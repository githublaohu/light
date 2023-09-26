package com.lamp.light.common.time;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StopWatchMillisTest {

    StopWatchMillis stopWatchMillis;

    @Before
    public void before() {
        stopWatchMillis = new StopWatchMillis();
    }

    @Test
    public void testStart() {
        stopWatchMillis.start();
        Assert.assertTrue(stopWatchMillis.start > 0);
    }

    @Test
    public void testEnd() {
        stopWatchMillis.start();
        Assert.assertTrue(stopWatchMillis.end() > 0);
        Assert.assertTrue(stopWatchMillis.duration > 0);
    }

    @Test
    public void testEndOutToConsole() {
        stopWatchMillis.start();
        stopWatchMillis.endOutToConsole();
    }
}
