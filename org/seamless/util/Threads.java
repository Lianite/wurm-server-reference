// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util;

import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.lang.management.ManagementFactory;

public class Threads
{
    public static ThreadGroup getRootThreadGroup() {
        ThreadGroup tg;
        ThreadGroup ptg;
        for (tg = Thread.currentThread().getThreadGroup(); (ptg = tg.getParent()) != null; tg = ptg) {}
        return tg;
    }
    
    public static Thread[] getAllThreads() {
        final ThreadGroup root = getRootThreadGroup();
        final ThreadMXBean thbean = ManagementFactory.getThreadMXBean();
        int nAlloc = thbean.getThreadCount();
        int n = 0;
        Thread[] threads;
        do {
            nAlloc *= 2;
            threads = new Thread[nAlloc];
            n = root.enumerate(threads, true);
        } while (n == nAlloc);
        return Arrays.copyOf(threads, n);
    }
    
    public static Thread getThread(final long id) {
        final Thread[] arr$;
        final Thread[] threads = arr$ = getAllThreads();
        for (final Thread thread : arr$) {
            if (thread.getId() == id) {
                return thread;
            }
        }
        return null;
    }
}
