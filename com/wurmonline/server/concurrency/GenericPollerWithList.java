// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.concurrency;

import com.wurmonline.server.banks.Bank;
import java.util.logging.Level;
import java.util.List;
import java.util.logging.Logger;
import java.util.concurrent.Callable;

public class GenericPollerWithList<V> implements Callable
{
    private static Logger logger;
    private int iFirstID;
    private int iLastID;
    private List<? extends Pollable> iTaskList;
    
    public GenericPollerWithList(final int aFirstID, final int aLastID, final List<? extends Pollable> aTaskList) {
        if (GenericPollerWithList.logger.isLoggable(Level.FINEST)) {
            GenericPollerWithList.logger.entering(GenericPollerWithList.class.getName(), "GenericPollerWithList()", new Object[] { aFirstID, aLastID, aTaskList });
        }
        if (aTaskList != null) {
            this.iTaskList = aTaskList;
            if (aFirstID < 0) {
                this.iFirstID = 0;
            }
            else {
                this.iFirstID = aFirstID;
            }
            if (aLastID < this.iFirstID) {
                this.iLastID = this.iFirstID;
            }
            else if (aLastID > aTaskList.size()) {
                this.iLastID = aTaskList.size();
            }
            else {
                this.iLastID = aLastID;
            }
            return;
        }
        throw new IllegalArgumentException("GenericPollerWithList TaskList argument must not be null");
    }
    
    @Override
    public Long call() throws Exception {
        if (GenericPollerWithList.logger.isLoggable(Level.FINEST)) {
            GenericPollerWithList.logger.entering(GenericPollerWithList.class.getName(), "call()");
        }
        final long start = System.nanoTime();
        System.out.println("TASK CALLED");
        for (int i = this.iFirstID; i < this.iLastID; ++i) {
            final Pollable lTask = (Pollable)this.iTaskList.get(i);
            if (lTask != null && lTask instanceof Bank) {
                ((Bank)lTask).poll(System.currentTimeMillis());
            }
            else {
                GenericPollerWithList.logger.log(Level.WARNING, "Unsupported Pollable Class: " + lTask);
            }
        }
        if (GenericPollerWithList.logger.isLoggable(Level.FINEST)) {
            GenericPollerWithList.logger.log(Level.FINEST, "Tasks from " + this.iFirstID + " to " + this.iLastID + " took " + (System.nanoTime() - start) / 1000000.0f + "ms");
        }
        return System.nanoTime() - start;
    }
    
    @Override
    public String toString() {
        return "GenericPollerWithList + First ID: " + this.iFirstID + ", Last ID: " + this.iLastID + ", Number of Tasks: " + this.iTaskList.size();
    }
    
    static {
        GenericPollerWithList.logger = Logger.getLogger(GenericPollerWithList.class.getName());
    }
}
