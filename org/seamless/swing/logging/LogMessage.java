// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class LogMessage
{
    private Level level;
    private Long createdOn;
    private String thread;
    private String source;
    private String message;
    
    public LogMessage(final String message) {
        this(Level.INFO, message);
    }
    
    public LogMessage(final String source, final String message) {
        this(Level.INFO, source, message);
    }
    
    public LogMessage(final Level level, final String message) {
        this(level, null, message);
    }
    
    public LogMessage(final Level level, final String source, final String message) {
        this.createdOn = new Date().getTime();
        this.thread = Thread.currentThread().getName();
        this.level = level;
        this.source = source;
        this.message = message;
    }
    
    public Level getLevel() {
        return this.level;
    }
    
    public Long getCreatedOn() {
        return this.createdOn;
    }
    
    public String getThread() {
        return this.thread;
    }
    
    public String getSource() {
        return this.source;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public String toString() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        return this.getLevel() + " - " + dateFormat.format(new Date(this.getCreatedOn())) + " - " + this.getThread() + " : " + this.getSource() + " : " + this.getMessage();
    }
}
