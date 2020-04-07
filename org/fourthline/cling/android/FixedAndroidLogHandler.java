// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.android;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import android.util.Log;
import java.util.logging.LogRecord;
import java.util.logging.Formatter;
import java.util.logging.Handler;

public class FixedAndroidLogHandler extends Handler
{
    private static final Formatter THE_FORMATTER;
    
    public FixedAndroidLogHandler() {
        this.setFormatter(FixedAndroidLogHandler.THE_FORMATTER);
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public void flush() {
    }
    
    @Override
    public void publish(final LogRecord record) {
        try {
            final int level = getAndroidLevel(record.getLevel());
            String tag = record.getLoggerName();
            if (tag == null) {
                tag = "null";
            }
            else {
                final int length = tag.length();
                if (length > 23) {
                    final int lastPeriod = tag.lastIndexOf(".");
                    if (length - lastPeriod - 1 <= 23) {
                        tag = tag.substring(lastPeriod + 1);
                    }
                    else {
                        tag = tag.substring(tag.length() - 23);
                    }
                }
            }
            final String message = this.getFormatter().format(record);
            Log.println(level, tag, message);
        }
        catch (RuntimeException e) {
            Log.e("AndroidHandler", "Error logging message.", (Throwable)e);
        }
    }
    
    static int getAndroidLevel(final Level level) {
        final int value = level.intValue();
        if (value >= 1000) {
            return 6;
        }
        if (value >= 900) {
            return 5;
        }
        if (value >= 800) {
            return 4;
        }
        return 3;
    }
    
    static {
        THE_FORMATTER = new Formatter() {
            @Override
            public String format(final LogRecord r) {
                final Throwable thrown = r.getThrown();
                if (thrown != null) {
                    final StringWriter sw = new StringWriter();
                    final PrintWriter pw = new PrintWriter(sw);
                    sw.write(r.getMessage());
                    sw.write("\n");
                    thrown.printStackTrace(pw);
                    pw.flush();
                    return sw.toString();
                }
                return r.getMessage();
            }
        };
    }
}
