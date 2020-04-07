// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing.logging;

import java.util.Arrays;
import java.util.logging.LogRecord;
import java.util.logging.Handler;

public abstract class LoggingHandler extends Handler
{
    public int sourcePathElements;
    
    public LoggingHandler() {
        this.sourcePathElements = 3;
    }
    
    public LoggingHandler(final int sourcePathElements) {
        this.sourcePathElements = 3;
        this.sourcePathElements = sourcePathElements;
    }
    
    public void publish(final LogRecord logRecord) {
        final LogMessage logMessage = new LogMessage(logRecord.getLevel(), this.getSource(logRecord), logRecord.getMessage());
        this.log(logMessage);
    }
    
    public void flush() {
    }
    
    public void close() throws SecurityException {
    }
    
    protected String getSource(final LogRecord record) {
        final StringBuilder sb = new StringBuilder(180);
        String[] split = record.getSourceClassName().split("\\.");
        if (split.length > this.sourcePathElements) {
            split = Arrays.copyOfRange(split, split.length - this.sourcePathElements, split.length);
        }
        for (final String s : split) {
            sb.append(s).append(".");
        }
        sb.append(record.getSourceMethodName());
        return sb.toString();
    }
    
    protected abstract void log(final LogMessage p0);
}
