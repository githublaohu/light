package com.lamp.light.common.time;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TaskWatchTest {

    private TaskWatch taskWatch;

    @Before
    public void before() {
        taskWatch = new TaskWatch(false);
    }
    @Test
    public void basicTest() throws InterruptedException {
        taskWatch.clear();
        long start = taskWatch.startTask("test01");
        Thread.sleep(5);
        long end = taskWatch.endTask("test01");
        Assert.assertNotEquals(0, start);
        Assert.assertNotEquals(-1, end);
        Assert.assertTrue(end > start);
        taskWatch.printAllTaskOrderByStart();
    }

    @Test
    public void complexTest() throws InterruptedException {
        taskWatch.startTask("test01");
        taskWatch.startTask("test02");
        taskWatch.endTask("test01");
        taskWatch.startTask("test02");
        taskWatch.printAllTaskOrderByStart();
    }
}



