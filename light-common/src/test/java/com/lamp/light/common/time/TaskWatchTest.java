package com.lamp.light.common.time;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TaskWatchTest {

    private TaskWatch test01, test02;

    @Before
    public void before() {
        test01 = new TaskWatch(TaskWatch.Method.MillisInsert);
        test02 = new TaskWatch(TaskWatch.Method.NanoAccess);
        test01.clear();
    }
    @Test
    public void basicTest1() throws InterruptedException {
        long start = test01.startTask("test01");
        Thread.sleep(5);
        long end = test01.endTask("test01");
        Assert.assertNotEquals(0, start);
        Assert.assertNotEquals(-1, end);
        Assert.assertTrue(end > start);
        test01.printAllTask();
    }

    @Test
    public void complexTest1() throws InterruptedException {
        test01.startTask("test01");
        test01.startTask("test02");
        Thread.sleep(5);
        test01.endTask("test01");
        test01.startTask("test02");
        test01.printAllTask();
        test01.printOverview();
        System.out.println(test01.allTaskDuration());
    }

    @Test
    public void accessOrderTest1() throws InterruptedException {
        test02.startTask("test01");
        test02.startTask("test02");
        test02.endTask("test02");
        test02.startTask("test03");
        test02.endTask("test01");
        test02.printAllTask();
        test02.printOverview();
    }
}



