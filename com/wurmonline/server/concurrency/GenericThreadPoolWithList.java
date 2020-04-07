// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Callable;
import java.util.Collection;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.logging.Logger;

public class GenericThreadPoolWithList
{
    private static Logger logger;
    public static final String VERSION = "$Revision: 1.0 $";
    
    public static void multiThreadedPoll(final List<? extends Pollable> lInputList, final int aNumberOfTasks) {
        System.out.println("Polling banks");
        final ExecutorService execSvc = Executors.newCachedThreadPool();
        final int lLastID = lInputList.size();
        int lFirstID = 0;
        final List toRun = new ArrayList();
        for (int lNumberOfTasks = Math.min(aNumberOfTasks, lInputList.size()), i = 1; i <= aNumberOfTasks && lNumberOfTasks <= i; ++i) {
            final int m = lLastID * i / aNumberOfTasks;
            if (GenericThreadPoolWithList.logger.isLoggable(Level.FINEST)) {
                GenericThreadPoolWithList.logger.log(Level.FINEST, i + " - First: " + lFirstID + ", last: " + m);
            }
            toRun.add(new GenericPollerWithList(lFirstID, m, lInputList));
            System.out.println("ADDED A TASK");
            lFirstID = m + 1;
        }
        final long start = System.nanoTime();
        try {
            execSvc.invokeAll((Collection<? extends Callable<Object>>)toRun);
            if (GenericThreadPoolWithList.logger.isLoggable(Level.FINEST)) {
                GenericThreadPoolWithList.logger.log(Level.FINEST, "invokeAll took " + (System.nanoTime() - start) / 1000000.0f + "ms");
            }
        }
        catch (InterruptedException e) {
            GenericThreadPoolWithList.logger.log(Level.WARNING, "task invocation interrupted", e);
        }
        catch (RejectedExecutionException e2) {
            if (!execSvc.isShutdown()) {
                GenericThreadPoolWithList.logger.log(Level.WARNING, "task submission rejected", e2);
            }
        }
        execSvc.shutdown();
        if (execSvc instanceof ThreadPoolExecutor) {
            final ThreadPoolExecutor tpe = (ThreadPoolExecutor)execSvc;
            if (GenericThreadPoolWithList.logger.isLoggable(Level.FINE)) {
                GenericThreadPoolWithList.logger.log(Level.FINE, "ThreadPoolExecutor CorePoolSize: " + tpe.getCorePoolSize() + ", LargestPoolSize: " + tpe.getLargestPoolSize() + ", TaskCount: " + tpe.getTaskCount());
            }
        }
        if (GenericThreadPoolWithList.logger.isLoggable(Level.FINEST)) {
            GenericThreadPoolWithList.logger.log(Level.FINEST, "execSvc.isTerminated(): " + execSvc.isTerminated() + " took: " + (System.nanoTime() - start) / 1000000.0f + "ms");
        }
        try {
            if (!execSvc.awaitTermination(30L, TimeUnit.SECONDS)) {
                GenericThreadPoolWithList.logger.log(Level.WARNING, "ThreadPoolExceutor timed out instead of terminating");
            }
        }
        catch (InterruptedException e) {
            GenericThreadPoolWithList.logger.log(Level.WARNING, "task awaitTermination interrupted", e);
        }
        if (GenericThreadPoolWithList.logger.isLoggable(Level.FINEST)) {
            GenericThreadPoolWithList.logger.log(Level.FINEST, "execSvc.isTerminated(): " + execSvc.isTerminated() + " took: " + (System.nanoTime() - start) / 1000000.0f + "ms");
        }
    }
    
    static {
        GenericThreadPoolWithList.logger = Logger.getLogger(GenericThreadPoolWithList.class.getName());
    }
}
