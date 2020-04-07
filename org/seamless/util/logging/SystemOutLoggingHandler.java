// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util.logging;

import java.text.DateFormat;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.logging.LogRecord;
import java.util.logging.Level;
import java.util.logging.Formatter;
import java.io.OutputStream;
import java.util.logging.StreamHandler;

public class SystemOutLoggingHandler extends StreamHandler
{
    public SystemOutLoggingHandler() {
        super(System.out, new SimpleFormatter());
        this.setLevel(Level.ALL);
    }
    
    public void close() {
        this.flush();
    }
    
    public void publish(final LogRecord record) {
        super.publish(record);
        this.flush();
    }
    
    public static class SimpleFormatter extends Formatter
    {
        public String format(final LogRecord record) {
            final StringBuffer buf = new StringBuffer(180);
            final DateFormat dateFormat = new SimpleDateFormat("kk:mm:ss,SS");
            buf.append("[").append(this.pad(Thread.currentThread().getName(), 32)).append("] ");
            buf.append(this.pad(record.getLevel().toString(), 12));
            buf.append(" - ");
            buf.append(this.pad(dateFormat.format(new Date(record.getMillis())), 24));
            buf.append(" - ");
            buf.append(this.toClassString(record.getSourceClassName(), 30));
            buf.append('#');
            buf.append(record.getSourceMethodName());
            buf.append(": ");
            buf.append(this.formatMessage(record));
            buf.append("\n");
            final Throwable throwable = record.getThrown();
            if (throwable != null) {
                final StringWriter sink = new StringWriter();
                throwable.printStackTrace(new PrintWriter(sink, true));
                buf.append(sink.toString());
            }
            return buf.toString();
        }
        
        public String pad(String s, final int size) {
            if (s.length() < size) {
                for (int i = s.length(); i < size - s.length(); s += " ", ++i) {}
            }
            return s;
        }
        
        public String toClassString(final String name, final int maxLength) {
            return (name.length() > maxLength) ? name.substring(name.length() - maxLength) : name;
        }
    }
}
