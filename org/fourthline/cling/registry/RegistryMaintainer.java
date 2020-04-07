// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.registry;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RegistryMaintainer implements Runnable
{
    private static Logger log;
    private final RegistryImpl registry;
    private final int sleepIntervalMillis;
    private volatile boolean stopped;
    
    public RegistryMaintainer(final RegistryImpl registry, final int sleepIntervalMillis) {
        this.stopped = false;
        this.registry = registry;
        this.sleepIntervalMillis = sleepIntervalMillis;
    }
    
    public void stop() {
        if (RegistryMaintainer.log.isLoggable(Level.FINE)) {
            RegistryMaintainer.log.fine("Setting stopped status on thread");
        }
        this.stopped = true;
    }
    
    @Override
    public void run() {
        this.stopped = false;
        if (RegistryMaintainer.log.isLoggable(Level.FINE)) {
            RegistryMaintainer.log.fine("Running registry maintenance loop every milliseconds: " + this.sleepIntervalMillis);
        }
        while (!this.stopped) {
            try {
                this.registry.maintain();
                Thread.sleep(this.sleepIntervalMillis);
            }
            catch (InterruptedException ex) {
                this.stopped = true;
            }
        }
        RegistryMaintainer.log.fine("Stopped status on thread received, ending maintenance loop");
    }
    
    static {
        RegistryMaintainer.log = Logger.getLogger(RegistryMaintainer.class.getName());
    }
}
