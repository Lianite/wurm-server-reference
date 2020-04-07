// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.logging.console;

import org.flywaydb.core.internal.util.logging.Log;

public class ConsoleLog implements Log
{
    private final Level level;
    
    public ConsoleLog(final Level level) {
        this.level = level;
    }
    
    @Override
    public void debug(final String message) {
        if (this.level == Level.DEBUG) {
            System.out.println("DEBUG: " + message);
        }
    }
    
    @Override
    public void info(final String message) {
        if (this.level.compareTo(Level.INFO) <= 0) {
            System.out.println(message);
        }
    }
    
    @Override
    public void warn(final String message) {
        System.out.println("WARNING: " + message);
    }
    
    @Override
    public void error(final String message) {
        System.err.println("ERROR: " + message);
    }
    
    @Override
    public void error(final String message, final Exception e) {
        System.err.println("ERROR: " + message);
        e.printStackTrace(System.err);
    }
    
    public enum Level
    {
        DEBUG, 
        INFO, 
        WARN;
    }
}
