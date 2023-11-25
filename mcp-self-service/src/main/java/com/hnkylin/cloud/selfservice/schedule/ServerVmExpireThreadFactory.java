package com.hnkylin.cloud.selfservice.schedule;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerVmExpireThreadFactory implements ThreadFactory {

    private final AtomicInteger mThreadNum = new AtomicInteger(1);

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "serverVmExpireTask-" + mThreadNum.getAndIncrement());
        System.out.println(t.getName() + " has been created");
        return t;
    }
}
