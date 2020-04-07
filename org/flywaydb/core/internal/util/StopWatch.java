// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util;

import java.util.concurrent.TimeUnit;

public class StopWatch
{
    private long start;
    private long stop;
    
    public void start() {
        this.start = System.nanoTime();
    }
    
    public void stop() {
        this.stop = System.nanoTime();
    }
    
    public long getTotalTimeMillis() {
        return TimeUnit.NANOSECONDS.toMillis(this.stop - this.start);
    }
}
