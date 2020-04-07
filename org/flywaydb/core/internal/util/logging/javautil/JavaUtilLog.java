// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.logging.javautil;

import java.util.logging.LogRecord;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.flywaydb.core.internal.util.logging.Log;

public class JavaUtilLog implements Log
{
    private final Logger logger;
    
    public JavaUtilLog(final Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public void debug(final String message) {
        this.log(Level.FINE, message, null);
    }
    
    @Override
    public void info(final String message) {
        this.log(Level.INFO, message, null);
    }
    
    @Override
    public void warn(final String message) {
        this.log(Level.WARNING, message, null);
    }
    
    @Override
    public void error(final String message) {
        this.log(Level.SEVERE, message, null);
    }
    
    @Override
    public void error(final String message, final Exception e) {
        this.log(Level.SEVERE, message, e);
    }
    
    private void log(final Level level, final String message, final Exception e) {
        final LogRecord record = new LogRecord(level, message);
        record.setLoggerName(this.logger.getName());
        record.setThrown(e);
        record.setSourceClassName(this.logger.getName());
        record.setSourceMethodName(this.getMethodName());
        this.logger.log(record);
    }
    
    private String getMethodName() {
        final StackTraceElement[] stackTrace;
        final StackTraceElement[] steArray = stackTrace = new Throwable().getStackTrace();
        for (final StackTraceElement stackTraceElement : stackTrace) {
            if (this.logger.getName().equals(stackTraceElement.getClassName())) {
                return stackTraceElement.getMethodName();
            }
        }
        return null;
    }
}
