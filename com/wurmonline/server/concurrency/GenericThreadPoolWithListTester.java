// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.concurrency;

import java.util.List;
import java.util.ArrayList;

public class GenericThreadPoolWithListTester
{
    public static void main(final String[] args) {
        try {
            Thread.sleep(1000L);
        }
        catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        final long lLastID = 10000L;
        final List<Pollable> lInputList = new ArrayList<Pollable>(10010);
        for (long j = 0L; j < 10000L; ++j) {
            lInputList.add(new Pollable() {
                @Override
                public void poll(final long now) {
                    Thread.yield();
                }
            });
        }
        for (int lNumberOfTasks = 1; lNumberOfTasks < 50; ++lNumberOfTasks) {
            GenericThreadPoolWithList.multiThreadedPoll(lInputList, lNumberOfTasks);
        }
    }
}
